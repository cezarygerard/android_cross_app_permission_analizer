package com.cgz.capa.model;

import com.cgz.capa.model.enums.PermissionFlag;
import com.cgz.capa.model.enums.PermissionGroupFlag;
import com.cgz.capa.model.enums.ProtectionLevel;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by czarek on 04/01/15.
 */
public class Permission {

    public static final String NAME_PREFIX =  "android.permission.";

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
        if (group != null) {
            group.addPermission(name, this);
        }
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

    @Override
    public String toString() {
        String shortName = name;
        if(name.startsWith(NAME_PREFIX)){
            shortName = name.substring(19);
        }
        String personal = "";
        if(group!=null && group.getFlag()!=null && group.getFlag().equals(PermissionGroupFlag.PERSONAL_INFO)){
            personal = "PERSONAL";
        }

        String level = "";
        if (protectionLevel!=null){
            level = protectionLevel.getName();
        }

        String flagStr ="";
        if(flag!=null){
            flagStr = flag.getName();
        }
        return shortName + ", " + level + ", " + personal + ", " + flagStr;
    }
}
