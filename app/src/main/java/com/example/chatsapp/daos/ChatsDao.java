package com.example.chatsapp.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.chatsapp.entities.Chat;

import java.util.List;

@Dao
public interface ChatsDao {
    @Query("SELECT * FROM chat")
    List<Chat> index();

    @Insert
    void insert(Chat... chats);

    @Query("DELETE FROM chat")
    void clear();
}

