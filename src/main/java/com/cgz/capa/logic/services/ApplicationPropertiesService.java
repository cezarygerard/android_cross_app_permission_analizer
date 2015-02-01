package com.cgz.capa.logic.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by czarek on 01/02/15.
 */
@Service
public class ApplicationPropertiesService {

    private NavigableMap<String, String> sortedProperties = new TreeMap<String, String>();
    private Properties originalProperties = new Properties();

    @Autowired
    public ApplicationPropertiesService(AbstractFileResolvingResource applicationPropertiesResourc) throws IOException {

        FileInputStream fis = new FileInputStream(applicationPropertiesResourc.getFile());
        originalProperties.load(fis);
        for (final String name: originalProperties.stringPropertyNames()) {
            sortedProperties.put(name, originalProperties.getProperty(name));
        }
    }

    public SortedMap<String, String> getByPrefix(String prefix ) {
        return sortedProperties.subMap( prefix, prefix + Character.MAX_VALUE );
    }

}

