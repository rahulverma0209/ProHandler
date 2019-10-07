package com.example.tryauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherChat extends AppCompatActivity {

    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_chat);


        String sid;
        mtoolbar = findViewById(R.id.teacher_chat_toolbar);
        setSupportActionBar(mtoolbar);
        //getSupportActionBar().setTitle("Student1");

        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();
        if(bundle!=null)
        {
            sid =(String) bundle.get("sid");
            getUserNameForToolBar(sid);
            Toast.makeText(this,sid,Toast.LENGTH_LONG).show();
        }
    }

    String getUserNameForToolBar(String uid){
        DatabaseReference fireBaseDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        fireBaseDataBase.keepSynced(true);

        fireBaseDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usnn = dataSnapshot.child("usn").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();

                //Setting ToolBarName
                getSupportActionBar().setTitle(usnn + " ( " + name + " )");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return "";
    }
}
