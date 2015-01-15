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

import java.util.*;

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
    public List<Pair<RiskScore, AlgorithmStep>> executeAnalysis(String investigatedPackageName, List<String> investigatedPackagePermissions, List<AlgorithmStep> algorithmSteps) throws AlgorithmException {

        AlgorithmDataDTO tuple = prepareDataForAlgorithms(investigatedPackageName, investigatedPackagePermissions);

        List<Pair<RiskScore, AlgorithmStep>> resultsList = new ArrayList<Pair<RiskScore, AlgorithmStep>>(algorithmSteps.size());

        for (AlgorithmStep step : algorithmSteps) {
            resultsList.add(Pair.of(step.executeStep(tuple), step));
            logger.warn(step.getClass().toString());
        }

        return resultsList;

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
                try {
                    Set<String> permSet = googlePlayCrawlerService.getPermissionsForPackage(appName);
                    similarAppsPermissions.put(appName, permSet);
                    logger.info("Permission for package" + appName + " : " + permSet);
                } catch (ServiceException e) {
                    logger.info("could not download permissions for package " + appName);
                }
            }
        }
        return similarAppsPermissions;
    }


}
