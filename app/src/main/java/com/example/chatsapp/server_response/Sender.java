package com.example.chatsapp.server_response;

public class Sender {
    private String username;

    public Sender(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
