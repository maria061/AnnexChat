package com.example.chat2;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MessagesReportFragment extends Fragment {

    private Button btnCreate;
    private Button btnCreateAndSave;
    private ChatDatabase chatDatabase = null;
    private String fileName = "MessagesReport";


    public MessagesReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages_report, container, false);

        chatDatabase = Room.databaseBuilder(getActivity(), ChatDatabase.class, "AnnexChat").allowMainThreadQueries().build();
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<Message> messages = chatDatabase.messageDao().selectLastTenMess(userUID);

        String report = "";
        for( Message m : messages){
            report += "\nTo " + chatDatabase.friendUserSqlDao().selectUsernameByUID(m.getReceiverID()) + ": " + m.getMessage();
        }
        final String reportFinal = report;

        btnCreate = v.findViewById(R.id.btnCreateRep);
        btnCreateAndSave = v.findViewById(R.id.btnCreateAndSaveRep);
        final TextView tv = v.findViewById(R.id.tvMessagesRep);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(reportFinal);
            }
        });

        btnCreateAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(reportFinal);
                saveFile(fileName, reportFinal);

            }
        });

        return v;
    }

    public void saveFile(String fileName, String report){
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(report);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
