package com.cgz.capa.logic.scoring.interfaces;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by czarek on 11/01/15.
 */
public interface AlgorithmExecutor {

    public List<Pair<RiskScore, AlgorithmStep>> executeAnalysis(String investigatedPackageName, List<String> investigatedPackagePermissions, List<AlgorithmStep> algorithmSteps) throws AlgorithmException;
}
