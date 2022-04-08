package com.example.chat2;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Messages")
public class Message {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    private String message;

    @NonNull
    private String senderID;

    @NonNull
    private String receiverID;

    @NonNull
    private String createdAt;

    public Message(){

    }
    public Message(@NonNull String id, @NonNull String message, @NonNull String senderID, @NonNull String receiverID, @NonNull String createdAt) {
        this.id = id;
        this.message = message;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    @NonNull
    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(@NonNull String senderID) {
        this.senderID = senderID;
    }

    @NonNull
    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(@NonNull String receiverID) {
        this.receiverID = receiverID;
    }

    @NonNull
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", senderID='" + senderID + '\'' +
                ", receiverID='" + receiverID + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
