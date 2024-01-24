package com.example.studylink_studio_dit2b03;

public class User {
    private String userid,email,username;
    private int roleid;

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
}
