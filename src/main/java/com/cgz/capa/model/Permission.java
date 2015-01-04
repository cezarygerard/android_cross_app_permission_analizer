package com.cgz.capa.model;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by czarek on 04/01/15.
 */
public class Permission {

    private String name;
    private PermissionGroup group;
    private ProtectionLevel protectionLevel;
    private PermissionFlag flag;

    public Permission(String name, PermissionGroup group, ProtectionLevel protectionLevel, PermissionFlag flag) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name must not be empty");
        }
        if (protectionLevel == null) {
            throw new IllegalArgumentException("protectionLevel must not be empty");
        }
        this.name = name;
        this.group = group;
        this.protectionLevel = protectionLevel;
        this.flag = flag;
        group.addPermission(name,this);
    }

    public String getName() {
        return name;
    }

    public PermissionGroup getGroup() {
        return group;
    }

    public ProtectionLevel getProtectionLevel() {
        return protectionLevel;
    }

    public PermissionFlag getFlag() {
        return flag;
    }
}
