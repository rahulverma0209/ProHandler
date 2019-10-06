package com.example.tryauth;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragment extends Fragment {


    public ProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        DatabaseReference fireBaseDataBase;
        fireBaseDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        fireBaseDataBase.keepSynced(true);

        fireBaseDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_type = dataSnapshot.child("user_type").getValue().toString();
                if(user_type.equals("teacher")) {
                    loadTeacherRecycler();
                }
                else if(user_type.equals("student")) {
                    loadStudentRecycler();
                }
                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    void loadTeacherRecycler(){
        TeacherFragment teacherFragment = new TeacherFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_page_frame,teacherFragment);
        fragmentTransaction.commit();
        Toast.makeText(getContext(),"Teacher",Toast.LENGTH_LONG).show();
    }

    void loadStudentRecycler(){
        StudentFragment studentFragment = new StudentFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_page_frame,studentFragment);
        fragmentTransaction.commit();
        Toast.makeText(getContext(),"Student",Toast.LENGTH_LONG).show();
    }
}
