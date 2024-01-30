package com.example.studylink_studio_dit2b03;

public class Question {
    private String userID;
    private String communityID;
    private String title;
    private String description;
    private String questionImageUrl;

    public Question(String userID, String communityID, String title, String description) {
        this.userID = userID;
        this.communityID = communityID;
        this.title = title;
        this.description = description;

    }

    public Question() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestionImageUrl() {
        return questionImageUrl;
    }

    public void setQuestionImageUrl(String questionImageUrl) {
        this.questionImageUrl = questionImageUrl;
    }
}
