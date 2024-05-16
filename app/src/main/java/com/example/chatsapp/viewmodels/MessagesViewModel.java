package com.example.chatsapp.viewmodels;


import android.content.Context;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.entities.Message;
import com.example.chatsapp.repositories.ChatsRepository;
import com.example.chatsapp.repositories.MessagesRepository;
import com.example.chatsapp.server_response.MessageToServer;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import io.socket.client.Socket;

public class MessagesViewModel extends ViewModel {
    // The repository will handle the interaction with the server & the database!
    private MessagesRepository mRepository;

    // The LiveData will be observed by the MessagesActivity, and will update the RecyclerView when the data changes!
    // Note that Mutable Live Data is not used here, because the data *is not changed* in this class!
    // In other words, *ONLY THE DB* will change the data, and the DB will notify the LiveData, which will notify the MessagesActivity!
    private LiveData<List<Message>> messages;

    // Initializing the repository:

    public void setRepository(Context context, String serverAddress, String token, String chatID) {
        mRepository = new MessagesRepository(context, serverAddress, token, chatID);
        // Getting all the posts (the LiveData) from the repository:
        LiveData<List<Message>> reversedList = mRepository.getAll();
        messages = mRepository.getAll();
    }


    // Note that the 'get' returns a list of LiveData!
    public LiveData<List<Message>> get() {
        return messages;
    }

    public List<Message> get(String chatID) {
        return mRepository.get(chatID);
    }

    public void reload(EditText etMessage, MaterialButton sendButton) {
        mRepository.reload(etMessage, sendButton);
        messages = mRepository.getAll();
    }

    public void sendMessage(MessageToServer messageToServer, MessagesViewModel messagesViewModel, EditText etMessage, MaterialButton sendButton, Socket socket) {
        mRepository.sendMessage(messageToServer, messagesViewModel, etMessage, sendButton, socket);
    }
}
