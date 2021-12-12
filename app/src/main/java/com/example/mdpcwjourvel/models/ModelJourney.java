package com.example.mdpcwjourvel.models;

public class ModelJourney {

    String uid, name, email, username, journeyID, journey, uploadTime;

    public ModelJourney() {
    }

    public ModelJourney(String uid, String name, String email, String username, String journeyID, String journey, String uploadTime) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.username = username;
        this.journeyID = journeyID;
        this.journey = journey;
        this.uploadTime = uploadTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public String getJourney() {
        return journey;
    }

    public void setJourney(String journey) {
        this.journey = journey;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}
