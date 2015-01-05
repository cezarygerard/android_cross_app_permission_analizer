package com.cgz.capa;

import com.cgz.capa.model.PermissionGroup;
import com.cgz.capa.model.enums.PermissionFlag;
import com.cgz.capa.services.SystemPermissionsInfoService;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class SystemPermissionsInfoServiceTest {


    private SystemPermissionsInfoService systemPermissionsInfoService;


    @Before
    public void setUp() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        systemPermissionsInfoService = context.getBean(SystemPermissionsInfoService.class);
    }

    @Test
    public void testPermissionGroupsParsing() throws Exception {
        systemPermissionsInfoService.readCoreManifest();
        Map<String, PermissionGroup> permissionGroups = systemPermissionsInfoService.getPermissionGroups();
        assertTrue(permissionGroups.size() == 31);
    }

    @Test(expected = IllegalStateException.class)
    public void testPermissionGroupsParsingFailsBeforeRead() throws Exception {
        Map<String, PermissionGroup> permissionGroups =  systemPermissionsInfoService.getPermissionGroups();
        assertEquals(permissionGroups.size(), 31);

    }

    //TODO zy flagi sa dobrze zwracane z enumow -- nowy test

}
