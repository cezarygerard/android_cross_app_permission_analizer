package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.exceptions.AlgorithmErrorException;
import com.cgz.capa.model.RiskScore;

import java.util.List;

/**
 * Created by czarek on 11/01/15.
 */
public interface PackageAnalysisAlgorithm {

    public RiskScore executeAnalysis(String investigatedPackageName, List<String> investigatedPackagePermissions) throws AlgorithmErrorException;
}
