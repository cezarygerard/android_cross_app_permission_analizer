package com.cgz.capa.model;

/**
 * Created by czarek on 04/01/15.
 */
public enum PermissionFlag {

    COSTS_MONEY("costsMoney"),
    NONE("");

    private String flagName;

    private PermissionFlag(String name){
        this.flagName = name;
    }

    public String getFlagName() {
        return flagName;
    }

}
