package com.cgz.capa.model.enums;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
//TODO extract common code from enums in com.cgz.capa.model.enums;
public enum PermissionGroupFlag {

    PERSONAL_INFO("personalInfo"),
    NONE("");

    private String name;
    private static Map<String, PermissionGroupFlag> valuesMap = new Hashtable<String, PermissionGroupFlag>();

    private PermissionGroupFlag(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PermissionGroupFlag getEnumValueByName(String name){
        if(name==null){
            return NONE;
        }

        PermissionGroupFlag retVal = valuesMap.get(name);
        if(retVal == null){
            retVal = NONE;
        }
        return retVal;
    }

    static{
        for(PermissionGroupFlag enumVal : PermissionGroupFlag.values()){
            valuesMap.put(enumVal.name, enumVal);
        }
    }
}
