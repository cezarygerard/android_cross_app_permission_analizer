package com.cgz.capa;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmExecutor;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.scoring.interfaces.ResultAnalyser;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class PackageAnalysisAlgorithmTest {

    @Autowired
    private AlgorithmExecutor algorithm;

    @Autowired
    private List<AlgorithmStep> algorithmSteps;

    @Autowired
    private ResultAnalyser analyser;

    @Autowired
    private GooglePlayCrawlerService crawlerService;

    private Set<String> permissionsSet;

    @Before
    public void setUp() throws Exception {
        permissionsSet = crawlerService.getPermissionsForPackage("goldenshorestechnologies.brightestflashlight.free");
    }


    //TODO w kazdej metodzie rozgraniczyc jej wykonanie w logu

    @Test
    public void testExecute() throws Exception {
        List<Pair<RiskScore, AlgorithmStep>> results = algorithm.execute("goldenshorestechnologies.brightestflashlight.free", new ArrayList<String>(permissionsSet), algorithmSteps);
        analyser.analise(results);
        //TODO assert

    }

    @Test
    public void testExecuteWith() throws Exception {
        List<String> permList = new ArrayList<String>(permissionsSet);
        permList.add("android.permission.SEND_SMS");
        List<Pair<RiskScore, AlgorithmStep>> results = algorithm.execute("goldenshorestechnologies.brightestflashlight.free", permList, algorithmSteps);
        analyser.analise(results);

        //TODO ASSERT
    }

    @Test(expected = AlgorithmException.class)
    public void testExecuteAnalysisWhenNoSuchPackageInStore() throws Exception {
        String[] perms = {"android.permission.ACCESS_CHECKIN_PROPERTIES", "android.permission.READ_SMS"};
        List<Pair<RiskScore, AlgorithmStep>> results = algorithm.execute("lalala", Arrays.asList(perms), algorithmSteps);
        analyser.analise(results);
    }


}