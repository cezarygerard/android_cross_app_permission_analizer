package com.cgz.capa.model;

import com.cgz.capa.model.enums.PermissionGroupFlag;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
public class PermissionGroup  {

    private String name;
    private PermissionGroupFlag flag;
    private int priority;
    private Map<String, Permission> permissions = new HashMap<String, Permission>();

    public PermissionGroup(String name, PermissionGroupFlag flag, int priority) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name must not be empty");
        }
        this.name = name;
        this.priority = priority;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public PermissionGroupFlag getFlag() {
        return flag;
    }

    public int getPriority() {
        return priority;
    }

    public void addPermission(String name, Permission permissionToAdd) {
        permissions.put(name, permissionToAdd);
    }

    public boolean containsPermission(String permissionName) {
        return null != permissions.get(permissionName);
    }
}
