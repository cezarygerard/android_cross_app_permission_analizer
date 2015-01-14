package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.model.RiskScore;

import java.util.List;

/**
 * Created by czarek on 14/01/15.
 */
public interface ResultAnalyser {
    RiskScore analise(List<RiskScore> scores) throws AlgorithmException;
}
