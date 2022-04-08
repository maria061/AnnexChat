package com.example.chat2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IncomingPendingFragment extends Fragment {
    private ChatUser user;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    public List<ChatUser> inPendings;
    private List<String> inPendingUIDs;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        final View v = inflater.inflate(R.layout.fragment_incoming_pending, container, false);
        user = this.getArguments().getParcelable("user");
        firebaseUser = this.getArguments().getParcelable("firebaseUser");
        ref = FirebaseDatabase.getInstance().getReference("Users");

        //inUids - list of the incoming friend requests (contains only the UIDs of the users who sent a friend request)
        final List<String> inUIDs = user.getInPendings();
        inPendings = new ArrayList<>();
        inPendingUIDs = new ArrayList<>();
        DatabaseReference friendRef;

        //find the users that are in the incoming pending list of the current user
        //their UIDs are in the inUIDS list
        if(inUIDs.size() > 0) {
            for (String uid : inUIDs) {

                friendRef = ref.child(uid);
                final String uidFinal = uid;
                friendRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChatUser chatUser = dataSnapshot.getValue(ChatUser.class);
                        inPendings.add(chatUser);
                        inPendingUIDs.add(uidFinal);
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
                if (!inPendings.isEmpty()) {
                    AdapterFriend adapter = new AdapterFriend(getActivity().getApplicationContext(), R.layout.friend_item_layout, inPendings);
                    ListView lv = v.findViewById(R.id.inPendingList);
                    lv.setAdapter(adapter);

                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(),IncomingFriendRequestActivity.class);

                            ChatUser requester = inPendings.get(position);
                            String requesterUID = inPendingUIDs.get(position);
                            intent.putExtra("requester", requester);
                            intent.putExtra("requesterUID", requesterUID);
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

