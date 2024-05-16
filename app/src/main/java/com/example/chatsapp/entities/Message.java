package com.example.chatsapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Message {
    @PrimaryKey (autoGenerate = true)
    private int id;

    private String chatID;
    private String senderUserName;
    private String timeCreated;

    private String content;

    public Message(String senderUserName, String timeCreated, String content) {
        this.senderUserName = senderUserName;
        this.timeCreated = timeCreated;
        this.content = content;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderDisplayName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}

