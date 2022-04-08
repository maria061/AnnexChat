package com.example.chat2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageFriendActivity extends AppCompatActivity {

    private ChatUser user;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private ChatUser friend;
    private String friendUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friend);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        firebaseUser = intent.getParcelableExtra("firebaseUser");
        friend = intent.getParcelableExtra("friend");
        friendUID = intent.getStringExtra("friendUID");

        TextView friendNameTV = findViewById(R.id.friend_name);
        friendNameTV.setText(friend.getUsername());

        TextView tvMessageFriend = findViewById(R.id.tv_messageFriend);
        tvMessageFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intentMessage = new Intent(ManageFriendActivity.this, ChatDialogActivity.class);
             intentMessage.putExtra("user", user);
             intentMessage.putExtra("friend", friend);
             intentMessage.putExtra("friendUID", friendUID);
             startActivity(intentMessage);
            }
        });

        EditText etSuggestedMovie = findViewById(R.id.et_suggestedMovie);
        final String suggestedMovie = etSuggestedMovie.getText().toString();

        TextView suggestMovie = findViewById(R.id.tv_suggestMovie_manFriend);
        suggestMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggestedMovie != null && !suggestedMovie.isEmpty()){
                    Movie movie = new Movie(suggestedMovie, user.getUsername(), false);

                    //add the suggested movie in realtime database
                    ref = FirebaseDatabase.getInstance().getReference("Movies").child(friendUID);
                    String id = ref.push().getKey();
                    ref.child(id).setValue(movie);

                    //Toast.makeText(this, R.string.movieSugested, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
