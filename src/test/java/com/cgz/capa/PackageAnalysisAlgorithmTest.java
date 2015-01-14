package com.cgz.capa;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmExecutor;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class PackageAnalysisAlgorithmTest {

    @Autowired
    private AlgorithmExecutor algorithm;

    @Test
    public void testExecute() throws Exception {
        algorithm.executeAnalysis("goldenshorestechnologies.brightestflashlight.free",Collections.<String>emptyList(), Collections.<AlgorithmStep>emptyList());

    }

    @Test(expected =  AlgorithmException.class)
    public void testExecuteAnalysisWhenNoSuchPackageInStore() throws Exception {

            algorithm.executeAnalysis("lalala",Collections.<String>emptyList(), Collections.<AlgorithmStep>emptyList());

    }
}