package com.example.tryauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherFragment extends Fragment {


    private RecyclerView list_of_stud;
    private View mView;
    private DatabaseReference firedb;
    private FirebaseUser currentUser;

    public TeacherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_teacher, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        System.out.println(uid);

        firedb = FirebaseDatabase.getInstance().getReference().child("group").child("teacher").child(uid);
        firedb.keepSynced(true);


        list_of_stud = mView.findViewById(R.id.list_of_stud);
        list_of_stud.setHasFixedSize(true);
        list_of_stud.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<StudentListForTeacherRecycler, ListOfStudentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<StudentListForTeacherRecycler, ListOfStudentViewHolder>
                (StudentListForTeacherRecycler.class,R.layout.student_list, ListOfStudentViewHolder.class,firedb) {

            @Override
            protected void populateViewHolder(ListOfStudentViewHolder notificationViewHolder, final StudentListForTeacherRecycler notification, int i) {

                notificationViewHolder.setUsn(notification.getUid());
                notificationViewHolder.setImg();
                //System.out.println(notification.getUid() + "rv");
                notificationViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Starting Chat page
                        Intent ii=new Intent(getContext(), TeacherChat.class);
                        ii.putExtra("sid", notification.getUid());
                        startActivity(ii);
                    }
                });

            }
        };

        list_of_stud.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ListOfStudentViewHolder extends RecyclerView.ViewHolder{

        View mview;
        public ListOfStudentViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setUsn(String uid){
            DatabaseReference fireBaseDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            fireBaseDataBase.keepSynced(true);

            fireBaseDataBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String usnn = dataSnapshot.child("usn").getValue().toString();
                    TextView noti_title = mview.findViewById(R.id.stud_usn);
                    noti_title.setText(usnn);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setImg(){
            ImageView img = mview.findViewById(R.id.stud_t_img);
            img.setBackgroundResource(R.drawable.customuserlogo);
        }
    }
}
