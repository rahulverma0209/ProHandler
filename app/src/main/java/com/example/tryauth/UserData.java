package com.example.tryauth;

public class UserData {

    private String name;
    private String usn;
    private String user_type;
    private String email;

    public UserData(){

    }

    public UserData(String name, String usn, String user_type, String email) {
        this.name = name;
        this.usn = usn;
        this.user_type = user_type;
        this.email = email;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
