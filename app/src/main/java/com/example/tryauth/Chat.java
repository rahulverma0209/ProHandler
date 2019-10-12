package com.example.tryauth;

public class Chat {

    private String date;
    private String feedback;
    private String flink;
    private String status;
    private String title;

    public Chat(){

    }

    public Chat(String date, String feedback, String flink, String status, String title) {
        this.date = date;
        this.feedback = feedback;
        this.flink = flink;
        this.status = status;
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFlink() {
        return flink;
    }

    public void setFlink(String flink) {
        this.flink = flink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
