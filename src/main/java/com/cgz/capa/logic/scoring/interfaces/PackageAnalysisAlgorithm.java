package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.model.RiskScore;

/**
 * Created by czarek on 11/01/15.
 */
public interface PackageAnalysisAlgorithm {

    public RiskScore ExecuteAnalysis(String packageName);
}
