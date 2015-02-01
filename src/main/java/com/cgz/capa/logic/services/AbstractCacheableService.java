package com.cgz.capa.logic.services;

import com.cgz.capa.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by czarek on 29/01/15.
 */
public abstract class AbstractCacheableService {

    protected Map<String, List<String>> cache = new ConcurrentHashMap<>();

    @Autowired
    private OfflineCacheService offlineCacheService;

    @PostConstruct
    public void setup() throws Exception {
        Map<String, List<String>> cashed = offlineCacheService.readCache(this.getClass().getSimpleName(), cache.getClass());
        if(cashed!=null) {
            cache.putAll(cashed);
        }
    }

    //TODO extrac abstract superclass with GooglePlayCrawlerService;
    protected void storeInCache(String packageName, List<String> data) throws ServiceException {
        List<String> previous  = cache.put(packageName, data);
        if(previous == null) {
            offlineCacheService.cacheAll(this.getClass().getSimpleName(), cache);
        }
    }
}
