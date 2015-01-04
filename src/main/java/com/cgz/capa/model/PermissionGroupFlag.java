package com.cgz.capa.model;

/**
 * Created by czarek on 04/01/15.
 */
public enum PermissionGroupFlag {

    PERSONAL_INFO("personalInfo"),
    NONE("");

    private String flagName;

    private PermissionGroupFlag(String name){
        this.flagName = name;
    }

    public String getFlagName() {
        return flagName;
    }
}
