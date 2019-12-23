package com.example.tryauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherChat extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView teacher_chat_rv;
    private DatabaseReference firedb;
    String sid,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_chat);



        mtoolbar = findViewById(R.id.teacher_chat_toolbar);
        setSupportActionBar(mtoolbar);
        //getSupportActionBar().setTitle("Student1");

        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();
        if(bundle!=null)
        {
            sid =(String) bundle.get("sid");
            uid =(String) bundle.get("uid");
            getUserNameForToolBar(sid);
            Toast.makeText(this,sid,Toast.LENGTH_LONG).show();
        }

        firedb = FirebaseDatabase.getInstance().getReference().child("chat").child(uid).child(sid);
        firedb.keepSynced(true);


        teacher_chat_rv = findViewById(R.id.teacher_chat_rv);
        teacher_chat_rv.setHasFixedSize(true);
        teacher_chat_rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Chat, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>
                (Chat.class,R.layout.stud_teach_chat, ChatViewHolder.class,firedb) {

            @Override
            protected void populateViewHolder(ChatViewHolder chatViewHolder, final Chat chat, int i) {

                chatViewHolder.setTitle(chat.getTitle());
                chatViewHolder.setStatus(chat.getStatus());
                chatViewHolder.setDate(chat.getDate());
                chatViewHolder.setFeedback(chat.getFeedback());
                chatViewHolder.setImg();
                //System.out.println(notification.getUid() + "rv");
                chatViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Starting Chat page
                        Intent ii=new Intent(TeacherChat.this, TeacherAction.class);
                        ii.putExtra("sid", sid);
                        ii.putExtra("tid",uid);
                        ii.putExtra("title",chat.getTitle());
                        ii.putExtra("status",chat.getStatus());
                        ii.putExtra("date",chat.getDate());
                        ii.putExtra("feedback",chat.getFeedback());
                        ii.putExtra("flink",chat.getFlink());
                        startActivity(ii);
                    }
                });

            }
        };

        teacher_chat_rv.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        View mview;
        public ChatViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setDate(String date) {
            TextView textView = mview.findViewById(R.id.chat_dos);
            textView.setText(date);
        }

        public void setFeedback(String feedback) {
            TextView textView = mview.findViewById(R.id.chat_feedback);
            textView.setText(feedback);
        }

        public void setFlink(String flink) {
            //to be cont
        }

        public void setStatus(String status) {
            LinearLayout chatLinearLayout = mview.findViewById(R.id.chatLinearLayout);
            TextView textView = mview.findViewById(R.id.chat_status);
            if (status.contains("Pending"))
                textView.setText(" Status : Pending");
            else
                textView.setText(" Status : "+status);

            if (status.equals("Rejected")){
                chatLinearLayout.setBackgroundResource(R.color.statusReject);
            }

            if (status.equals("Accepted")){
                chatLinearLayout.setBackgroundResource(R.color.statusAccept);
            }
        }

        public void setTitle(String title) {
            TextView textView = mview.findViewById(R.id.chat_title);
            textView.setText(title);
        }

        public void setImg(){
            ImageView img = mview.findViewById(R.id.chat_image);
            img.setBackgroundResource(R.drawable.pdf_logo);
        }
    }


    void getUserNameForToolBar(String sid){
        DatabaseReference fireBaseDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(sid);
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
    }
}
