package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.model.RiskScore;

import java.util.List;

/**
 * Created by czarek on 11/01/15.
 */
public interface AlgorithmExecutor {

    public List<RiskScore> executeAnalysis(String investigatedPackageName, List<String> investigatedPackagePermissions, List<AlgorithmStep> steps) throws AlgorithmException;
}
