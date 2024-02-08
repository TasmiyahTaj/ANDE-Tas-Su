package com.example.studylink_studio_dit2b03;

import java.util.Date;

public class Question {
    private String userID;
    private String communityID;
    private String questionId; // Make sure it's consistent with the Firestore field name
    private String title;
    private String description;
    private String questionImageUrl;
    private String tutorName;
    private String communityName;
    private int replyCount;
    private Date createdAt;

    // Constructors, getters, and setters
    public Question(String userID, String communityID, String title, String description) {
        this.userID = userID;
        this.communityID = communityID;
        this.title = title;
        this.description = description;
    }

    public Question() {
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public Question(String questionId, String userID, String communityName, String title, String description, String tutorName) {
        this.userID = userID;
        this.communityName = communityName;
        this.title = title;
        this.description = description;
        this.tutorName = tutorName;
        this.questionId = questionId;
    }

    public Question(String questionId, String userID, String communityName, String title, String description, String tutorName, String questionImage) {
        this.userID = userID;
        this.communityName = communityName;
        this.title = title;
        this.description = description;
        this.tutorName = tutorName;
        this.questionImageUrl = questionImage;
        this.questionId = questionId;
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

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
