package com.cgz.capa.logic.services;

import com.cgz.capa.exceptions.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
 * Created by czarek on 28/01/15.
 */
@Service
public class OfflineCacheService {

    Logger logger = LoggerFactory.getLogger(AlgorithmDataProviderService.class);

    public <T> void cacheAll(String cacheName, T cache)   {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(cacheName + ".json"), cache);

        } catch (Exception e) {
            logger.error("Could not write cache", e);
        }
    }

    public <T> T readCache(String cacheName, Class<T> type )   {
        ObjectMapper mapper = new ObjectMapper();
        T result = null;

        try {
             result = mapper.readValue(new File(cacheName + ".json"), type);
        } catch (IOException e) {
           return null;
        }

        return result;
    }




}
