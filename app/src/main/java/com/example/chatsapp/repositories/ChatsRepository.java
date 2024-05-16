package com.example.chatsapp.repositories;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.chatsapp.LocalDatabase;
import com.example.chatsapp.apis.ChatsAPI;
import com.example.chatsapp.daos.ChatsDao;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.server_response.User;
import com.example.chatsapp.viewmodels.ChatsViewModel;

import java.util.LinkedList;
import java.util.List;

public class ChatsRepository {
    private ChatsDao dao;
    private ChatsListData chatsListData;
    private ChatsAPI api;
    String token;

    public ChatsRepository(Context context, String serverAddress, String token) {
        LocalDatabase db = Room.databaseBuilder(context, LocalDatabase.class, "localDatabase").fallbackToDestructiveMigration().build();
        dao = db.chatsDao();
        chatsListData = new ChatsListData();
        api = new ChatsAPI(chatsListData, dao, serverAddress);
        this.token = token;
    }

    class ChatsListData extends MutableLiveData<List<Chat>> {
        public ChatsListData() {
            super();
            setValue(new LinkedList<Chat>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                chatsListData.postValue(dao.index());
            }).start();
        }
    }

    public LiveData<List<Chat>> getAll() {
        return chatsListData;
    }

    public void addContact(String token, User user, TextView tvErrors, EditText idEdtContactUserName, Context context) {
        api.addContact(token, user, tvErrors, idEdtContactUserName, context);
    }

    public void deleteChatByID(String token, String id, ChatsViewModel chatsViewModel) {
        api.deleteChatByID(token, id, chatsViewModel);
    }

    public void reload() {
        api.getChats(token);
    }

    public void clearRoomChats() {
        new Thread(() -> {
            dao.clear();
        }).start();
    }
}
