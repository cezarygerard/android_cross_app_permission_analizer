package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.model.enums.ProtectionLevel;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by czarek on 14/01/15.
 */
//TODO extract common code with other classes implementing AlgorithmStep
@Component
public class ScoreAppPermissionsStep implements AlgorithmStep {

    private Logger logger = LoggerFactory.getLogger(ScoreAppPermissionsStep.class);

    @Autowired
    protected RiskScoreFactory riskScoreFactory;

    @Autowired
    SystemPermissionsInfoService permissionsInfoService;

    //TODO make it more generic, so when new protecion level appears thewe will be no need to code here.
    // e.g.there could be an injectable map of protecion levels and it's scores, flags and it's scores, group and it's scores

    @Value(value = "${scoreAppPermissions.permission.flag.costs.money}")
    protected int permCostsMoney;

    @Value(value = "${scoreAppPermissions.permission.protectionLevel.normal}")
    protected int permProtectionLevelNormal;

    @Value(value = "${scoreAppPermissions.permission.protectionLevel.dangerous}")
    protected int permProtectionLevelDangerous;

    @Value(value = "${scoreAppPermissions.permission.protectionLevel.signature}")
    protected int permProtectionLevelSignature;

    @Value(value = "${scoreAppPermissions.permission.protectionLevel.signatureOrSystem}")
    protected int permProtectionLevelSignatureOrSystem;

    @Value(value = "${scoreAppPermissionsStep.permissionGroup.flag.personalInfo}")
    protected int permUsesPersonalInfo;

    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {
        int scoreValue = 0;
        for (String permissionName : algorithmDataDTO.getManifestPermissions()) {
            Permission permission = permissionsInfoService.getPermission(permissionName);
            if (permission == null) {
                //not a system permission
                continue;
            }
            scoreValue += permissionsInfoService.costsMoney(permission) ? permCostsMoney : 0;
            scoreValue += protectionLevelScore(permission);
            scoreValue += permissionsInfoService.usesPersonalInfo(permission) ? permUsesPersonalInfo : 0;
        }
        try {
            return riskScoreFactory.createRiskScore(scoreValue);
        } catch (ServiceException e) {
            logger.error("Creating RiskScore failed", e);
            throw new AlgorithmException("Creating RiskScore failed", e);
        }
    }


    //TODO usunac to obrzydliwe kopiowanie kodu
    protected int protectionLevelScore(Permission permission) {
        if (ProtectionLevel.NORMAL.equals(permission.getProtectionLevel())) {
            return permProtectionLevelNormal;
        }
        if (ProtectionLevel.DANGEROUS.equals(permission.getProtectionLevel())) {
            return permProtectionLevelDangerous;
        }
        if (ProtectionLevel.SIGNATURE.equals(permission.getProtectionLevel())) {
            return permProtectionLevelSignature;
        }
        if (ProtectionLevel.SIGNATURE_OR_SYSTEM.equals(permission.getProtectionLevel())) {
            return permProtectionLevelSignatureOrSystem;
        }
        return 0;
    }


}
