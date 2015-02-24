package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by czarek on 14/01/15.
 */
public class ContrastWithMarketVersionStep extends AbstractAlgorithmStep   {

    public static final String MESSAGE_PREFIX = "Comparing ith the same app in market. ";
    private Logger logger = LoggerFactory.getLogger(ContrastWithMarketVersionStep.class);

    protected int appNotFoundScore;

    public ContrastWithMarketVersionStep(final Map<String, Integer> riskScoreMap, final int appNotFoundScore) {
        this.riskScoreMap = riskScoreMap;
        this.appNotFoundScore = appNotFoundScore;
    }

    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {
        int scoreValue = 0;
        StringBuilder stringBuilder = new StringBuilder().append(MESSAGE_PREFIX);

        List<String> permissionsFromStore = algorithmDataDTO.getStorePermissions();
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
                scoreValue += evaluateRisk(permission, stringBuilder);

            }
        }

        return prepareResult(scoreValue, stringBuilder);
    }

    private RiskScore prepareResult(int scoreValue, StringBuilder stringBuilder) throws AlgorithmException {

        String message = stringBuilder.toString();
        if (message.equals(MESSAGE_PREFIX)) {
            return riskScoreFactory.createRiskScoreWithMessage(scoreValue, "App uses no more permissions than its version from store");
        } else {
            return riskScoreFactory.createRiskScoreWithMessage(scoreValue, "App uses some more permissions than are NOT declared in Google Play. " + message);
        }
    }


}
