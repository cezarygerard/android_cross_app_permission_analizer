package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.logic.services.SystemPermissionsInfoService;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.RiskScore;
import com.cgz.capa.utils.AlgorithmDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by czarek on 14/01/15.
 */
public class ContrastWithSimilarAppsStep extends AbstractStep implements AlgorithmStep {

    private Logger logger = LoggerFactory.getLogger(ContrastWithSimilarAppsStep.class);

    @Autowired
    protected RiskScoreFactory riskScoreFactory;

    @Autowired
    protected SystemPermissionsInfoService permissionsInfoService;

    @Autowired
    protected GooglePlayCrawlerService googlePlayCrawlerService;

    protected double rarePermissionBracket;

    protected Map<String, Integer> riskScoreUniquePermissionMap;

    protected Map<String, Integer> riskScoreRarePermissionMap;

    public ContrastWithSimilarAppsStep(Map<String, Integer> riskScoreUniquePermissionMap, Map<String, Integer> riskScoreRarePermissionMap, double rarePermissionBracket) {
        this.rarePermissionBracket = rarePermissionBracket;
        this.riskScoreUniquePermissionMap = riskScoreUniquePermissionMap;
        this.riskScoreRarePermissionMap = riskScoreRarePermissionMap;
    }

    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {

        Map<String, Integer> permissionUsageCounter = buildPermissionUsageCounter(algorithmDataDTO.getSimilarAppsPermissions());

        int scoreValue = 0;

        for (String permissionName : algorithmDataDTO.getManifestPermissions()) {
            scoreValue += calculateRisk(algorithmDataDTO, permissionUsageCounter, permissionName);
        }

        return riskScoreFactory.createRiskScore(scoreValue);
    }

    private int calculateRisk(AlgorithmDataDTO algorithmDataDTO, Map<String, Integer> permissionUsageCounter, String permissionName) {
        int scoreValue = 0;
        Permission permission = permissionsInfoService.getPermission(permissionName);
        if (permission != null) {
            if (isUniquePermission(permissionName, permissionUsageCounter)) {
                scoreValue += evaluateRisk(permission, riskScoreUniquePermissionMap);
            }
            if (isItRarePermission(permissionName, permissionUsageCounter, algorithmDataDTO.getSimilarAppsPermissions().size())) {
                scoreValue += evaluateRisk(permission, riskScoreRarePermissionMap);
            }
        }
        return scoreValue;
    }

    private boolean isItRarePermission(String permissionName, Map<String, Integer> permissionUsageCounter, int size) {
        Integer count = permissionUsageCounter.get(permissionName);
        if (count != null) {
            double rate = count.doubleValue() / (double) size;
            if (0 < rate && rate <= rarePermissionBracket) {
                return true;
            }
        }
        return false;
    }

    private boolean isUniquePermission(String permissionName, Map<String, Integer> permissionUsageCounter) {

        Integer count = permissionUsageCounter.get(permissionName);
        if (count == null || count == 0) {
            return true;
        }
        return false;

    }

    public Map<String, Integer> buildPermissionUsageCounter(Map<String, Set<String>> similarAppsPermissions) {

        Map<String, Integer> permissionUsageCounter = new HashMap<>();

        Iterator<String> nameIterator = similarAppsPermissions.keySet().iterator();
        while (nameIterator.hasNext()) {
            String appName = nameIterator.next();
            Set<String> setOfPermissions = similarAppsPermissions.get(appName);
            Iterator<String> permIterator = setOfPermissions.iterator();
            while (permIterator.hasNext()) {
                String permName = permIterator.next();
                Integer count = permissionUsageCounter.get(permName);
                if (count != null) {
                    permissionUsageCounter.put(permName, count + 1);
                } else {
                    permissionUsageCounter.put(permName, 1);
                }
            }
        }

        return permissionUsageCounter;
    }

}
