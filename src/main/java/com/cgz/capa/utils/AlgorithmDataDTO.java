package com.cgz.capa.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by czarek on 14/01/15.
 */
public class AlgorithmDataDTO {
    private final String name;
    private final List<String> manifestPermissions;
    private final Set<String> storePermissions;
    private final Map<String, Set<String>> similarAppsPermissions;

    /**
     */
    public AlgorithmDataDTO(String name, List<String> manifestPermissions, Set<String> storePermissions, Map<String, Set<String>> similarAppsPermissions) {
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

    public Set<String> getStorePermissions() {
        return storePermissions;
    }

    public Map<String, Set<String>> getSimilarAppsPermissions() {
        return similarAppsPermissions;
    }
}