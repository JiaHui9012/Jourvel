package com.example.mdpcwjourvel.models;

public class ModelPost {
    String uid, name, email, profilePic, username, postID, postText, postImage, postTime;

    public ModelPost() {
    }

    public ModelPost(String uid, String name, String email, String profilePic, String username, String postID, String postText, String postImage, String postTime) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.username = username;
        this.postID = postID;
        this.postText = postText;
        this.postImage = postImage;
        this.postTime = postTime;
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
