package com.cgz.capa.logic.scoring.impl;


import com.cgz.capa.exceptions.AlgorithmErrorException;
import com.cgz.capa.exceptions.ServiceErrorException;
import com.cgz.capa.logic.scoring.interfaces.PackageAnalysisAlgorithm;
import com.cgz.capa.logic.services.ApplicationDescriptionParserService;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.RiskScore;
import com.google.protobuf.ServiceException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by czarek on 11/01/15.
 */
public class BasicAlgorithm implements PackageAnalysisAlgorithm  {


    protected ApplicationDescriptionParserService applicationDescriptionParserService;
    protected GooglePlayCrawlerService googlePlayCrawlerService;
    protected RiskScoreFactory riskScoreFactory;
    protected SystemPermissionsInfoService systemPermissionsInfoService;

    public BasicAlgorithm(ApplicationDescriptionParserService applicationDescriptionParserService, GooglePlayCrawlerService googlePlayCrawlerService, RiskScoreFactory riskScoreFactory, SystemPermissionsInfoService systemPermissionsInfoService) {
        //TODO check no null. Necessary when not in spring.
        this.applicationDescriptionParserService = applicationDescriptionParserService;
        this.googlePlayCrawlerService = googlePlayCrawlerService;
        this.riskScoreFactory = riskScoreFactory;
        this.systemPermissionsInfoService = systemPermissionsInfoService;
    }


    @Override
    public RiskScore executeAnalysis(String investigatedPackageName, List<String> investigatedPackagePermissions) throws AlgorithmErrorException {

        try {
            List<String> similarAppNames = applicationDescriptionParserService.getSimilarAppsPackageNames(investigatedPackageName);

            Map<String,List<String>> similarAppsPermissions = downloadPermissionsForAll(similarAppNames);

            Pair<Integer, String> result = coreAlgorithm(investigatedPackageName,investigatedPackagePermissions);

            return riskScoreFactory.createRiskScoreWithMessage(0, "TEST");
        } catch (ServiceErrorException  e) {
            throw new AlgorithmErrorException(e);
        }
    }

    protected Map<String,List<String>> downloadPermissionsForAll(List<String> similarAppNames) {
        for (String appName :similarAppNames) {
           synchronized (this){
               try {
                   List l = googlePlayCrawlerService.getPermissionsForPackage(appName);
                   System.out.println(l);
               } catch (ServiceErrorException e) {
                   System.out.println("could not download permissions for package " + appName);
                   continue;
               }
           }
        }

        return null;
    }

    protected Pair<Integer, String> coreAlgorithm(String investigatedPackageName, List<String> investigatedPackagePermissions) {
        return null;
    }
}
