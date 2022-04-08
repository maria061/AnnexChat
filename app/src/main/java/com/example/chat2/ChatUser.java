package com.example.chat2;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChatUser implements Parcelable {
    private String username;
    private String image;
    private List<String> friends; //friends' UIDs
    private List<String> inPendings; //UIDs of the users that sent a friend request to this user
    private List<String> outPendings;//UIDs of the users that didn't accept yet the friend request sent by this users

    public ChatUser(){
        this.username = "";
        this.image = "";
        this.friends = new ArrayList<>();
        this.inPendings = new ArrayList<>();
        this.outPendings = new ArrayList<>();
    }

    public ChatUser(String username, String image, List<String> friends, List<String> inPendings, List<String> outPendings) {
        this.username = username;
        this.image = image;
        this.friends = friends;
        this.inPendings= inPendings;
        this.outPendings = outPendings;
    }

    public ChatUser(String username, String image) {
        this.username = username;
        this.image = image;
        this.friends = new ArrayList<>();
        this.inPendings = new ArrayList<>();
        this.outPendings = new ArrayList<>();
    }

    public ChatUser(String username) {
        this.username = username;
        this.image = "";
        this.friends = new ArrayList<>();
        this.inPendings = new ArrayList<>();
        this.outPendings = new ArrayList<>();
    }

    protected ChatUser(Parcel in) {
        username = in.readString();
        image = in.readString();
        friends = new ArrayList<>();
        inPendings = new ArrayList<>();
        outPendings = new ArrayList<>();
        in.readList(friends, String.class.getClassLoader());
        in.readList(inPendings, String.class.getClassLoader());
        in.readList(outPendings, String.class.getClassLoader());
    }

    public static final Creator<ChatUser> CREATOR = new Creator<ChatUser>() {
        @Override
        public ChatUser createFromParcel(Parcel in) {
            return new ChatUser(in);
        }

        @Override
        public ChatUser[] newArray(int size) {
            return new ChatUser[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() { return  image; }

    public void setImage(String image) {this.image = image; }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getInPendings() {
        return inPendings;
    }

    public void setInPendings(List<String> inPendings) {
        this.inPendings = inPendings;
    }

    public List<String> getOutPendings() {
        return outPendings;
    }

    public void setOutPendings(List<String> outPendings) {
        this.outPendings = outPendings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(image);
        dest.writeList(friends);
        dest.writeList(inPendings);
        dest.writeList(outPendings);
    }

    @NonNull
    @Override
    public String toString() {
        return username;
    }
}
