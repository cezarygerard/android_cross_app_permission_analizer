package com.cgz.capa;

import com.cgz.capa.logic.services.ApplicationDescriptionParserService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class ApplicationDescriptionParserServiceTest{

    @Autowired
    private ApplicationDescriptionParserService applicationDescriptionParserService;

    public static final String APP_PACKAGE_TO_TEST = "com.ea.game.pvz2_row";

    @Test
    public void testGetSimilarAppsPackageNames() throws Exception {
        List<String> similarAppsList = applicationDescriptionParserService.getSimilarAppsPackageNames("com.ea.game.pvz2_row");
        assertEquals(16,similarAppsList.size());
    }

    //TODO test  getSimilarAppsPackageNames(null) and getSimilarAppsPackageNames("");

}