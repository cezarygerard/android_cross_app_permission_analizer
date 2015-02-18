package com.cgz.capa.logic.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by czarek on 28/01/15.
 */
@Service
public class OfflineCacheService {


    private static final long MAX_SAVE_FREQUENCY = 30 * 1000;
    String cacheDir = "." + File.separator;//System.getProperty("java.io.tmpdir") +  File.separator;;

    Logger logger = LoggerFactory.getLogger(AlgorithmDataProviderService.class);

    public <T> void cacheAll(String cacheName, T cache)   {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(cacheDir  + cacheName + ".json");
            if(System.currentTimeMillis() > file.lastModified() + MAX_SAVE_FREQUENCY) {
                mapper.writeValue(file, cache);
            }

        } catch (Exception e) {
            logger.error("Could not write cache", e);
        }
    }

    public <T> T readCache(String cacheName, Class<T> type )   {
        ObjectMapper mapper = new ObjectMapper();
        T result = null;

        try {
             result = mapper.readValue(new File(cacheDir  + cacheName + ".json"), type);
        } catch (IOException e) {
           return null;
        }

        return result;
    }

}
