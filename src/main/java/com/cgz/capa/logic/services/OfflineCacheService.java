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


    String cacheDir = "";//System.getProperty("java.io.tmpdir");;

    Logger logger = LoggerFactory.getLogger(AlgorithmDataProviderService.class);

    public <T> void cacheAll(String cacheName, T cache)   {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(cacheDir + File.separator + cacheName + ".json"), cache);

        } catch (Exception e) {
            logger.error("Could not write cache", e);
        }
    }

    public <T> T readCache(String cacheName, Class<T> type )   {
        ObjectMapper mapper = new ObjectMapper();
        T result = null;

        try {
             result = mapper.readValue(new File(cacheDir + File.separator + cacheName + ".json"), type);
        } catch (IOException e) {
           return null;
        }

        return result;
    }

}
