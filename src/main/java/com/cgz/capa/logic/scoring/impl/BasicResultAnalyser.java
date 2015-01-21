package com.cgz.capa.logic.scoring.impl;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.scoring.interfaces.ResultAnalyser;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by czarek on 14/01/15.
 * TODO tests
 */
public class BasicResultAnalyser implements ResultAnalyser {

    Logger logger = LoggerFactory.getLogger(BasicResultAnalyser.class);

    RiskScoreFactory riskScoreFactory;

    public BasicResultAnalyser(RiskScoreFactory riskScoreFactory) {
        this.riskScoreFactory = riskScoreFactory;
    }


    @Override
    public RiskScore analise(List<Pair<RiskScore, AlgorithmStep>> results) throws AlgorithmException {
        int finalScore = 0;
        StringBuilder finalMessageStringBuilder = new StringBuilder();
        for (Pair<RiskScore, AlgorithmStep> result : results) {
            RiskScore riskScore = result.getLeft();
            if (riskScore != null) {
                finalScore += riskScore.getScore();
                if(StringUtils.isNotEmpty(riskScore.getMessage())) {
                    finalMessageStringBuilder.append( result.getRight().getClass().getName()).append(System.getProperty("line.separator"));
                    finalMessageStringBuilder.append(riskScore.getMessage()).append(System.getProperty("line.separator"));
                    finalMessageStringBuilder.append(System.getProperty("line.separator"));
                }
            }

            logger.warn("Algorithm step " + result.getRight().getClass() + " scored: " + riskScore);
        }

            RiskScore finalRS = riskScoreFactory.createRiskScoreWithMessage(finalScore, finalMessageStringBuilder.toString());
            logger.warn("Cumulative score: " + finalRS);
            return finalRS;


    }
}
