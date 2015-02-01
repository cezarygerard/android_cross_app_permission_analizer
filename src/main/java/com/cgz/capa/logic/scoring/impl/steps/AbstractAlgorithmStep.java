package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
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
public abstract class AbstractAlgorithmStep implements AlgorithmStep {

    @Autowired
    protected RiskScoreFactory riskScoreFactory;

    @Autowired
    protected SystemPermissionsInfoService permissionsInfoService;

    @Autowired
    protected GooglePlayCrawlerService googlePlayCrawlerService;

    protected String WHITESPACE = " ";

    public abstract RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException;

    protected Map<String, Integer> riskScoreMap;

    protected int evaluateRisk(Permission permission , StringBuilder messageBuilder) {
        return evaluateRisk(permission, this.riskScoreMap, messageBuilder);
    }

    protected int evaluateRisk(Permission permission, Map<String, Integer> riskScoreMap, StringBuilder messageBuilder) {

        int score = 0;
        messageBuilder.append(WHITESPACE).append(permission.getName()).append(WHITESPACE).append(permission.getProtectionLevel()).append(WHITESPACE);
        if (riskScoreMap.containsKey(permission.getProtectionLevel().getName())) {
            score += riskScoreMap.get(permission.getProtectionLevel().getName());
            messageBuilder.append(riskScoreMap.get(permission.getProtectionLevel().getName())).append(WHITESPACE);
        }

        if (permissionsInfoService.costsMoney(permission)) {
            score += riskScoreMap.get("costsMoney");
            messageBuilder.append("costsMoney").append(WHITESPACE).append(riskScoreMap.get("costsMoney")).append(WHITESPACE);
        }

        if (permissionsInfoService.usesPersonalInfo(permission)) {
            score += riskScoreMap.get("personalInfo");
            messageBuilder.append("personalInfo").append(WHITESPACE).append(riskScoreMap.get("personalInfo")).append(WHITESPACE);
        }

        return score;

    }
}
