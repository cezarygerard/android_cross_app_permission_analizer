package com.cgz.capa.logic.scoring.impl;

import com.cgz.capa.logic.scoring.impl.steps.ContrastWithMarketVersionStep;
import com.cgz.capa.logic.scoring.impl.steps.ContrastWithSimilarAppsStep;
import com.cgz.capa.logic.scoring.impl.steps.ScoreAppPermissionsStep;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class WeightedResultAnalyserTest {

    @Autowired
    private WeightedResultAnalyser resultAnalyser;

    @Autowired
    ContrastWithMarketVersionStep contrastWithMarketVersionStep;

    @Autowired
    @Qualifier("ScoreAppPermissionsStep")
    ScoreAppPermissionsStep scoreAppPermissionsStep;

    @Autowired
    @Qualifier("ContrastWithSimilarAppsStep")
    ContrastWithSimilarAppsStep contrastWithSimilarAppsStep;

    @Autowired
    RiskScoreFactory riskScoreFactory;

    Map<AlgorithmStep, Double> weights;

    @Before
    public void setup(){
        weights = new Hashtable<>();
        weights.put(scoreAppPermissionsStep,0.0);
        weights.put(contrastWithMarketVersionStep,1.0);
        weights.put(contrastWithSimilarAppsStep,10.0);
        resultAnalyser.setWeights(weights);
    }

    @Test
    public void testExecuteAnalysisForOneResult() throws Exception {

        Pair p = Pair.of(riskScoreFactory.createRiskScore(1000), scoreAppPermissionsStep);
        assertEquals(0.0, resultAnalyser.executeAnalysisForOneResult(0, p),0);

        p = Pair.of(riskScoreFactory.createRiskScore(1000), contrastWithMarketVersionStep);
        assertEquals(1000.0, resultAnalyser.executeAnalysisForOneResult(0, p),0);

        p = Pair.of(riskScoreFactory.createRiskScore(100), contrastWithSimilarAppsStep);
        assertEquals(1000.0, resultAnalyser.executeAnalysisForOneResult(0, p),0);

    }

}
