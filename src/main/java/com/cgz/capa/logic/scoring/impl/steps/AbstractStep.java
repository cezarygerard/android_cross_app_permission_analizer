package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by czarek on 16/01/15.
 */
public abstract class AbstractStep {

    @Autowired
    protected RiskScoreFactory riskScoreFactory;

    @Autowired
    protected SystemPermissionsInfoService permissionsInfoService;

    @Autowired
    protected GooglePlayCrawlerService googlePlayCrawlerService;

    public abstract RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException;

    protected int evaluateRisk(Permission permission, Map<String, Integer> riskScoreMap) {

        int score = 0;

        if (riskScoreMap.containsKey(permission.getProtectionLevel().getName())) {
            score += riskScoreMap.get(permission.getProtectionLevel().getName());
        }

        if (permissionsInfoService.costsMoney(permission)) {
            score += riskScoreMap.get("costsMoney");
        }

        if (permissionsInfoService.usesPersonalInfo(permission)) {
            score += riskScoreMap.get("personalInfo");
        }

        return score;

    }
}
