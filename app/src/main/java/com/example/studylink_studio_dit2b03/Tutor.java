package com.example.studylink_studio_dit2b03;

public class Tutor {
    private String userid,username,qualification,specialised;
    private int yearsOfExperience;

    public Tutor(String userid, String username, String qualification, String specialised, int yearsOfExperience) {
        this.userid = userid;
        this.username = username;
        this.qualification = qualification;
        this.specialised = specialised;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialised() {
        return specialised;
    }

    public void setSpecialised(String specialised) {
        this.specialised = specialised;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
