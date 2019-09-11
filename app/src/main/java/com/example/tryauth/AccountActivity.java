package com.example.tryauth;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class AccountActivity extends AppCompatActivity {

    private DatabaseReference fireBaseDataBase;
    private FirebaseUser currentUser;

    TextView username,usn;
    Button setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        username = findViewById(R.id.username);
        usn = findViewById(R.id.usn);
        setting = findViewById(R.id.setting);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        //for student
        fireBaseDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        fireBaseDataBase.keepSynced(true);

        fireBaseDataBase.addValueEventListener(new ValueEventListener() {
            //For retrieving the data
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String usnn = dataSnapshot.child("usn").getValue().toString();

                username.setText(name);
                usn.setText(usnn);
            }

            //For handling errors
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
