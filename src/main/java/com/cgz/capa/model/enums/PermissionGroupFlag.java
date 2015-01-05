package com.cgz.capa.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
//TODO extract common code from enums in com.cgz.capa.model.enums;
public enum PermissionGroupFlag {

    PERSONAL_INFO("personalInfo");

    private String name;
    private static Map<String, PermissionGroupFlag> valuesMap = new HashMap<String, PermissionGroupFlag>();

    private PermissionGroupFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PermissionGroupFlag getEnumValueByName(String name) {
        return valuesMap.get(name);
    }

    static {
        for (PermissionGroupFlag enumVal : PermissionGroupFlag.values()) {
            valuesMap.put(enumVal.name, enumVal);
        }
    }
}
