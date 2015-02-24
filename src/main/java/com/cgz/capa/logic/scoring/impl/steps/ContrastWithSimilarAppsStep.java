package com.cgz.capa.logic.scoring.impl.steps;

import com.cgz.capa.exceptions.AlgorithmException;
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
import java.util.List;
import java.util.Map;

/**
 * Created by czarek on 14/01/15.
 */
public class ContrastWithSimilarAppsStep extends AbstractAlgorithmStep  {

    private Logger logger = LoggerFactory.getLogger(ContrastWithSimilarAppsStep.class);


    protected double rarePermissionBracket;

    protected double uniquePermissionBracket;

    protected double uniquePermissionPenaltyRatio;

    public ContrastWithSimilarAppsStep(Map<String, Integer> riskScoreMap, double rarePermissionBracket, double uniquePermissionBracket, double uniquePermissionPenaltyRatio) {
        this(rarePermissionBracket,uniquePermissionBracket,uniquePermissionPenaltyRatio);
        this.riskScoreMap = riskScoreMap;
    }

    protected  ContrastWithSimilarAppsStep(double rarePermissionBracket, double uniquePermissionBracket, double uniquePermissionPenaltyRatio) {
        this.rarePermissionBracket = rarePermissionBracket;
        this.uniquePermissionBracket = uniquePermissionBracket;
        this.uniquePermissionPenaltyRatio = uniquePermissionPenaltyRatio;
    }



    @Override
    public RiskScore executeStep(AlgorithmDataDTO algorithmDataDTO) throws AlgorithmException {

        Map<String, Integer> permissionUsageCounter = buildPermissionUsageCounter(algorithmDataDTO.getSimilarAppsPermissions());

        int scoreValue = 0;

        StringBuilder messageBuilder = new StringBuilder().append("Comparing with similar apps. ");

        for (String permissionName : algorithmDataDTO.getManifestPermissions()) {
            scoreValue += calculateRisk(algorithmDataDTO, permissionUsageCounter, permissionName, messageBuilder);
        }

        return riskScoreFactory.createRiskScoreWithMessage(scoreValue, messageBuilder.toString());
    }

    private int calculateRisk(AlgorithmDataDTO algorithmDataDTO, Map<String, Integer> permissionUsageCounter, String permissionName, StringBuilder messageBuilder) {
        int scoreValue = 0;
        Permission permission = permissionsInfoService.getPermission(permissionName);
        if (permission != null) {
            if (isUniquePermission(permissionName, permissionUsageCounter, algorithmDataDTO.getSimilarAppsPermissions().size())) {
                messageBuilder.append(permissionName).append(" is unique. ") ;
                scoreValue += uniquePermissionPenaltyRatio * evaluateRisk(permission, messageBuilder);
            } else if (isItRarePermission(permissionName, permissionUsageCounter, algorithmDataDTO.getSimilarAppsPermissions().size())) {
                messageBuilder.append(permissionName).append(" is rare. ");
                scoreValue += evaluateRisk(permission,  messageBuilder);
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

    private boolean isUniquePermission(String permissionName, Map<String, Integer> permissionUsageCounter, int size) {

        Integer count = permissionUsageCounter.get(permissionName);
        if (count == null || count == 0) {
            return true;
        }

        if (count != null) {
            double rate = count.doubleValue() / (double) size;
            if (0 < rate && rate <= uniquePermissionBracket) {
                return true;
            }
        }

        return false;

    }

    public Map<String, Integer> buildPermissionUsageCounter(Map<String, List<String>> similarAppsPermissions) {

        Map<String, Integer> permissionUsageCounter = new HashMap<>();

        Iterator<String> nameIterator = similarAppsPermissions.keySet().iterator();
        while (nameIterator.hasNext()) {
            String appName = nameIterator.next();
            List<String> setOfPermissions = similarAppsPermissions.get(appName);
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
