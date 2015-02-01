package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by czarek on 14/01/15.
 */
public class ScoreAppPermissionsStep extends AbstractAlgorithmStep   {

    private Logger logger = LoggerFactory.getLogger(ScoreAppPermissionsStep.class);

    public ScoreAppPermissionsStep(final Map<String, Integer> riskScoreMap) {
        this.riskScoreMap = riskScoreMap;
    }

    protected ScoreAppPermissionsStep(){
        super();
    }

    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {
        int scoreValue = 0;

        StringBuilder messageBuilder = new StringBuilder().append("Scoring app permissions. ");

        for (String permissionName : algorithmDataDTO.getManifestPermissions()) {
            Permission permission = permissionsInfoService.getPermission(permissionName);
            if (permission == null) {
                //not a system permission
                continue;
            }
            scoreValue += evaluateRisk(permission, messageBuilder);
        }

        return riskScoreFactory.createRiskScoreWithMessage(scoreValue, messageBuilder.toString());

    }


}
