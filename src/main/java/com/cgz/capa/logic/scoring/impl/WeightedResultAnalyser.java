package com.cgz.capa.logic.scoring.impl;

import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by czarek on 30/01/15.
 * //TODO TESTS
 */

public class WeightedResultAnalyser extends BasicResultAnalyser {

    Logger logger = LoggerFactory.getLogger(WeightedResultAnalyser.class);

    Map<AlgorithmStep, Double> weights = new HashMap<>();

    @Override
    protected double executeAnalysisForOneResult(double finalScore, Pair<RiskScore, AlgorithmStep> result) {
        double simpleValue = super.executeAnalysisForOneResult(finalScore, result);
        Double weight = weights.get(result.getRight());
        if(weight!=null){
            return simpleValue *  weight;
        }
        return simpleValue;

    }


    public void setWeights(Map<AlgorithmStep, Double> weights) {
        this.weights = weights;
    }
}
