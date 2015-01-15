package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;

/**
 * Created by czarek on 14/01/15.
 */
public interface AlgorithmStep {

    RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException;
}
