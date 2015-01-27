package com.cgz.capa;

import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.PermissionGroup;
import com.cgz.capa.model.enums.PermissionFlag;
import com.cgz.capa.model.enums.PermissionGroupFlag;
import com.cgz.capa.model.enums.ProtectionLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class SystemPermissionsInfoServiceTests {


    private static final int NUMBER_OF_PERMISSION_GROUPS = 31;
    private static final int NUMBER_OF_PERMISSIONS = 267;

    @Autowired
    private SystemPermissionsInfoService systemPermissionsInfoService;


    @Test
    public void testPermissionGroupsParsing() throws Exception {
        Map<String, PermissionGroup> permissionGroups = systemPermissionsInfoService.getPermissionGroupsMap();
        assertTrue(permissionGroups.size() == NUMBER_OF_PERMISSION_GROUPS);

        PermissionGroup pg = permissionGroups.get("android.permission-group.SYNC_SETTINGS");
        assertEquals(pg.getPriority(), 120);

        PermissionGroup pg2 = permissionGroups.get("android.permission-group.ACCOUNTS");
        assertEquals(pg2.getFlag(), PermissionGroupFlag.PERSONAL_INFO);

    }

    @Test
    public void testPermissionsParsing() throws Exception {
        Map<String, Permission> permissions = systemPermissionsInfoService.getPermissionsMap();
        assertTrue(permissions.size() == NUMBER_OF_PERMISSIONS);

        Permission p = permissions.get("android.permission.CALL_PHONE");
        assertEquals(PermissionFlag.COSTS_MONEY, p.getFlag());
        assertEquals(ProtectionLevel.DANGEROUS, p.getProtectionLevel());

        PermissionGroup phoneCallsGroup = systemPermissionsInfoService.getPermissionGroupsMap().get("android.permission-group.PHONE_CALLS");
        assertEquals(phoneCallsGroup, p.getGroup());

        assertTrue(phoneCallsGroup.containsPermission(p.getName()));
        assertTrue(phoneCallsGroup.containsPermission("android.permission.USE_SIP"));

    }

    //TODO czy flagi sa dobrze zwracane z enumow -- nowy test

}
