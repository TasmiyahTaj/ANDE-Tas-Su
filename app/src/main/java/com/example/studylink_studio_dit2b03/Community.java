package com.example.studylink_studio_dit2b03;

import java.util.Date;

import com.google.firebase.firestore.Exclude;

public class Community {
    private String communityId;
    private String title;
    private String description;
    private String creatorId;
    private Date creationTimestamp;
    private int memberCount;

    // Default constructor required for Firestore
    public Community() {
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    // Exclude this property from Firestore to prevent it from being stored as a separate field
    @Exclude
    public boolean isNewCommunity() {
        // You can add any condition here based on your business logic
        return communityId == null || communityId.isEmpty();
    }
}
