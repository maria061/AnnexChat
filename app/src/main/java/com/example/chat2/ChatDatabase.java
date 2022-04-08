package com.example.chat2;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Message.class, FriendUserSql.class}, version = 2, exportSchema = false)
public abstract class ChatDatabase extends  RoomDatabase{
    public abstract MessageDao messageDao();
    public abstract FriendUserSqlDao friendUserSqlDao();

}

