package com.example.chat2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private ChatUser user;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    public List<ChatUser> friends;
    private ImageButton btnAddFriend;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View v = inflater.inflate(R.layout.friends_fragment, container, false);
        user = this.getArguments().getParcelable("user");
        firebaseUser = this.getArguments().getParcelable("firebaseUser");
        ref = FirebaseDatabase.getInstance().getReference("Users");

        btnAddFriend = v.findViewById(R.id.btnAddFriend_friendsFrag);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AddFriendActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("firebaseUser", firebaseUser);

                startActivity(intent);
            }
        });


        List<String> inUIDs = user.getInPendings();
        List<String> outUIDs = user.getOutPendings();
        final List<String> friendsUIDs = user.getFriends();

        friends = new ArrayList<>();
        final List <String> friendsUids = new ArrayList<>();
        DatabaseReference friendRef;
// for outgoing requests fragment
//        if(outUIDs.size() > 0 ) {
//            for (String uid : outUIDs) {
//                friendRef = ref.child(uid);
//                friendRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        ChatUser chatUser = dataSnapshot.getValue(ChatUser.class);
//                        friends.add(chatUser);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        }

        if(friendsUIDs.size() > 0) {
            for (String uid : friendsUIDs) {
                friendRef = ref.child(uid);
                final String uidFinal = uid;

                friendRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChatUser chatUser = dataSnapshot.getValue(ChatUser.class);
                        friends.add(chatUser);
                        friendsUids.add(uidFinal);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!friends.isEmpty()) {
                    AdapterFriend adapter = new AdapterFriend(getActivity().getApplicationContext(), R.layout.friend_item_layout, friends);
                    ListView lv = v.findViewById(R.id.friendsList);
                    lv.setAdapter(adapter);

                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), ManageFriendActivity.class);

                            ChatUser friend = friends.get(position);
                            String friendUID = friendsUids.get(position);
                            intent.putExtra("friend", friend);
                            intent.putExtra("friendUID", friendUID);
                            intent.putExtra("user", user);
                            intent.putExtra("firebaseUser", firebaseUser);
                            startActivity(intent);

                            return true;
                        }
                    });
                }else{
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
