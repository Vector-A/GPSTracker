package com.example.gpstracker;

public class UserData {

    private String userName;

    // string variable for storing
    // employee contact number
    private String email;

    // string variable for storing
    // employee address.
    private String pass;

    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public UserData() {

    }

    // created getter and setter methods
    // for all our variables.
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


}
