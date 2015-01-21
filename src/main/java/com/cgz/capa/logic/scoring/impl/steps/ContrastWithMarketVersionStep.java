package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Created by czarek on 14/01/15.
 */
public class ContrastWithMarketVersionStep extends AbstractStep implements AlgorithmStep {

    private Logger logger = LoggerFactory.getLogger(ContrastWithMarketVersionStep.class);

    protected int appNotFoundScore;

    protected Map<String, Integer> riskScoreMap;

    public ContrastWithMarketVersionStep(final Map<String, Integer> riskScoreMap, final int appNotFoundScore) {
        this.riskScoreMap = riskScoreMap;
        this.appNotFoundScore = appNotFoundScore;
    }

    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {
        int scoreValue = 0;
        StringBuilder stringBuilder = new StringBuilder().append("Comparing ith the same app in market. ");

        Set<String> permissionsFromStore = downloadPermissionsFromStore(algorithmDataDTO);
        ;
        if (permissionsFromStore == null) {
                return riskScoreFactory.createRiskScoreWithMessage(appNotFoundScore, "App was not found in store");
        }

        for (String permissionName : algorithmDataDTO.getManifestPermissions()) {
            Permission permission = permissionsInfoService.getPermission(permissionName);
            if (permission == null) {
                //not a system permission
                continue;
            }
            if (!permissionsFromStore.contains(permissionName)) {
                stringBuilder.append(permissionName);
                scoreValue += evaluateRisk(permission, riskScoreMap, stringBuilder);

            }
        }

        return prepareResult(scoreValue, stringBuilder);
    }

    private RiskScore prepareResult(int scoreValue, StringBuilder stringBuilder) throws AlgorithmException {

        String message = stringBuilder.toString();
        if (scoreValue==0) {
            return riskScoreFactory.createRiskScore(scoreValue);
        } else {
            return riskScoreFactory.createRiskScoreWithMessage(scoreValue, "App uses some more permissions than are NOT declared in Google Play. " + message);
        }
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
