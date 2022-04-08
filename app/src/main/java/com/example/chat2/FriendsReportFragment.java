package com.example.chat2;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FriendsReportFragment extends Fragment {

    private Button btnCreate;
    private Button btnCreateAndSave;
    private ChatDatabase chatDatabase = null;
    private String fileName = "FriendsReport";

    public FriendsReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_friends_report, container, false);

        chatDatabase = Room.databaseBuilder(getActivity(), ChatDatabase.class, "AnnexChat").allowMainThreadQueries().build();
        List<String> friends = chatDatabase.friendUserSqlDao().selectFirstTenFriends();

        String report = "";
        for( String f : friends){
            report += "\n" +f;
        }
        final String reportFinal = report;

        btnCreate = v.findViewById(R.id.btnCreateRepFriends);
        btnCreateAndSave = v.findViewById(R.id.btnCreateAndSaveRepFriends);
        final TextView tv = v.findViewById(R.id.tvFriendsRep);


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
        return  v;
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
