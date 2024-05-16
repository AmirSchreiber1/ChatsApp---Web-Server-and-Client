package com.example.chatsapp.server_response;

public class LastMessage {
    private String created;
    private String content;

    public LastMessage(String created, String content) {
        this.created = created;
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
