//package com.cgz.capa.web;
//
//import com.cgz.capa.exceptions.AlgorithmException;
//import com.cgz.capa.exceptions.ServiceException;
//import com.cgz.capa.exceptions.WebApplicationException;
//import com.cgz.capa.logic.scoring.interfaces.AlgorithmExecutor;
//import com.cgz.capa.logic.scoring.interfaces.AlgorithmStep;
//import com.cgz.capa.logic.scoring.interfaces.ResultAnalyser;
//import com.cgz.capa.logic.services.GooglePlayCrawlerService;
//import com.cgz.capa.model.RiskScore;
//import org.apache.commons.lang3.tuple.Pair;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by czarek on 17/01/15.
// */
//@RestController
//@RequestMapping("/capa")
//public class RestApiController {
//
//    private Logger logger = LoggerFactory.getLogger(RestController.class);
//
//    private AlgorithmExecutor algorithm;
//
//    private ResultAnalyser analyser;
//
//    private GooglePlayCrawlerService crawlerService;
//
//    @RequestMapping(value = "analiseFromStore", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    RiskScore analiseFromStore(@RequestParam(value = "packageName", required = true) String packageName) throws WebApplicationException {
//        try {
//            Set<String> permissionsForPackage = crawlerService.getPermissionsForPackage(packageName);
//            List<Pair<RiskScore, AlgorithmStep>> results = algorithm.executeAnalysisAllSteps(packageName, new ArrayList<String>(permissionsForPackage));
//            return analyser.analise(results);
//        } catch (ServiceException | AlgorithmException e) {
//            logger.error("Error happened: ", e);
//            throw new WebApplicationException("Error happened: ", e);
//        }
//    }
//
//    @RequestMapping(value = "permissionsFromStore", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    Collection<String> permissionsFromStore(@RequestParam(value = "packageName", required = true) String packageName) throws WebApplicationException {
//        try {
//            Collection<String> permissionsForPackage = crawlerService.getPermissionsForPackage(packageName);
//            return permissionsForPackage;
//        } catch (ServiceException e) {
//            logger.error("Error happened: ", e);
//            throw new WebApplicationException("Error happened: ", e);
//        }
//    }
//
//    //TODO analize [postem wysylana lista uprawnien]
//}
