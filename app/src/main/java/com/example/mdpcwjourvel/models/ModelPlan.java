package com.example.mdpcwjourvel.models;

public class ModelPlan {

    String uid, name, email, username, planID, plan, uploadTime;

    public ModelPlan() {
    }

    public ModelPlan(String uid, String name, String email, String username, String planID, String plan, String uploadTime) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.username = username;
        this.planID = planID;
        this.plan = plan;
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

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}
