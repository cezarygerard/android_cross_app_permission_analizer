package com.cgz.capa.utils;

import java.util.List;
import java.util.Map;

/**
 * Created by czarek on 14/01/15.
 */
public class AlgorithmTuple {
    private final String name;
    private final List<String> manifestPermissions;
    private final List<String> storePermissions;
    private final Map<String, List<String>> similarAppsPermissions;

    /**
     */
    public AlgorithmTuple(String name, List<String> manifestPermissions, List<String> storePermissions, Map<String, List<String>> similarAppsPermissions) {
        this.name = name;
        this.manifestPermissions = manifestPermissions;
        this.storePermissions = storePermissions;
        this.similarAppsPermissions = similarAppsPermissions;
    }

    public String getName() {
        return name;
    }

    public List<String> getManifestPermissions() {
        return manifestPermissions;
    }

    public List<String> getStorePermissions() {
        return storePermissions;
    }

    public Map<String, List<String>> getSimilarAppsPermissions() {
        return similarAppsPermissions;
    }
}