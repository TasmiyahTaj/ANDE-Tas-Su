package com.example.studylink_studio_dit2b03;

import java.util.Date;
import java.io.Serializable;

public class Note implements Serializable {
    private String noteId;
    private String title;
    private String content;
    private String teacherId; // Assuming this is the ID of the teacher who created the note
    private String communityId; // Assuming this is the ID of the community associated with the note
    private double price;
    private String attachmentUrl; // URL or reference to the attachment file


    // Default constructor
    public Note() {
        // Default constructor required for Firestore
    }

    // Parameterized constructor
    public Note(String title, String content, String teacherId, String communityId, double price,
                 String attachmentUrl) {
        this.title = title;
        this.content = content;
        this.teacherId = teacherId;
        this.communityId = communityId;
        this.price = price;
        this.attachmentUrl = attachmentUrl;

    }

    // Getter and setter methods

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

}
