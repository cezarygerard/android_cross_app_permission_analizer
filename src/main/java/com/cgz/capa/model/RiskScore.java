package com.cgz.capa.model;

/**
 * Created by czarek on 11/01/15.
 */
public class RiskScore {

    private int score;
    private String name;
    private String message;

    public RiskScore(int score, String name, String message) {
        this.score = score;
        this.name = name;
        this.message = message;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

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

    //this must stay here in order to serialize and deserialize.
    protected RiskScore() {
    }

//    public void setScore(int score) {
//        this.score = score;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
}
