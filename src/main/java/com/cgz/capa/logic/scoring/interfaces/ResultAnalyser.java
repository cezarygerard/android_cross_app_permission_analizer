package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by czarek on 14/01/15.
 */
public interface ResultAnalyser {
    RiskScore analise(List<Pair<RiskScore, AlgorithmStep>> results) throws AlgorithmException;
}
