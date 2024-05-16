package com.example.chatsapp;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.chatsapp.daos.ChatsDao;
import com.example.chatsapp.daos.MessagesDao;
import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.entities.Message;

@Database(entities = {Chat.class, Message.class}, version = 18)
public abstract class LocalDatabase extends RoomDatabase{
    public abstract ChatsDao chatsDao();
    public abstract MessagesDao messagesDao();
}

