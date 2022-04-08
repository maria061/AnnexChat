package com.example.chat2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(Message message);


    @Query("select * from Messages where senderID in (:user1, :user2) and receiverID in (:user1, :user2) LIMIT 150;")
    List<Message> selectConversation(String user1, String user2);

    @Query("select * from Messages where senderID = :senderID and  receiverID = :receiverID;")
     List<Message> selectEntireConversation (String senderID, String receiverID);

    @Query("select * from Messages where senderID = :senderID LIMIT 10;")
    List<Message> selectLastTenMess (String senderID);

}
