package com.example.tryauth;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TeacherAction extends AppCompatActivity {

    private Toolbar mtoolbar;
    private TextView ta_title,ta_date,ta_status,ta_stud_msg,ta_teach_msg;
    private String tid,sid,mStatus,flink,feed;
    private Button ta_a_btn,ta_r_btn,ta_d_btn;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_action);

        mtoolbar = findViewById(R.id.teacher_action_toolbar);
        setSupportActionBar(mtoolbar);

        ta_title = findViewById(R.id.ta_title);
        ta_date = findViewById(R.id.ta_date);
        ta_teach_msg = findViewById(R.id.ta_teach_msg);
        ta_status = findViewById(R.id.ta_status);
        ta_stud_msg = findViewById(R.id.ta_stud_msg);

        ta_a_btn = findViewById(R.id.ta_a_btn);
        ta_r_btn = findViewById(R.id.ta_r_btn);
        ta_d_btn = findViewById(R.id.ta_d_btn);


        ta_teach_msg.setVisibility(View.INVISIBLE);
        ta_a_btn.setVisibility(View.INVISIBLE);
        ta_r_btn.setVisibility(View.INVISIBLE);



        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();
        if(bundle!=null)
        {
            sid =(String) bundle.get("sid");
            tid =(String) bundle.get("tid");

            ta_title.setText((String) bundle.get("title"));
            ta_date.setText((String) bundle.get("date"));
            flink = (String) bundle.get("flink");

            mStatus = (String) bundle.get("status");
            ta_status.setText(mStatus);

            if (((String) bundle.get("status")).contains("Pending"))
            {
                ta_teach_msg.setVisibility(View.VISIBLE);
                ta_a_btn.setVisibility(View.VISIBLE);
                ta_r_btn.setVisibility(View.VISIBLE);
                ta_status.setText("Pending");
            }

            ta_stud_msg.setText((String) bundle.get("feedback"));

            ta_a_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStatus("Accepted");
                }
            });

            ta_r_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setStatus("Rejected");
                }
            });

            ta_d_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(flink));
                    startActivity(browserIntent);
                }
            });
        }

    }

    void setStatus(String status){
        String key = mStatus.split("z")[1];
        Toast.makeText(TeacherAction.this,key,Toast.LENGTH_LONG).show();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("chat").child(tid).child(sid).child(key);
        firebaseDatabase.keepSynced(true);

        HashMap<String,Object> statusData = new HashMap<>();
        statusData.put("status",status);

        firebaseDatabase.updateChildren(statusData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(TeacherAction.this,"Done",Toast.LENGTH_LONG).show();

            }
        });
    }
}
