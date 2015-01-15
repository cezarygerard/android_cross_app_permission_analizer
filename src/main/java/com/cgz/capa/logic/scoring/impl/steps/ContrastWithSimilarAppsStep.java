package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by czarek on 14/01/15.
 */
@Component
public class ContrastWithSimilarAppsStep implements AlgorithmStep {


    @Autowired
    RiskScoreFactory riskScoreFactory;

    @Autowired
    SystemPermissionsInfoService permissionInfo;

    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) {
        return null;
    }

}
