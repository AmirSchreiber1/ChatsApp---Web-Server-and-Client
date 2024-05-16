package com.example.chatsapp;

import android.graphics.Bitmap;

import com.google.firebase.iid.FirebaseInstanceId;

public class UserDetails {
    private String username;
    private String password;
    private String fireBaseToken;
    private String displayName;
    private String profilePic;
    private String token;


    public UserDetails(String username, String password, String displayName, String profilePic) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.profilePic = profilePic;
        this.fireBaseToken = null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getFireBaseToken() {
        return fireBaseToken;
    }

    public void setFireBaseToken(String fireBaseToken) {
        this.fireBaseToken = fireBaseToken;
    }
}
