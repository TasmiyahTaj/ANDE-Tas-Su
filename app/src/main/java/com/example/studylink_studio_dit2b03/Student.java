package com.example.studylink_studio_dit2b03;

public class Student {

    String userid,username,institution,course;

    public Student(String userid, String username, String institution, String course) {
        this.userid = userid;
        this.username = username;

        this.institution = institution;
        this.course= course;
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


    public String getInstitutionid() {
        return institution;
    }

    public void setInstitutionid(String institutionid) {
        this.institution = institutionid;
    }

    public String getCourseid() {
        return course;
    }

    public void setCourseid(String courseid) {
        this.course = courseid;
    }
}
