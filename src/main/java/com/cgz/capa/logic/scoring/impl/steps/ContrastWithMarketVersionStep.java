package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.model.enums.ProtectionLevel;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by czarek on 14/01/15.
 */
//TODO extract common code with other classes implementing AlgorithmStep
@Component
public class ContrastWithMarketVersionStep implements AlgorithmStep {

    private Logger logger = LoggerFactory.getLogger(ContrastWithMarketVersionStep.class);

    @Autowired
    protected RiskScoreFactory riskScoreFactory;

    @Autowired
    protected SystemPermissionsInfoService permissionsInfoService;

    @Autowired
    protected GooglePlayCrawlerService googlePlayCrawlerService;

    @Value(value = "${contrastWithMarket.newPermission.flag.costs.money}")
    protected int newPermCostsMoney;

    @Value(value = "${contrastWithMarket.newPermission.protectionLevel.normal}")
    protected int newPermProtectionLevelNormal;

    @Value(value = "${contrastWithMarket.newPermission.protectionLevel.dangerous}")
    protected int newPermProtectionLevelDangerous;

    @Value(value = "${contrastWithMarket.newPermission.protectionLevel.signature}")
    protected int newPermProtectionLevelSignature;

    @Value(value = "${contrastWithMarket.newPermission.protectionLevel.signatureOrSystem}")
    protected int newPermProtectionLevelSignatureOrSystem;

    @Value(value = "${contrastWithMarket.newPermission.permissionGroup.flag.personalInfo}")
    protected int newPermUsesPersonalInfo;

    @Value(value = "${contrastWithMarket.appNotFoundInMarket}")
    protected int appNotFoundScore;


    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {
        int scoreValue = 0;
        StringBuilder stringBuilder = new StringBuilder();

        Set<String> permissionsFromStore = downloadPermissionsFromStore(algorithmDataDTO);
        ;
        if (permissionsFromStore == null) {
            try {
                return riskScoreFactory.createRiskScoreWithMessage(appNotFoundScore, "App was not found in store");
            } catch (ServiceException e) {
                logger.error("Creating RiskScore failed", e);
                throw new AlgorithmException("Creating RiskScore failed", e);
            }
        }
        for (String permissionName : algorithmDataDTO.getManifestPermissions()) {
            Permission permission = permissionsInfoService.getPermission(permissionName);
            if (permission == null) {
                //not a system permission
                continue;
            }
            if (!permissionsFromStore.contains(permissionName)) {
                scoreValue += evaluateScoreForNewPermission(permission);
                stringBuilder.append(permissionName).append(" , ");
            }
        }

        return prepareResult(scoreValue, stringBuilder);
    }

    private RiskScore prepareResult(int scoreValue, StringBuilder stringBuilder) throws AlgorithmException {

        String permissions = stringBuilder.toString();
        try {
            if (StringUtils.isNotEmpty(permissions)) {
                return riskScoreFactory.createRiskScoreWithMessage(scoreValue, "App uses some more permissions than declared: " + permissions);
            } else {
                return riskScoreFactory.createRiskScore(scoreValue);
            }

        } catch (ServiceException e) {
            logger.error("Creating RiskScore failed", e);
            throw new AlgorithmException("Creating RiskScore failed", e);
        }
    }

    protected int evaluateScoreForNewPermission(Permission permission) {
        int scoreValueTmp = 0;
        scoreValueTmp += permissionsInfoService.costsMoney(permission) ? newPermCostsMoney : 0;
        scoreValueTmp += protectionLevelScore(permission);
        scoreValueTmp += permissionsInfoService.usesPersonalInfo(permission) ? newPermUsesPersonalInfo : 0;
        return scoreValueTmp;
    }

    //TODO usunac to obrzydliwe kopiowanie kodu
    protected int protectionLevelScore(Permission permission) {
        if (ProtectionLevel.NORMAL.equals(permission.getProtectionLevel())) {
            return newPermProtectionLevelNormal;
        }
        if (ProtectionLevel.DANGEROUS.equals(permission.getProtectionLevel())) {
            return newPermProtectionLevelDangerous;
        }
        if (ProtectionLevel.SIGNATURE.equals(permission.getProtectionLevel())) {
            return newPermProtectionLevelSignature;
        }
        if (ProtectionLevel.SIGNATURE_OR_SYSTEM.equals(permission.getProtectionLevel())) {
            return newPermProtectionLevelSignatureOrSystem;
        }
        return 0;
    }

    private Set<String> downloadPermissionsFromStore(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {

        try {
            return googlePlayCrawlerService.getPermissionsForPackage(algorithmDataDTO.getName());
        } catch (ServiceException e) {
            logger.warn("Didn't find package in Google Play", e);

        }

        return null;
    }

}
