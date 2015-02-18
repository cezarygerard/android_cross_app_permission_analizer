package com.cgz.capa.web;

import com.cgz.capa.exceptions.AlgorithmException;
import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmExecutor;
import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
import com.cgz.capa.logic.scoring.interfaces.ResultAnalyser;
import com.cgz.capa.logic.services.ApplicationDescriptionParserService;
import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import com.cgz.capa.logic.services.RiskScoreFactory;
import com.cgz.capa.model.RiskScore;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
* Created by czarek on 17/01/15.
 * TODO add some basic tests
*/
@RestController
@RequestMapping("/capa")
public class RestApiController {

    private Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private AlgorithmExecutor algorithm;

    @Resource(name="BasicResultAnalyser")
    private ResultAnalyser analyser;

    @Autowired
    private GooglePlayCrawlerService crawlerService;

    @Autowired
    private RiskScoreFactory riskScoreFactory;

    @Autowired
    private ApplicationDescriptionParserService applicationDescriptionParserService;

    @RequestMapping(value = "analiseFromStore", method = RequestMethod.GET)
    public
    @ResponseBody
    RiskScore analiseFromStore(@RequestParam(value = "packageName", required = true) String packageName, HttpServletResponse response)  {
        try {
            List<String> permissionsForPackage = crawlerService.getPermissionsForPackage(packageName);
            List<Pair<RiskScore, AlgorithmStep>> results = algorithm.executeAllSteps(packageName, new ArrayList<String>(permissionsForPackage));
            return analyser.analise(results);
        } catch (ServiceException | AlgorithmException e) {
            return handleError(response, e);
        }
    }

    private RiskScore handleError(HttpServletResponse response, Exception e) {
        //TODO better error handling
        logger.error("Error happened: ", e);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return riskScoreFactory.createRiskScoreWithMessage(0, "ERROR HAPPENED");
    }

    @RequestMapping(value = "permissionsFromStore", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> permissionsFromStore(@RequestParam(value = "packageName", required = true) String packageName, HttpServletResponse response)  {
        try {
            List<String> permissionsForPackage = crawlerService.getPermissionsForPackage(packageName);
            return permissionsForPackage;
        } catch (ServiceException e) {
            //TODO better error handling
            logger.error("Error happened: ", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Collections.<String>emptyList();
        }
    }

    @RequestMapping(value = "analise", method = RequestMethod.POST)
    public
    @ResponseBody
    RiskScore analise(@RequestParam(value = "packageName", required = true) String packageName, @RequestBody(required = true) List<String> packagePermissions, HttpServletResponse response)  {

        try {
            List<Pair<RiskScore, AlgorithmStep>> results = algorithm.executeAllSteps(packageName, packagePermissions);
            return analyser.analise(results);
        } catch (AlgorithmException e) {
            return handleError(response, e);
        }

    }

    @RequestMapping(value = "similarApps", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> similarAppsFromStore(@RequestParam(value = "packageName", required = true) String packageName, HttpServletResponse response)  {
        try {
            return  applicationDescriptionParserService.getSimilarAppsPackageNames(packageName);
        } catch (ServiceException e) {
            //TODO better error handling
            logger.error("Error happened: ", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Collections.<String>emptyList();
        }
    }
}
