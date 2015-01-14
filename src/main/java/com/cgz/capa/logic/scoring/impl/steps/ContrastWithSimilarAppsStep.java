package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.springframework.stereotype.Component;

/**
 * Created by czarek on 14/01/15.
 */
@Component
public class ContrastWithSimilarAppsStep implements AlgorithmStep {

    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) {
        return null;
    }

}
