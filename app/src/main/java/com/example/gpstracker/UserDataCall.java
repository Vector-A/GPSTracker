package com.example.gpstracker;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDataCall {
    private String nameCall;
    private String emailCall;
    private String passwordCall;

    // Constructor
    public UserDataCall() {
        // Required for Firebase
    }

    // Getters and setters
    public String getNameCall() { return nameCall; }
    public void setNameCall(String name) { this.nameCall = nameCall; }

    public String getEmailCall() { return emailCall; }
    public void setEmailCall(String email) { this.emailCall = emailCall; }

    public String getPasswordCall() { return passwordCall; }
    public void setPasswordCall(String passwordCall) { this.passwordCall = passwordCall; }


}


