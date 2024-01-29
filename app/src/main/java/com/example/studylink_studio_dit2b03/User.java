package com.example.studylink_studio_dit2b03;

public class User {
    private String userid,email,username;
    private int roleid;
    private String profilePicUrl;
    private static User instance;

    // Private constructor to prevent instantiation
    private User() {
    }
    public void clearUserDetails() {
        userid = null;
        email = null;
        username = null;
        profilePicUrl = null;
        roleid = 0; // Set to default role or a value that represents not logged in
        student = null;
        tutor = null;
    }

    // Method to get the singleton instance
    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }
    public static void resetInstance() {
        instance = null;
    }
    public User(String userid, String email, String username, int roleid) {
        this.userid = userid;
        this.email = email;
        this.username = username;
        this.roleid = roleid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
    private Student student;
    private Tutor tutor;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }
}
