package com.example.chatsapp.repositories;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Room;

import com.example.chatsapp.LocalDatabase;
import com.example.chatsapp.apis.MessagesAPI;
import com.example.chatsapp.daos.ChatsDao;
import com.example.chatsapp.daos.MessagesDao;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.entities.Message;
import com.example.chatsapp.server_response.MessageToServer;
import com.example.chatsapp.viewmodels.MessagesViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.socket.client.Socket;

public class MessagesRepository {
    private MessagesDao dao;
    private String chatID;
    private MessagesListData messagesListData;
    private MessagesAPI api;
    String token;

    public MessagesRepository(Context context, String serverAddress, String token, String chatID) {
        LocalDatabase db = Room.databaseBuilder(context, LocalDatabase.class, "localDatabase").fallbackToDestructiveMigration().build();
        dao = db.messagesDao();
        messagesListData = new MessagesListData();
        api = new MessagesAPI(messagesListData, dao, serverAddress);
        this.token = token;
        this.chatID = chatID;
    }

    class MessagesListData extends MutableLiveData<List<Message>> {
        public MessagesListData() {
            super();
            setValue(new LinkedList<Message>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                messagesListData.postValue(dao.get(chatID));
            }).start();
        }
    }

    public LiveData<List<Message>> getAll() {
        return messagesListData;
    }


    public List<Message> get(String chatID) {
        List<Message> allMessages = messagesListData.getValue();
        List<Message> messagesFromChat = new ArrayList<>();
        for (Message msg : allMessages) {
            if (msg.getChatID().equals(chatID)) {
                messagesFromChat.add(msg);
            }
        }
        return messagesFromChat;
    }

    public void reload(EditText etMessage, MaterialButton sendButton) {
        api.getMessagesOfChat(token, chatID, etMessage, sendButton);
    }

    public void clearLocalMessages() {
        new Thread(() -> dao.clearAll()).start();
    }

    public void sendMessage(MessageToServer messageToServer, MessagesViewModel messagesViewModel, EditText etMessage, MaterialButton sendButton, Socket socket) {
        api.sendMessage(messageToServer, token, chatID, messagesViewModel, etMessage, sendButton, socket);
    }
}
