package com.cgz.capa.logic.scoring.impl;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.scoring.interfaces.ResultAnalyser;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by czarek on 30/01/15.
 * //TODO TESTS
 */
@Component("WeightedResultAnalyser")
public class WeightedResultAnalyser extends BasicResultAnalyser {

    Logger logger = LoggerFactory.getLogger(WeightedResultAnalyser.class);

    Map<String, Double> weights;

    @Override
    protected double executeAnalysis(double finalScore, Pair<RiskScore, AlgorithmStep> result) {
        double simpleValue = super.executeAnalysis(finalScore, result);
        Double weight = weights.get(result.getRight());
        if(weight!=null){
            return simpleValue *  weight;
        }
        return simpleValue;

    }
}
