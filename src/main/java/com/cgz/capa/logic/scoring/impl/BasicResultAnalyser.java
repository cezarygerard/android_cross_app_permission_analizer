package com.cgz.capa.logic.scoring.impl;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.ResultAnalyser;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.StringUtils;
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

    RiskScoreFactory riskScoreFactory;

    public BasicResultAnalyser(RiskScoreFactory riskScoreFactory) {
        this.riskScoreFactory = riskScoreFactory;
    }

    @Override
    public RiskScore analise(List<RiskScore> scores) throws AlgorithmException {
        int finalScore = 0 ;
        StringBuilder finalMessageStringBuilder = new StringBuilder();
        for (RiskScore riskScore : scores) {
            finalScore+=riskScore.getScore();
            if(StringUtils.isNotEmpty(riskScore.getMessage())) {
                finalMessageStringBuilder.append(riskScore.getMessage()).append("\n");
            }
        }

        try {
            return riskScoreFactory.createRiskScoreWithMessage(finalScore, finalMessageStringBuilder.toString());
        } catch (ServiceException e) {
            logger.error("BasicResultAnalyser: error while creating  RiskScore : " , e);
            throw new AlgorithmException("BasicResultAnalyser: error while creating  RiskScore : " , e);
        }

    }
}
