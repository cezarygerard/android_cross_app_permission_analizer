package com.cgz.capa;

import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class GooglePlayCrawlerServiceTest{

    @Autowired
    private GooglePlayCrawlerService googlePlayCrawlerService;

    @Autowired
    private GooglePlayCrawlerService googlePlayCrawlerService2;

    public static final String APP_PACKAGE_TO_TEST = "com.ea.game.pvz2_row";

    @Test
    public void testDownloadPermission() throws Exception {
        List<String> permissions = googlePlayCrawlerService.getPermissionsForPackage(APP_PACKAGE_TO_TEST);
        assertNotNull(permissions);
        assertTrue(0<permissions.size());
    }

    @Test
    public void testPrototyping() throws Exception {
        assertNotSame(googlePlayCrawlerService, googlePlayCrawlerService2);
        List<String> permissions_1 = googlePlayCrawlerService.getPermissionsForPackage(APP_PACKAGE_TO_TEST);
        List<String> permissions_2 = googlePlayCrawlerService2.getPermissionsForPackage(APP_PACKAGE_TO_TEST);
        assertTrue(permissions_1.containsAll(permissions_2));
        assertTrue(permissions_2.containsAll(permissions_1));
    }

    //TODO tests with crappy arguments

    @Test
    public void testInvalidData() throws Exception {
        List<String> permissions = googlePlayCrawlerService.getPermissionsForPackage("not.such.crap");
        int i=0;
        i++;
    }
}