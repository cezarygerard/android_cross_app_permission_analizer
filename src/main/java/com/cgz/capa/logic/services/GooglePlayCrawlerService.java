package com.cgz.capa.logic.services;

import com.akdeniz.googleplaycrawler.GooglePlay;
import com.akdeniz.googleplaycrawler.GooglePlayAPI;
import com.google.protobuf.ServiceException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.IoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by czarek on 05/01/15.
 */
public class GooglePlayCrawlerService {
    //dla danej nazwy pakietowej pobierz listę uprawnien
    //stateless!

    private static GooglePlayAPI service;

    private static Map<String, List<String>> cachedPermissionLists = new ConcurrentHashMap<>();

    public GooglePlayCrawlerService(String email, String password) throws Exception {
        mustNotBeEmpty(email,password);
        service = new GooglePlayAPI(email, password);
        service.checkin();
        service.login();
    }

    public List<String> getPermissionsForPackage(String packageName) throws ServiceException {

        if(cachedPermissionLists.containsKey(packageName)){
            return cachedPermissionLists.get(packageName);
        }

        GooglePlay.DetailsResponse details = null;
        try {
            details = service.details(packageName);
        } catch (IOException e) {
            throw new ServiceException("downloading permissions dailed for: " + packageName);
        }
        GooglePlay.AppDetails appDetails = details.getDocV2().getDetails().getAppDetails();

        List<String> previous =  cachedPermissionLists.put(packageName, appDetails.getPermissionList());

        if(previous!=null){
            cachedPermissionLists.remove(packageName);
            cachedPermissionLists.put(packageName, appDetails.getPermissionList());
        }

        return appDetails.getPermissionList();
    }

    private void mustNotBeEmpty(String... args) throws ServiceException {
        for (int i = 0; i < args.length; i++) {
            if(StringUtils.isEmpty(args[i])){
                throw new ServiceException("prameter must not be empty");
            }
        }
    }
}
