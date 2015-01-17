package com.cgz.capa;

import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class GooglePlayCrawlerServiceTest {

    @Autowired
    private GooglePlayCrawlerService googlePlayCrawlerService;

    @Autowired
    private GooglePlayCrawlerService googlePlayCrawlerService2;

    public static final String APP_PACKAGE_TO_TEST = "com.ea.game.pvz2_row";

    @Test
    public void testDownloadPermission() throws Exception {
        Set<String> permissions = googlePlayCrawlerService.getPermissionsForPackage(APP_PACKAGE_TO_TEST);
        assertNotNull(permissions);
        assertTrue(0 < permissions.size());
        permissions = googlePlayCrawlerService.getPermissionsForPackage("com.skype.raider");
        assertNotNull(permissions);
        assertTrue(0 < permissions.size());
        permissions = googlePlayCrawlerService.getPermissionsForPackage("se.feomedia.quizkampen.pl.lite");
        assertNotNull(permissions);
        assertTrue(0 < permissions.size());


    }

    //TODO tests with crappy arguments

    @Test(expected = ServiceException.class)
    public void testInvalidData() throws Exception {
        Set<String> permissions = googlePlayCrawlerService.getPermissionsForPackage("not.such.crap");
    }
}