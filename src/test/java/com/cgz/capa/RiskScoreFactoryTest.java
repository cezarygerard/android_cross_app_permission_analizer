package com.cgz.capa;

import com.cgz.capa.exceptions.ServiceErrorException;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.model.RiskScore;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class RiskScoreFactoryTest{

    @Autowired
    private RiskScoreFactory riskScoreFactory;

    @Test
    public void testCreateRiskScore() throws Exception {
        RiskScore rs1 = riskScoreFactory.createRiskScore(0);

        RiskScore rs2 = riskScoreFactory.createRiskScore(Integer.MAX_VALUE);
        assertFalse(rs1.getName().equals(rs2.getName()));
    }

    @Test
    public void testCreateRiskScoreWithMessage() throws Exception {

        String message = "cool";
        RiskScore rs = riskScoreFactory.createRiskScoreWithMessage(500, message);
        assertEquals(rs.getMessage(), message);

    }

    @Test(expected = ServiceErrorException.class)
    public void testInvalidScore() throws Exception {
        riskScoreFactory.createRiskScore(-5);
    }
}