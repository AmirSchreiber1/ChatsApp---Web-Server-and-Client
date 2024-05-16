package com.example.chatsapp.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.chatsapp.entities.Chat;
import com.example.chatsapp.entities.Message;

import java.util.List;

@Dao
public interface MessagesDao {

    @Query("SELECT * FROM message WHERE chatID = :chatID ORDER BY " +
            "SUBSTR(timeCreated, 12, 4) ||" +
            "CASE " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Jan' THEN '01' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Feb' THEN '02' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Mar' THEN '03' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Apr' THEN '04' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'May' THEN '05' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Jun' THEN '06' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Jul' THEN '07' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Aug' THEN '08' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Sep' THEN '09' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Oct' THEN '10' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Nov' THEN '11' " +
            "WHEN SUBSTR(timeCreated, 5, 3) = 'Dec' THEN '12' " +
            "END || SUBSTR(timeCreated, 9, 2) || SUBSTR(timeCreated, 17, 8) DESC")
    List<Message> get(String chatID);

    @Insert
    void insert(Message... messages);

    @Query("DELETE FROM message WHERE chatID = :chatID")
    void clear(String chatID);

    @Query("DELETE FROM message")
    void clearAll();
}

