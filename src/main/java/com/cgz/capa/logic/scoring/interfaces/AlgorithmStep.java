package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmTuple;

/**
 * Created by czarek on 14/01/15.
 */
public interface AlgorithmStep {

    RiskScore executeStep(AlgorithmTuple algorithmTuple);
}
