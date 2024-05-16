package com.example.chatsapp.server_response;

public class MessageToServer {
    String msg;

    public MessageToServer(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
