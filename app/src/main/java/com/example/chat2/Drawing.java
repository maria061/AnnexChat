package com.example.chat2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Drawing extends View {
    private Canvas canvas;
    int friendsNo;
    String[] friendsNames = {"test04", "test02", "test05", "test06"};
    int[] rightValues={700, 400, 440, 393, 432};
    FirebaseUser user;
    DatabaseReference ref;
    ChatUser chatUser;
    private ChatDatabase chatDatabase = null;

    public Drawing(Context context, int friendsNo, String[] friendsNames, int[] rightValues){
        super(context);
        this.friendsNo = friendsNo;
       // this.friendsNames = friendsNames;
        //this.rightValues = rightValues;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint instrument=new Paint();

        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatUser = dataSnapshot.getValue(ChatUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        int left = 300, top = 200, bottom = 300;
        instrument.setStyle(Paint.Style.FILL_AND_STROKE);
        for(int i=0; i<friendsNo; i++ ) {
            instrument.setColor(Color.BLACK);
            instrument.setTextSize(40);
            //canvas.drawText(friendsNames[i], left-150, top+30, instrument);
            instrument.setColor(Color.MAGENTA);
            canvas.drawRect(left, top, rightValues[i], bottom, instrument);
            canvas.drawText(friendsNames[i], rightValues[i] +10, top+30, instrument);
            top +=110;
            bottom += 110;
        }
    }
}
