package com.example.chatsapp.viewmodels;


import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.repositories.ChatsRepository;
import com.example.chatsapp.server_response.User;

import org.w3c.dom.Text;

import java.util.List;

public class ChatsViewModel extends ViewModel {
    // The repository will handle the interaction with the server & the database!
    private ChatsRepository mRepository;

    // The LiveData will be observed by the ChatsActivity, and will update the RecyclerView when the data changes!
    // Note that Mutable Live Data is not used here, because the data *is not changed* in this class!
    // In other words, *ONLY THE DB* will change the data, and the DB will notify the LiveData, which will notify the ChatsActivity!
    private LiveData<List<Chat>> chats;

    // Initializing the repository:

    public void setRepository(Context context, String serverAddress, String token) {
        mRepository = new ChatsRepository(context, serverAddress, token);
        // Getting all the posts (the LiveData) from the repository:
        chats = mRepository.getAll();
    }


    // Note that the 'get' returns a list of LiveData!
    public LiveData<List<Chat>> get() {
        return chats;
    }


    public void addContact(String token, User user, TextView tvErrors, EditText idEdtContactUserName, Context context) {
        mRepository.addContact(token, user, tvErrors, idEdtContactUserName, context);
        chats = mRepository.getAll();
    }

    public void deleteChat(String token, String id, ChatsViewModel chatsViewModel) {
        mRepository.deleteChatByID(token, id, chatsViewModel);
        chats = mRepository.getAll();
    }

    public void reload() {
        mRepository.reload();
        chats = mRepository.getAll();
    }

    public void clearRoomChats() {
        mRepository.clearRoomChats();
    }
}
