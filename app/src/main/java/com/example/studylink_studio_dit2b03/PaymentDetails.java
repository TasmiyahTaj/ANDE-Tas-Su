package com.example.studylink_studio_dit2b03;

public class PaymentDetails {
    private String tutorId;
    private String noteId;
    private String userId;
    private double price;
    private String noteTitle;
    private String noteAttachment;
    private String currentUserAccountNumber;

    // Default constructor (required for Firestore)
    public PaymentDetails() {}

    public PaymentDetails(String tutorId, String userId, double price, String noteTitle, String noteAttachment, String currentUserAccountNumber,String noteId) {
        this.tutorId = tutorId;
        this.userId = userId;
        this.price = price;
        this.noteTitle = noteTitle;
        this.noteAttachment = noteAttachment;
        this.currentUserAccountNumber = currentUserAccountNumber;
        this.noteId=noteId;
    }

    // Getters and setters
    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteAttachment() {
        return noteAttachment;
    }

    public void setNoteAttachment(String noteAttachment) {
        this.noteAttachment = noteAttachment;
    }

    public String getCurrentUserAccountNumber() {
        return currentUserAccountNumber;
    }

    public void setCurrentUserAccountNumber(String currentUserAccountNumber) {
        this.currentUserAccountNumber = currentUserAccountNumber;
    }
}
