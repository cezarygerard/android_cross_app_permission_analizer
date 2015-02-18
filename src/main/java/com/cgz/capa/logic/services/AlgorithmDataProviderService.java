package com.cgz.capa.logic.services;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by czarek on 27/01/15.
 *  * TODO implement offline (persistance) cache, so it will be possible to rerun the same analisis offline (with new params)
 */
@Service
public class AlgorithmDataProviderService  {

    Logger logger = LoggerFactory.getLogger(AlgorithmDataProviderService.class);

    @Autowired
    private OfflineCacheService offlineCacheService;

    Set<String> cacheOfErrors = new HashSet<>();


    @PostConstruct
    public void setup() throws Exception {
        Set<String> cashed = offlineCacheService.readCache(this.getClass().getSimpleName(), cacheOfErrors.getClass());
        if(cashed!=null) {
            cacheOfErrors.addAll(cashed);
        }
    }

    protected void storeInCache(String value) throws ServiceException {
        boolean newValue  = cacheOfErrors.add(value);
        if(newValue) {
            offlineCacheService.cacheAll(this.getClass().getSimpleName(), cacheOfErrors);
        }
    }

    @Autowired
    protected ApplicationDescriptionParserService applicationDescriptionParserService;
    @Autowired
    protected GooglePlayCrawlerService googlePlayCrawlerService;
    @Autowired
    protected RiskScoreFactory riskScoreFactory;
    @Autowired
    protected SystemPermissionsInfoService systemPermissionsInfoService;

    //TODO ake sure that there are no duplicates on the permissions lists
    public AlgorithmDataDTO prepareDataForAlgorithms(String investigatedPackageName, List<String> investigatedPackagePermissions) throws AlgorithmException {
        if(cacheOfErrors.contains(investigatedPackageName)){
            //TODO turn it into return, do not throw exception as it is somewhat expected
            throw new AlgorithmException("package " + investigatedPackageName + " is known to cause errors");
        }

        AlgorithmDataDTO tuple;
        try {
            List<String> similarAppNames = applicationDescriptionParserService.getSimilarAppsPackageNames(investigatedPackageName);

            Map<String, List<String>> similarAppsPermissionsFromStore = downloadPermissionsForAll(similarAppNames);
            List<String> investigateAppPermissionsFromStore = googlePlayCrawlerService.getPermissionsForPackage(investigatedPackageName);

            List<String>deduplicateInvestigatedPackagePermissions = removeDuplicates(investigatedPackagePermissions);
            tuple = new AlgorithmDataDTO(investigatedPackageName, deduplicateInvestigatedPackagePermissions, investigateAppPermissionsFromStore, similarAppsPermissionsFromStore);
        } catch (ServiceException e) {
            try {
                storeInCache(investigatedPackageName);
            } catch (ServiceException e1) {
                throw new AlgorithmException(e);
            }
            throw new AlgorithmException(e);
        }
        return tuple;
    }

    private List<String> removeDuplicates(List<String> investigatedPackagePermissions) {

        return new ArrayList<>(new LinkedHashSet<>(investigatedPackagePermissions));
    }

    protected Map<String, List<String>> downloadPermissionsForAll(List<String> similarAppNames) {
        Map<String, List<String>> similarAppsPermissions = new HashMap<>();
        for (String appName : similarAppNames) {
            synchronized (this) {

                List<String> permissionsList  = null;
                try {
                    permissionsList = googlePlayCrawlerService.getPermissionsForPackage(appName);
                } catch (ServiceException e) {
                    logger.error("could not download permissions", e);
                }

                similarAppsPermissions.put(appName, permissionsList);
            }
        }
        return similarAppsPermissions;
    }



}
