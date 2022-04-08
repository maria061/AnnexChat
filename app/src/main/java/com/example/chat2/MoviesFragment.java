package com.example.chat2;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {

    private ChatUser user;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private List<Movie> movies;

    public MoviesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View v = inflater.inflate(R.layout.fragment_movies, container, false);
        user = this.getArguments().getParcelable("user");
        firebaseUser = this.getArguments().getParcelable("firebaseUser");
        final ListView lv = v.findViewById(R.id.lv_moviesList);
        movies = new ArrayList<>();
        movies.add(new Movie("Home Alone 2: Lost in New York", "test", false));
        movies.add(new Movie("Zootopia", "test", false));
        movies.add(new Movie("Frozen", "test2", true));

        //take the movies from database
        ref = FirebaseDatabase.getInstance().getReference("Movies").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for( DataSnapshot movieSnapshot : dataSnapshot.getChildren()){
                    Movie movie = movieSnapshot.getValue(Movie.class);
                    movies.add(movie);
                }

                if(movies.size()>0) {
                    AdapterMovie adapter = new AdapterMovie(getActivity().getApplicationContext(), R.layout.movie_item, movies);
                    lv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), MovieActivity.class);
                    Movie movie = movies.get(position);
                    intent.putExtra("movie", movie);

                    startActivity(intent);
                    return true;
                }
            });



        return v;
    }

}
