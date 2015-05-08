package com.mrcornman.otp.models;

public class MatchItem {
    private String id;
    private String createdTimestamp;
    private String firstId;
    private String secondId;
    private String matchmakerId;
    private int numLikes;

    public MatchItem(String id){
        this.id = id;
    }

    public MatchItem(){}

    public String getId() {
        return id;
    }

    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(String createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getFirstId() {
        return firstId;
    }

    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }

    public String getSecondId() {
        return secondId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
    }

    public String getMatchmakerId() {
        return matchmakerId;
    }

    public void setMatchmakerId(String matchmakerId) {
        this.matchmakerId = matchmakerId;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    @Override
    public String toString() {
        return "TODO: First + Second + Matchmaker";
    }
}
