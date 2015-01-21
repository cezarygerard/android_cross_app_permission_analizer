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


    public static final long MAX_RETRIES_WHEN_DOWNLOADING = 0;
    //Added, so google wont block our calls
    public static final long SLEEP_TIME_ON_FAILED_DOWNLOAD_IN_MILIS = 30000;
    //Added, so google wont block our calls
    public static final long SLEEP_TIME_ON_SUCCESS_DOWNLOAD_IN_MILIS = 1000;
    @Autowired
    private List<AlgorithmStep> algorithmSteps;

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
    public List<Pair<RiskScore, AlgorithmStep>> execute(String investigatedPackageName, List<String> investigatedPackagePermissions, List<AlgorithmStep> algorithmSteps) throws AlgorithmException {

        AlgorithmDataDTO tuple = prepareDataForAlgorithms(investigatedPackageName, investigatedPackagePermissions);

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

    protected AlgorithmDataDTO prepareDataForAlgorithms(String investigatedPackageName, List<String> investigatedPackagePermissions) throws AlgorithmException {
        AlgorithmDataDTO tuple;
        try {
            List<String> similarAppNames = applicationDescriptionParserService.getSimilarAppsPackageNames(investigatedPackageName);

            Map<String, Set<String>> similarAppsPermissionsFromStore = downloadPermissionsForAll(similarAppNames);
            Set<String> investigateAppPermissionsFromStore = googlePlayCrawlerService.getPermissionsForPackage(investigatedPackageName);

            tuple = new AlgorithmDataDTO(investigatedPackageName, investigatedPackagePermissions, investigateAppPermissionsFromStore, similarAppsPermissionsFromStore);
        } catch (ServiceException e) {
            throw new AlgorithmException(e);
        }
        return tuple;
    }

    protected Map<String, Set<String>> downloadPermissionsForAll(List<String> similarAppNames) {
        Map<String, Set<String>> similarAppsPermissions = new HashMap<>();
        for (String appName : similarAppNames) {
            synchronized (this) {

                Set<String> permSet  = downloadPermissionsInternal(appName, 0);
                if(permSet!= null){
                    similarAppsPermissions.put(appName, permSet);
                } else {
                    logger.error("could not download permissions EVEN AFTER RETRYING");
                }
            }
        }
        return similarAppsPermissions;
    }

    public Set<String> downloadPermissionsInternal(String appName, int tries){
        Set<String> permSet = null;
        try {
           permSet = googlePlayCrawlerService.getPermissionsForPackage(appName);
            logger.info("Permission for package" + appName + " : " + permSet);
        } catch (ServiceException e) {
            logger.error("could not download permissions for package, " +  appName +"   tried:  " + (tries + 1) + "  times" , e);
        }

        if(tries > MAX_RETRIES_WHEN_DOWNLOADING){
            return null;
        }

        if(permSet == null){

            sleep(SLEEP_TIME_ON_FAILED_DOWNLOAD_IN_MILIS);
            return  downloadPermissionsInternal(appName, tries + 1);
        }


        sleep(SLEEP_TIME_ON_SUCCESS_DOWNLOAD_IN_MILIS);
        return permSet;
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("ops! ",e);
        }
    }


}
