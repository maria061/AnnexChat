package com.example.chat2;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//this class links the senderUID from Message class to the sender's username
@Entity(tableName = "Friends")
public class FriendUserSql {

    @PrimaryKey
    @NonNull
    private String UID;

    @NonNull
    private String username;

    public FriendUserSql(@NonNull String UID, @NonNull String username) {
        this.UID = UID;
        this.username = username;
    }

    @NonNull
    public String getUID() {
        return UID;
    }

    public void setUID(@NonNull String UID) {
        this.UID = UID;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "FriendUserSql{" +
                "UID='" + UID + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
