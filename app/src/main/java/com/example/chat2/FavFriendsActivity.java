package com.example.chat2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FavFriendsActivity extends AppCompatActivity {

    //in this activity we'll draw a bar chart for the 5 most favorite friends (the friends you sent most messages to)


    private ChatUser user;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private DatabaseReference friendRef;
    private ChatDatabase chatDatabase = null;
    private int friendsNo;
    private String[] friendsNames;
    private String[] topFriendsNames;
    private int[] rightValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        user= (ChatUser)intent.getParcelableExtra("user");
        firebaseUser = intent.getParcelableExtra("firebaseUser");
        chatDatabase = Room.databaseBuilder(this, ChatDatabase.class, "AnnexChat").allowMainThreadQueries().build();
        ref = FirebaseDatabase.getInstance().getReference("messages").child(firebaseUser.getUid());
        List<String> friendsUids = user.getFriends();
        friendsNo = friendsUids.size();
        String friendName;
        //long messagesNo; // the number of messages sent to a friend
        final long[] messagesNos = new long[friendsNo];
        friendsNames = new String[200];
        int i = 0;
        for (String friendUid : friendsUids) {
            friendName = chatDatabase.friendUserSqlDao().selectUsernameByUID(friendUid);//get the name of the friend
            friendsNames[i] = friendName;//add the name of the friend in the friends names list
            friendRef = ref.child(friendUid);
            final int j = i;
            friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long messagesNo = dataSnapshot.getChildrenCount();//get the number of messages sent to the specified user
                    messagesNos[j] = messagesNo;//add the number of messages in the number of messages list

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            i++;

        }


        //calculating the values for drawing the chart:
        //calculating the first 5 friends the current user sent most messages to
        //descending order the  number of messages list
        //in the same time we ll change the values in the friends names list
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    long aux;
                                    String aux2;
                                    for (int ind = 0; ind < friendsNo; ind++) {
                                        for (int ind2 = ind + 1; ind2 < friendsNo; ind2++) {
                                            if (messagesNos[ind] < messagesNos[ind2]) {
                                                aux = messagesNos[ind];
                                                messagesNos[ind] = messagesNos[ind2];
                                                messagesNos[ind2] = aux;
                                                aux2 = friendsNames[ind];
                                                friendsNames[ind] = friendsNames[ind2];
                                                friendsNames[ind2] = aux2;
                                            }
                                        }
                                    }

                                    //store the top5 (or less in case the user has less than 5 fr) friends in a different array for easier use
                                    //calculate the total number of messages sent to these 5 friends (or less in case the user has less than 5 fr)
                                    long[] topFriends = new long[5];
                                    long totalMessages = 0;
                                    for (int ind = 0; ind < 5 && ind < friendsNo; ind++) {
                                        topFriends[ind] = messagesNos[ind];
                                        totalMessages += topFriends[ind];
                                    }

                                    //store the top5 (or less in case the user has less than 5 fr) friends names in a different array for easier use
                                    topFriendsNames = new String[5];
                                    for (int ind = 0; ind < 5 && ind < friendsNo; ind++) {
                                        topFriendsNames[ind] = friendsNames[ind];
                                    }

                                    //String str = topFriendsNames[0];
                                    //update the number of friends to the actual number of friends in the top friends list
                                    if (friendsNo > 5) {
                                        friendsNo = 5;
                                    }

                                    //store the names of the first 5 friends


                                    //calculate the weight of every friend in the total messages sent
                                    float[] friendsWeight = new float[5];
                                    for (int ind = 0; ind < friendsNo; ind++) {
                                        friendsWeight[ind] = topFriends[ind] / totalMessages;
                                    }

                                    //calculate the length of every bar depending on the friend weight
                                    //the biggest is the longest (600)
                                    //the others will get their values as weight in the biggest one


                                     rightValues = new int[5];
                                    //adding the left value (the right value inlcudes the left value)
                                    for (int ind = 0; ind < friendsNo; ind++) {
                                        rightValues[ind] = 200;
                                    }
                                    rightValues[0] += 600;//the length of the top 1 friend


                                    for (int ind = 1; ind < friendsNo; ind++) {
                                        rightValues[ind] += Math.round((friendsWeight[ind] * 600) / friendsWeight[0]);
                                    }

                                }
                            },
                delay);

//        String str = topFriendsNames[0];
        Drawing view = new Drawing(this, friendsNo, topFriendsNames, rightValues);
        setContentView(view);
    }
}
