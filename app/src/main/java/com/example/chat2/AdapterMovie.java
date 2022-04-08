package com.example.chat2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AdapterMovie extends ArrayAdapter<Movie> {
    private int resID;

    public AdapterMovie(@NonNull Context context, int resource, @NonNull List<Movie> objects) {
        super(context, resource, objects);
        this.resID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(this.resID, null);
        Movie movie = getItem(position);

        TextView movieTitle = v.findViewById(R.id.tv_movieItemTitle);
        movieTitle.setText(movie.getTitle());

        TextView suggestedBy = v.findViewById(R.id.tv_movieItemSuggestedBy);
        suggestedBy.setText(movie.getSuggestedBy());

        TextView viewed = v.findViewById(R.id.tv_movieItemViewed);
        if(movie.isViewed()){
            viewed.setText("viewed");
        }else {
            viewed.setText("unviewed");
        }

        return v;
    }
}
