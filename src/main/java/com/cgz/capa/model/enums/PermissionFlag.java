package com.cgz.capa.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
//TODO extract common code from enums in com.cgz.capa.model.enums;
public enum PermissionFlag {

    COSTS_MONEY("costsMoney");

    private String name;
    private static Map<String, PermissionFlag> valuesMap = new HashMap<String, PermissionFlag>();


    private PermissionFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PermissionFlag getEnumValueByName(String name) {
        return valuesMap.get(name);
    }

    static {
        for (PermissionFlag enumVal : PermissionFlag.values()) {
            valuesMap.put(enumVal.name, enumVal);
        }
    }
}
