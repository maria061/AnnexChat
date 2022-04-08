package com.example.chat2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class IncomingFriendRequestActivity extends AppCompatActivity {
    private ChatUser user;
    private FirebaseUser firebaseUser;
    private ChatUser requester;
    private String requesterUID;
    private DatabaseReference refCurrentUser;
    private DatabaseReference refRequester;
    private String userUID;
    private ChatDatabase chatDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_friend_request);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        firebaseUser = intent.getParcelableExtra("firebaseUser");
        requester = intent.getParcelableExtra("requester");
        requesterUID = intent.getStringExtra("requesterUID");

        userUID = firebaseUser.getUid();
        refCurrentUser = FirebaseDatabase.getInstance().getReference("Users").child(userUID);
        refRequester = FirebaseDatabase.getInstance().getReference("Users").child(requesterUID);
        chatDatabase = Room.databaseBuilder(this, ChatDatabase.class, "AnnexChat").allowMainThreadQueries().build();
    }

    public void acceptFriendRequest(View view) {
        //accept the friend request

        //current user: add the requester as friend
        user.getFriends().add(requesterUID);

        //current user: delete the incoming request
        List<String> inRequests = user.getInPendings();
        inRequests.remove(requesterUID);
        user.setInPendings(inRequests);

        //update the current user in database
        refCurrentUser.setValue(user);

        //requester: add the current user as friend
        List<String> friends2 = requester.getFriends();
        friends2.add(userUID);
        requester.setFriends(friends2);

        //requester: delete the outgoing request
        List<String> outRequests = user.getOutPendings();
        outRequests.remove(userUID);
        user.setOutPendings(outRequests);

        //update the requester in database
        refRequester.setValue(requester);

        //add the friend to the sqlite database
        FriendUserSql friendUserSql = new FriendUserSql(requesterUID, requester.getUsername());
        chatDatabase.friendUserSqlDao().insertFriend(friendUserSql);

    }

    public void rejectFriendRequest(View view) {
        //reject the friend request

        //current user: delete the incoming request
        List<String> inRequests = user.getInPendings();
        inRequests.remove(requesterUID);
        user.setInPendings(inRequests);

        //update the current user in database
        refCurrentUser.setValue(user);

        //requester: delete the outgoing request
        List<String> outRequests = user.getOutPendings();
        outRequests.remove(userUID);
        user.setOutPendings(outRequests);

        //update the requester in database
        refRequester.setValue(requester);

    }
}
