package com.example.chat2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddFriendActivity extends AppCompatActivity {

    private ChatUser user;
    private FirebaseUser firebaseUser;
    private EditText etFriendName;
    private DatabaseReference usersRef;
    private ChatDatabase chatDatabase = null;
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        firebaseUser = intent.getParcelableExtra("firebaseUser");
        chatDatabase = Room.databaseBuilder(this, ChatDatabase.class, "AnnexChat").allowMainThreadQueries().build();
    }

    private boolean regexValidation (String regexPattern, String textToCheck){
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.matches();
    }

    //verify if the username is valid
    private boolean usernameValidation(String username){
        //verify if the username is already taken
        if(!regexValidation("\\w+", username))//username contains non-word characters
            return false;
        else if(username.length() < 4)
            return false;
        else if(username.length() > 20)
            return false;
        else return true;

    }

    //verify if the username is valid as a friend
    private boolean usernameValidAsFriend(String friendUsername){
        if(friendUsername == user.getUsername()){
            Toast.makeText(AddFriendActivity.this, R.string.friendRequestToItself, Toast.LENGTH_LONG).show();
            return false;
        }

        return usernameValidation(friendUsername);
    }

    private boolean canBeFriend(String uid){
        //check if the user is already in the friend list
        List<String> friends = user.getFriends();
        for(String friendUID : friends){
            if(uid.equals(friendUID)){
                //the user is already in the friend list
                return false;
            }
        }

        //check if the user is already in the incoming pending list
        List<String> inRequests = user.getInPendings();
        for(String requesterUID : inRequests){
            if(uid.equals(requesterUID)){
                //the user is already in the friend list
                return false;
            }
        }

        //check if the user is already in the outgoing pending list
        List<String> outRequests = user.getOutPendings();
        for(String outRequesterUID : outRequests){
            if(uid.equals(outRequesterUID)){
                //the user is already in the friend list
                return false;
            }
        }

        return true;
    }

    public void addFriend(View view) {

        etFriendName = findViewById(R.id.ET_addFriend_friendUsername);
        final String friendUsername = etFriendName.getText().toString().trim();


            //searching the user with the specified username
            usersRef = ref;

            if(usernameValidAsFriend(friendUsername)==false){
                Toast.makeText(AddFriendActivity.this, R.string.noUserFound, Toast.LENGTH_LONG).show();
            }
            else {
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int founded = 0;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            ChatUser requestedUser = userSnapshot.getValue(ChatUser.class);
                            String uid = userSnapshot.getKey();


                            //verify if the user has the specified username
                            if (requestedUser.getUsername().equals(friendUsername)) {
                                //user founded
                                founded = 1;

                                //check if the user can be added as friend

                                if(canBeFriend(uid)) {
                                    //update the out pending list of the current user
                                    List<String> outPendings = user.getOutPendings();
                                    if (outPendings == null) {
                                        outPendings = new ArrayList<>();
                                    }
                                    outPendings.add(uid);
                                    user.setOutPendings(outPendings);

                                    //update in pending list of the receiver user
                                    List<String> inPendings = requestedUser.getInPendings();
                                    if (inPendings == null) {
                                        inPendings = new ArrayList<>();
                                    }
                                    inPendings.add(firebaseUser.getUid());
                                    requestedUser.setInPendings(inPendings);


                                    //update the current user in realtime database
                                    ref.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            } else {
                                                Toast.makeText(AddFriendActivity.this, R.string.friendRequestFailed, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    //update the requested user in realtime database
                                    ref.child(uid).setValue(requestedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddFriendActivity.this, R.string.friendRequestSentSuccess, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(AddFriendActivity.this, R.string.friendRequestFailed, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    //add the possible friend to the SQL friends UID - friends username bridge table
                                    FriendUserSql friendUserSql = new FriendUserSql(uid, requestedUser.getUsername());
                                    chatDatabase.friendUserSqlDao().insertFriend(friendUserSql);

                                }

                            }
                        }

                        if (founded == 0) {
                            //no user with the specified username was found
                            Toast.makeText(AddFriendActivity.this, R.string.noUserFound, Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
    }

}
