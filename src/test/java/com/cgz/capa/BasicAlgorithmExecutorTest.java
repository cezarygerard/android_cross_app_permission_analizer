package com.cgz.capa;

import com.cgz.capa.logic.scoring.impl.BasicAlgorithmExecutor;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class BasicAlgorithmExecutorTest {

    @Autowired
    List<AlgorithmStep> algorithmSteps;

    @Autowired
    BasicAlgorithmExecutor executor;

    @Test
    public void testExecuteAnalysis() throws Exception {
        System.out.println("WORKS!!" + executor + algorithmSteps);
    }

}