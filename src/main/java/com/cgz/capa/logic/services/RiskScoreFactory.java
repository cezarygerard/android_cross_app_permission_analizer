package com.cgz.capa.logic.services;

import com.cgz.capa.exceptions.ServiceErrorException;
import com.cgz.capa.model.RiskScore;

import java.util.*;

/**
 * Created by czarek on 11/01/15.
 */
public class RiskScoreFactory {

    public static final String ERROR_MESSAGE = "Invalid config for RiskScoreService ";
    public static final String PROPERTY_SEPARATOR= ",";
    public static final int MIN_SCORE = 0;
    NavigableMap<Integer, String> ranges = new TreeMap<Integer, String>();

    public RiskScoreFactory(String levelNames, String levelPoints) throws ServiceErrorException {
        List<String> names = parseRiskLevelNames(levelNames);
        List<Integer> points = parseRiskLevelPointsLevelPoints(levelPoints);
        validate(names, points);


        ranges.put(MIN_SCORE, names.get(0));
        for (int i = 0; i < names.size() -1; i++) {
            ranges.put(points.get(i), names.get(i+1));
        }
        ranges.put(points.get(points.size()-1), names.get(names.size()-1));
    }

    public RiskScore createRiskScore(int score) throws ServiceErrorException {
        return createRiskScoreWithMessage(score,null);
    }

    public RiskScore createRiskScoreWithMessage(int score, String message) throws ServiceErrorException {
        validateScore(score);
        String name = ranges.floorEntry(score).getValue();
        return new RiskScoreInternal(score, name, message) ;
    }

    private void validateScore(int score) throws ServiceErrorException {
        if(score<MIN_SCORE){
            throw new ServiceErrorException("Score must not be below " + MIN_SCORE);
        }
    }

    private void validate(List<String> names, List<Integer> pointsList) throws ServiceErrorException {
        if(names == null || pointsList == null ){
            throw new ServiceErrorException(ERROR_MESSAGE + " levelNames or levelPoints are null");
        }

        if(names.size()!= pointsList.size()+1){
            throw new ServiceErrorException(ERROR_MESSAGE + " there should be one levelPoint fever than levelNames");
        }

        int lastScore = MIN_SCORE ;
        for (Integer point : pointsList) {
            if(point < MIN_SCORE){
                throw new ServiceErrorException(ERROR_MESSAGE + " score must not be below " + MIN_SCORE);
            }

            if(point < lastScore){
                throw new ServiceErrorException(ERROR_MESSAGE + " levelPoint should be in ascending order");
            }

            lastScore=MIN_SCORE;
        }



    }

    private List<Integer> parseRiskLevelPointsLevelPoints(String levelNames) throws ServiceErrorException {
        String [] points = levelNames.split(PROPERTY_SEPARATOR);
        ArrayList<Integer> pointsList = new ArrayList<Integer>(points.length);
        try {
            for (int i = 0; i < points.length; i++) {
                pointsList.add(Integer.parseInt(points[i]));
            }
        } catch (NumberFormatException e) {
            throw new ServiceErrorException(ERROR_MESSAGE);
        }

        return pointsList;
    }

    private List<String> parseRiskLevelNames(String levelNames) {
        String [] levels = levelNames.split(PROPERTY_SEPARATOR);
        return Arrays.asList(levels);
    }

    /**
     *
     */
    private class RiskScoreInternal implements RiskScore {

        private int score;
        private String name;
        private String message;

        public RiskScoreInternal(int score, String name, String message) {
            this.score = score;
            this.name = name;
            this.message = message;
        }

        @Override
        public int getScore() {
            return score;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getMessage() {
            return message;
        }


    }
}
