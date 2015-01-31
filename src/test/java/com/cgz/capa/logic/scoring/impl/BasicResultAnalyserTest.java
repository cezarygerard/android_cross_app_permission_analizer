package com.cgz.capa.logic.scoring.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class BasicResultAnalyserTest {

    @Autowired
    @Qualifier("BasicResultAnalyser")
    private BasicResultAnalyser basicResultAnalyser;

    @Test
    public void testAnalise() throws Exception {

        //TODO
    }
}