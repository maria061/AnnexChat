package com.example.chat2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import androidx.room.Room;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatDialogActivity extends AppCompatActivity {

    private ChatUser user;
    private ChatUser friend;
    private FirebaseUser firebaseUser;
    private DatabaseReference messagesRef;
    private String friendUID;
    static private List<String> messagesList;
    private ChatDatabase chatDatabase = null;
    ArrayAdapter<String> arrayAdapter;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        friend = intent.getParcelableExtra("friend");
        friendUID = intent.getStringExtra("friendUID");
        chatDatabase = Room.databaseBuilder(this, ChatDatabase.class, "AnnexChat").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        messagesList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userUID = firebaseUser.getUid();
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        List<Message> messages = chatDatabase.messageDao().selectConversation(userUID, friendUID);
        for (Message m : messages) {
            String senderUsername;
            if (m.getSenderID().equals(friendUID)) {
                senderUsername = chatDatabase.friendUserSqlDao().selectUsernameByUID(friendUID);
            } else {
                senderUsername = user.getUsername();
            }
            String messageText = senderUsername + ": " + m.getMessage();
            messagesList.add(messageText);
        }

        //load the messages in list view
        ListView lv = findViewById(R.id.lv_messagesList);
        arrayAdapter = new ArrayAdapter<>(getBaseContext(),android.R.layout.simple_list_item_1 , messagesList);
        lv.setAdapter(arrayAdapter);

        //listen for new messages sent by THE CURRENT USER FROM ANOTHER DEVICE
        //in this case we will have to add the new sent messages to the local SQLite database
        DatabaseReference refSent = messagesRef.child(userUID).child(friendUID);
        refSent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> sentMessagesSnapshot = dataSnapshot.getChildren(); //new sent messages from FIREBASE database
                for(DataSnapshot sentMessage : sentMessagesSnapshot){
                    Message sentMsg = sentMessage.getValue(Message.class);
                    //add the new sent messages in the SQLite database
                    chatDatabase.messageDao().insertMessage(sentMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //listen for new messages sent by the corespondent
        //in this case we will have to add the new received messages to the local SQLite database
        DatabaseReference refReceived = messagesRef.child(friendUID).child(userUID);
        refReceived.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> receivedMessagesSnapshot = dataSnapshot.getChildren(); //the new messages received (taken from firebase)
                for(DataSnapshot receivedMessage : receivedMessagesSnapshot){
                    Message receivedMsg = receivedMessage.getValue(Message.class);
                    chatDatabase.messageDao().insertMessage(receivedMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



            final EditText etMessage = findViewById(R.id.ET_chatDialog_writeMsg);
            ImageButton sendBtn = findViewById(R.id.imgBtnSend_chatDialog);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageString = etMessage.getText().toString();
                    if (messageString != null && !messageString.isEmpty()) {
                        //add the message in firebase database
                        //in firebase database the messages are stored as follows:
                        //a table called messages that contains all messages
                        //this table contains as children the users' UIDs
                        //any user UID child contains all messages sent by that user (divided by the UID of the receiver)
                        //so, to get a message with a known sender, receiver and id (key), the next steps are required:
                        //Messages -> user (sender) UID -> receiver UID -> key -> wanted message

                        DatabaseReference ref = messagesRef.child(userUID).child(friendUID);
                        String messageId = ref.push().getKey();
                        //create a Message instance
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String createdAt = sdf.format(timestamp);
                        Message message = new Message(messageId, messageString, userUID, friendUID, createdAt);
                        ref.child(messageId).setValue(message);

                        //add the message in sqlite
                        //insert the message iin sqlite
                        chatDatabase.messageDao().insertMessage(message);

                        //add the message to the messageList
                        messagesList.add(user.getUsername() + ": " + message.getMessage());
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });

        }

}
