package com.cgz.capa.logic.services;

import com.akdeniz.googleplaycrawler.GooglePlay;
import com.akdeniz.googleplaycrawler.GooglePlayAPI;
import com.cgz.capa.exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by czarek on 05/01/15.
 */
@Service
public class GooglePlayCrawlerService extends AbstractCacheableService {

    Logger logger = LoggerFactory.getLogger(GooglePlayCrawlerService.class);

    @Value("${crawler.maxRetriesWhenDownloadingAppPermission}")
    public   long maxRetriesWhenDownloadingAppPermission = 0;
    @Value("${crawler.sleepTimeOnFailedDownloadInMilliseconds}")
    public   long sleepTimeOnFailedDownloadInMilliseconds = 0;
    @Value("${crawler.sleepTimeOnSuccessfulDownloadInMilliseconds}")
    public   long sleepTimeOnSuccessfulDownloadInMilliseconds = 0;


    private static GooglePlayAPI service;

//    private static Map<String, List<String>> cachedPermissionLists = new ConcurrentHashMap<>();
    private final String password;
    private final String email;

    @Autowired
    public GooglePlayCrawlerService(@Value("${crawler.email}")String email, @Value("${crawler.password}") String password) throws Exception {
        validate(email, password);
        this.email = email;
        this.password = password;
    }


    @PostConstruct
    public void setupService() /*throws Exception */{
        service = new GooglePlayAPI(email, password);
        try {
            service.checkin();
            service.login();
        } catch (Exception e) {
            logger.warn("Could not register GooglePlayCrawlerService", e);
        }
    }

    private void validate(String... args) throws com.google.protobuf.ServiceException {
        for (int i = 0; i < args.length; i++) {
            if (StringUtils.isEmpty(args[i])) {
                throw new com.google.protobuf.ServiceException("prameter must not be empty");
            }
        }
    }

    private List<String> getPermissionsForPackageInternal(String packageName, int retries) throws ServiceException {


        GooglePlay.DetailsResponse details = null;
        try {
            details = service.details(packageName);
        } catch (IOException e) {

            logger.warn("could not download permissions for package, " +  packageName +"   tried:  " + (retries + 1) + "  times" , e);
            if( e.getMessage().contains("Item not found") || retries > maxRetriesWhenDownloadingAppPermission){
                throw new ServiceException("downloading permissions failed for: " + packageName, e);
            }
            doSleep(sleepTimeOnFailedDownloadInMilliseconds);
            return  getPermissionsForPackageInternal(packageName, retries + 1);
        }


        GooglePlay.AppDetails appDetails = details.getDocV2().getDetails().getAppDetails();

        List<String> permissionsList = new ArrayList<>(new LinkedHashSet<>(appDetails.getPermissionList()));

        storeInCache(packageName, permissionsList);
        logger.info("Permission for package" + packageName + " : " + permissionsList);
        doSleep(sleepTimeOnSuccessfulDownloadInMilliseconds);
        return permissionsList;
    }



    public List<String> getPermissionsForPackage(String packageName) throws ServiceException {
        if (cache.containsKey(packageName)) {
            logger.info("Permissions from cache...");
            logger.info("Permissions for package" + packageName + " : " +  cache.get(packageName));
            return cache.get(packageName);
        }
        logger.info("Permission NOT from cache...");
        return getPermissionsForPackageInternal(packageName,0);
    }



//    public List<String> downloadPermissionsInternal(String appName, int tries){
//        List<String> permissions = null;
//        try {
//            permissions = googlePlayCrawlerService.getPermissionsForPackage(appName);
//            logger.info("Permission for package" + appName + " : " + permissions);
//        } catch (ServiceException e) {
//            logger.error("could not download permissions for package, " +  appName +"   tried:  " + (tries + 1) + "  times" , e);
//        }
//
//        if(tries > maxRetriesWhenDownloadingAppPermission){
//            return null;
//        }
//
//        if(permissions == null){
//            sleep(sleepTimeOnFailedDownloadInMilliseconds);
//            return  downloadPermissionsInternal(appName, tries + 1);
//        }
//
//
//        sleep(sleepTimeOnSuccessfulDownloadInMilliseconds);
//        return permissions;
//    }
//
    private void doSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("ops! ",e);
        }
    }
}
