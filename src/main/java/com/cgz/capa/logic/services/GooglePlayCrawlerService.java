package com.cgz.capa.logic.services;

import com.akdeniz.googleplaycrawler.GooglePlay;
import com.akdeniz.googleplaycrawler.GooglePlayAPI;
import com.cgz.capa.exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by czarek on 05/01/15.
 */
public class GooglePlayCrawlerService extends AbstractCacheableService {

    Logger logger = LoggerFactory.getLogger(GooglePlayCrawlerService.class);


    @Autowired
    private OfflineCacheService offlineCacheService;

    private static GooglePlayAPI service;

//    private static Map<String, List<String>> cachedPermissionLists = new ConcurrentHashMap<>();
    private final String password;
    private final String email;

    public GooglePlayCrawlerService(String email, String password) throws Exception {
        validate(email, password);
        this.email = email;
        this.password = password;
    }

    @PostConstruct
    public void setup() throws Exception {
        service = new GooglePlayAPI(email, password);
        service.checkin();
        service.login();
//        Object o = cachedPermissionLists.getClass();
//        Map<String, List<String>> cashed = offlineCacheService.readCache(this.getClass().getSimpleName(), cachedPermissionLists.getClass());
//        if(cashed!=null) {
//            cachedPermissionLists.putAll(cashed);
//        }
    }

    public List<String> getPermissionsForPackage(String packageName) throws ServiceException {

        if (cache.containsKey(packageName)) {
            return cache.get(packageName);
        }

        GooglePlay.DetailsResponse details = null;
        try {
            details = service.details(packageName);
        } catch (IOException e) {
            logger.warn("downloading permissions failed for: " + packageName, e);
            throw new ServiceException("downloading permissions failed for: " + packageName, e);
        }
        GooglePlay.AppDetails appDetails = details.getDocV2().getDetails().getAppDetails();


       List<String> permissionsList = new ArrayList<>(new LinkedHashSet<>(appDetails.getPermissionList()));

        storeInCache(packageName, permissionsList);

        return permissionsList;
    }

//    private void storeInCache(String packageName, List<String> permissionsSet) throws ServiceException {
//
//        cachedPermissionLists.put(packageName, permissionsSet);
//
//        offlineCacheService.cacheAll(this.getClass().getSimpleName(),cachedPermissionLists );
//    }

    private void validate(String... args) throws com.google.protobuf.ServiceException {
        for (int i = 0; i < args.length; i++) {
            if (StringUtils.isEmpty(args[i])) {
                throw new com.google.protobuf.ServiceException("prameter must not be empty");
            }
        }
    }
}
