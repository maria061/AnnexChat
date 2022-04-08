package com.example.chat2;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String title;
    private String suggestedBy;
    private boolean viewed;

    public Movie()

    {
        title = "";
        suggestedBy = "";
        viewed = false;
    }

    public Movie(String title, String suggestedBy, boolean viewed) {
        this.title = title;
        this.suggestedBy = suggestedBy;
        this.viewed = viewed;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        suggestedBy = in.readString();
        viewed = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuggestedBy() {
        return suggestedBy;
    }

    public void setSuggestedBy(String suggestedBy) {
        this.suggestedBy = suggestedBy;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(suggestedBy);
        dest.writeBoolean(viewed);
    }
}
