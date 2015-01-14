package com.cgz.capa.logic.scoring.impl;


import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmExecutor;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.services.ApplicationDescriptionParserService;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by czarek on 11/01/15.
 * TODO tests
 */
public class BasicAlgorithmExecutor implements AlgorithmExecutor {


    protected ApplicationDescriptionParserService applicationDescriptionParserService;
    protected GooglePlayCrawlerService googlePlayCrawlerService;
    protected RiskScoreFactory riskScoreFactory;
    protected SystemPermissionsInfoService systemPermissionsInfoService;

    Logger logger = LoggerFactory.getLogger(BasicAlgorithmExecutor.class);


    public BasicAlgorithmExecutor(ApplicationDescriptionParserService applicationDescriptionParserService, GooglePlayCrawlerService googlePlayCrawlerService, RiskScoreFactory riskScoreFactory, SystemPermissionsInfoService systemPermissionsInfoService) {
        //TODO check no null. Necessary when not in spring.
        this.applicationDescriptionParserService = applicationDescriptionParserService;
        this.googlePlayCrawlerService = googlePlayCrawlerService;
        this.riskScoreFactory = riskScoreFactory;
        this.systemPermissionsInfoService = systemPermissionsInfoService;
    }


    @Override
    public List<RiskScore> executeAnalysis(String investigatedPackageName, List<String> investigatedPackagePermissions, List<AlgorithmStep> algorithmSteps) throws AlgorithmException {

        AlgorithmTuple tuple = prepareDataForAlgorithms(investigatedPackageName, investigatedPackagePermissions);

        List<RiskScore> resultsList= new ArrayList<RiskScore>(algorithmSteps.size());

        for (AlgorithmStep step : algorithmSteps) {
            resultsList.add(step.executeStep(tuple));
        }
        return resultsList;

    }

    private AlgorithmTuple prepareDataForAlgorithms(String investigatedPackageName, List<String> investigatedPackagePermissions) throws AlgorithmException {
        AlgorithmTuple tuple;
        try {
            List<String> similarAppNames = applicationDescriptionParserService.getSimilarAppsPackageNames(investigatedPackageName);

            Map<String,List<String>> similarAppsPermissionsFromStore = downloadPermissionsForAll(similarAppNames);
            List<String> investigateAppPermissionsFromStore = googlePlayCrawlerService.getPermissionsForPackage(investigatedPackageName);

            tuple = new AlgorithmTuple(investigatedPackageName, investigatedPackagePermissions, investigateAppPermissionsFromStore,  similarAppsPermissionsFromStore);
        } catch (ServiceException e) {
            throw new AlgorithmException(e);
        }
        return tuple;
    }

    protected Map<String,List<String>> downloadPermissionsForAll(List<String> similarAppNames) {
        Map<String,List<String>> similarAppsPermissions = new HashMap<>();
        for (String appName :similarAppNames) {
           synchronized (this){
               try {
                   List<String> permList = googlePlayCrawlerService.getPermissionsForPackage(appName);
                   similarAppsPermissions.put(appName, permList);
                   logger.info("Permission for package" + appName + " : " + permList) ;
               } catch (ServiceException e) {
                   logger.info("could not download permissions for package " + appName) ;
               }
           }
        }
        return similarAppsPermissions;
    }



}
