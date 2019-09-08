package com.example.tryauth;


public class Notification {

    private String name;
    private String dos;
    private String file;

    public Notification() {
    }

    public Notification(String name, String dos, String file) {
        this.name = name;
        this.dos = dos;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDos() {
        return dos;
    }

    public void setDos(String dos) {
        this.dos = dos;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }


}
