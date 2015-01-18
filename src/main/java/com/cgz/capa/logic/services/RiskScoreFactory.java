package com.cgz.capa.logic.services;

import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.exceptions.ServiceRuntimeException;
import com.cgz.capa.model.RiskScore;

import java.util.*;

/**
 * Created by czarek on 11/01/15.
 */
public class RiskScoreFactory {

    private static final String ERROR_MESSAGE = "Invalid config for RiskScoreService ";
    private static final String PROPERTY_SEPARATOR = ",";
    public static final int MIN_SCORE = Integer.MIN_VALUE;
    public static final int MAX_SCORE = Integer.MAX_VALUE;
    NavigableMap<Integer, String> ranges = new TreeMap<Integer, String>();

    public RiskScoreFactory(String levelNames, String levelPoints) throws ServiceException {
        List<String> names = parseRiskLevelNames(levelNames);
        List<Integer> points = parseRiskLevelPointsLevelPoints(levelPoints);
        validate(names, points);


        ranges.put(MIN_SCORE, names.get(0));
        for (int i = 0; i < names.size() - 1; i++) {
            ranges.put(points.get(i), names.get(i + 1));
        }
        ranges.put(points.get(points.size() - 1), names.get(names.size() - 1));
    }

    public RiskScore createRiskScore(int score) {
        return createRiskScoreWithMessage(score, "");
    }

    public RiskScore createRiskScoreWithMessage(int score, String message) {
        validateScore(score);
        String name = ranges.floorEntry(score).getValue();
        return new RiskScoreInternal(score, name, message);
    }


    private void validateScore(int score) {
        if (score < MIN_SCORE) {
            throw new ServiceRuntimeException("Score must not be below " + MIN_SCORE);
        }
        if (score > MAX_SCORE) {
            throw new ServiceRuntimeException("Score must not be over " + MIN_SCORE);
        }
    }

    private void validate(List<String> names, List<Integer> pointsList) throws ServiceException {
        if (names == null || pointsList == null) {
            throw new ServiceException(ERROR_MESSAGE + " levelNames or levelPoints are null");
        }

        if (names.size() != pointsList.size() + 1) {
            throw new ServiceException(ERROR_MESSAGE + " there should be one levelPoint fever than levelNames");
        }

        int lastScore = MIN_SCORE;
        for (Integer point : pointsList) {
            if (point < MIN_SCORE) {
                throw new ServiceException(ERROR_MESSAGE + " score must not be below " + MIN_SCORE);
            }

            if (point < lastScore) {
                throw new ServiceException(ERROR_MESSAGE + " levelPoint should be in ascending order");
            }

            lastScore = MIN_SCORE;
        }


    }

    private List<Integer> parseRiskLevelPointsLevelPoints(String levelNames) throws ServiceException {
        String[] points = levelNames.split(PROPERTY_SEPARATOR);
        ArrayList<Integer> pointsList = new ArrayList<Integer>(points.length);
        try {
            for (int i = 0; i < points.length; i++) {
                pointsList.add(Integer.parseInt(points[i]));
            }
        } catch (NumberFormatException e) {
            throw new ServiceException(ERROR_MESSAGE);
        }

        return pointsList;
    }

    private List<String> parseRiskLevelNames(String levelNames) {
        String[] levels = levelNames.split(PROPERTY_SEPARATOR);
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

        @Override
        public String toString() {
            return "RiskScoreInternal{" +
                    "score=" + score +
                    ", name='" + name + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
