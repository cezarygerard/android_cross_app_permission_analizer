package com.cgz.capa.logic.scoring.impl;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.scoring.interfaces.ResultAnalyser;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by czarek on 14/01/15.
 * TODO tests
 */
public class BasicResultAnalyser implements ResultAnalyser {

    Logger logger = LoggerFactory.getLogger(BasicResultAnalyser.class);

    @Autowired
    RiskScoreFactory riskScoreFactory;


    @Override
    public RiskScore analise(List<Pair<RiskScore, AlgorithmStep>> results) throws AlgorithmException {
        double finalScore = 0;
        StringBuilder finalMessageStringBuilder = new StringBuilder();

        for (Pair<RiskScore, AlgorithmStep> result : results) {

            RiskScore riskScore = result.getLeft();
            if (riskScore != null) {
                finalScore += executeAnalysis(finalScore, result);
                appendMessage(finalMessageStringBuilder, result, riskScore);
            }

            logger.warn("Algorithm step " + result.getRight().getClass() + " scored: " + riskScore);
        }

        return prepareFinalRiskScore(finalScore, finalMessageStringBuilder);


    }

    protected double executeAnalysis(double finalScore, Pair<RiskScore, AlgorithmStep> result) {
        return result.getLeft().getScore();
    }

    protected RiskScore prepareFinalRiskScore(double finalScore, StringBuilder finalMessageStringBuilder) {
        RiskScore finalRS = riskScoreFactory.createRiskScoreWithMessage(finalScore, finalMessageStringBuilder.toString());
        logger.warn("Cumulative score: " + finalRS);
        return finalRS;
    }

    protected void appendMessage(StringBuilder finalMessageStringBuilder, Pair<RiskScore, AlgorithmStep> result, RiskScore riskScore) {
        if(StringUtils.isNotEmpty(riskScore.getMessage())) {
            finalMessageStringBuilder.append( result.getRight().getClass().getName()).append(System.getProperty("line.separator"));
            finalMessageStringBuilder.append(riskScore.getMessage()).append(System.getProperty("line.separator"));
            finalMessageStringBuilder.append(System.getProperty("line.separator"));
        }
    }
}
