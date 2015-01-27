package com.cgz.capa.logic.scoring.impl;


import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmExecutor;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.services.*;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by czarek on 11/01/15.
 * TODO tests
 */
public class BasicAlgorithmExecutor implements AlgorithmExecutor {

    Logger logger = LoggerFactory.getLogger(BasicAlgorithmExecutor.class);


    @Autowired
    private List<AlgorithmStep> algorithmSteps;


    @Autowired
    private AlgorithmDataProviderService algorithmDataProviderService;


    @Override
    public List<Pair<RiskScore, AlgorithmStep>> execute(String investigatedPackageName, List<String> investigatedPackagePermissions, List<AlgorithmStep> algorithmSteps) throws AlgorithmException {


        AlgorithmDataDTO tuple = algorithmDataProviderService.prepareDataForAlgorithms(investigatedPackageName, investigatedPackagePermissions);

        List<Pair<RiskScore, AlgorithmStep>> resultsList = new ArrayList<Pair<RiskScore, AlgorithmStep>>(algorithmSteps.size());

        for (AlgorithmStep step : algorithmSteps) {
            resultsList.add(Pair.of(step.executeStep(tuple), step));
            logger.warn(step.getClass().toString());
        }

        return resultsList;

    }

    @Override
    public List<Pair<RiskScore, AlgorithmStep>> executeAllSteps(String investigatedPackageName, List<String> investigatedPackagePermissions) throws AlgorithmException {
        return execute(investigatedPackageName, investigatedPackagePermissions, algorithmSteps);
    }



}
