package com.example.chat2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdapterFriend extends ArrayAdapter<ChatUser> {
    private int resID;

    public AdapterFriend(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.resID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(this.resID, null);
        ChatUser friend = getItem(position);

        TextView friendName = v.findViewById(R.id.friendItem);
        friendName.setText(friend.getUsername());

        return v;
    }
}
