package com.cgz.capa.logic.services;

import com.akdeniz.googleplaycrawler.GooglePlay;
import com.akdeniz.googleplaycrawler.GooglePlayAPI;
import com.cgz.capa.exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by czarek on 05/01/15.
 */
public class GooglePlayCrawlerService {

    private static GooglePlayAPI service;

    //private static Map<String, Set<String>> cachedPermissionLists = new ConcurrentHashMap<>();
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
    }

    public Set<String> getPermissionsForPackage(String packageName) throws ServiceException {

//        if (cachedPermissionLists.containsKey(packageName)) {
//            return cachedPermissionLists.get(packageName);
//        }

        GooglePlay.DetailsResponse details = null;
        try {
            details = service.details(packageName);
        } catch (IOException e) {
            throw new ServiceException("downloading permissions failed for: " + packageName, e);
        }
        GooglePlay.AppDetails appDetails = details.getDocV2().getDetails().getAppDetails();

        Set<String> permissionsSet = new LinkedHashSet<>(appDetails.getPermissionList());

        //Set<String> previous = cachedPermissionLists.put(packageName, permissionsSet);

//        if (previous != null) {
//            cachedPermissionLists.remove(packageName);
//            cachedPermissionLists.put(packageName, permissionsSet);
//        }

        return permissionsSet;
    }

    private void validate(String... args) throws com.google.protobuf.ServiceException {
        for (int i = 0; i < args.length; i++) {
            if (StringUtils.isEmpty(args[i])) {
                throw new com.google.protobuf.ServiceException("prameter must not be empty");
            }
        }
    }
}
