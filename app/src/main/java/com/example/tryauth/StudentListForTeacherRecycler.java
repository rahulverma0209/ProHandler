package com.example.tryauth;

public class StudentListForTeacherRecycler {

    private String uid;
    StudentListForTeacherRecycler(){

    }

    public StudentListForTeacherRecycler(String sid) {
        this.uid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String sid) {
        this.uid = sid;
    }
}
