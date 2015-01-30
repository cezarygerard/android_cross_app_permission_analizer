package com.cgz.capa.logic.services;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by czarek on 27/01/15.
 *  * TODO implement offline (persistance) cache, so it will be possible to rerun the same analisis offline (with new params)
 */
@Service
public class AlgorithmDataProviderService {

    Logger logger = LoggerFactory.getLogger(AlgorithmDataProviderService.class);

    @Value("${algorithmDataProviderService.maxRetriesWhenDownloadingAppPermission}")
    public   long maxRetriesWhenDownloadingAppPermission = 0;
    @Value("${algorithmDataProviderService.sleepTimeOnFailedDownloadInMilliseconds}")
    public   long sleepTimeOnFailedDownloadInMilliseconds = 30000;
    @Value("${algorithmDataProviderService.sleepTimeOnSuccessfulDownloadInMilliseconds}")
    public   long sleepTimeOnSuccessfulDownloadInMilliseconds = 1000;

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
        AlgorithmDataDTO tuple;
        try {
            List<String> similarAppNames = applicationDescriptionParserService.getSimilarAppsPackageNames(investigatedPackageName);

            Map<String, List<String>> similarAppsPermissionsFromStore = downloadPermissionsForAll(similarAppNames);
            List<String> investigateAppPermissionsFromStore = googlePlayCrawlerService.getPermissionsForPackage(investigatedPackageName);

            List<String>deduplicateInvestigatedPackagePermissions = removeDuplicates(investigatedPackagePermissions);
            tuple = new AlgorithmDataDTO(investigatedPackageName, deduplicateInvestigatedPackagePermissions, investigateAppPermissionsFromStore, similarAppsPermissionsFromStore);
        } catch (ServiceException e) {
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

                List<String> ermissionsList  = downloadPermissionsInternal(appName, 0);
                if(ermissionsList!= null){
                    similarAppsPermissions.put(appName, ermissionsList);
                } else {
                    logger.error("could not download permissions EVEN AFTER RETRYING");
                }
            }
        }
        return similarAppsPermissions;
    }

    public List<String> downloadPermissionsInternal(String appName, int tries){
        List<String> permissions = null;
        try {
            permissions = googlePlayCrawlerService.getPermissionsForPackage(appName);
            logger.info("Permission for package" + appName + " : " + permissions);
        } catch (ServiceException e) {
            logger.error("could not download permissions for package, " +  appName +"   tried:  " + (tries + 1) + "  times" , e);
        }

        //TODO consider moving this retry logic into  googlePlayCrawlerService

        if(tries > maxRetriesWhenDownloadingAppPermission){
            return null;
        }

        if(permissions == null){

            sleep(sleepTimeOnFailedDownloadInMilliseconds);
            return  downloadPermissionsInternal(appName, tries + 1);
        }


        sleep(sleepTimeOnSuccessfulDownloadInMilliseconds);
        return permissions;
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("ops! ",e);
        }
    }

}
