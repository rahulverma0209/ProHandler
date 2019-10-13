package com.example.tryauth;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentChatFragment extends Fragment {

    private RecyclerView student_chat_recycler_view;
    private View mView;
    private FirebaseUser currentUser;
    private DatabaseReference fireBaseDataBase;
    FloatingActionButton msgButton;
    private String uid,tid;

    public StudentChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_student_chat, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tid = bundle.getString("tid");
        }


        fireBaseDataBase = FirebaseDatabase.getInstance().getReference().child("chat").child(tid).child(uid);
        fireBaseDataBase.keepSynced(true);

        student_chat_recycler_view = mView.findViewById(R.id.student_chat_recycler_view);
        student_chat_recycler_view.setHasFixedSize(true);
        student_chat_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        msgButton = mView.findViewById(R.id.student_floating_button);
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ChatStudent.class);
                intent.putExtra("uid",uid);
                intent.putExtra("tid",tid);
                startActivity(intent);
            }
        });


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(getContext(),tid,Toast.LENGTH_LONG).show();


        FirebaseRecyclerAdapter<Chat, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>
                (Chat.class,R.layout.stud_teach_chat, ChatViewHolder.class,fireBaseDataBase) {

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
                        /*Intent ii=new Intent(getContext(), TeacherChat.class);
                        ii.putExtra("sid", chat.getUid());
                        ii.putExtra("uid",uid);
                        startActivity(ii);*/
                        studentAction(chat.getFlink());
                    }
                });

            }
        };

        student_chat_recycler_view.setAdapter(firebaseRecyclerAdapter);
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

    void studentAction(final String url){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Action");
        builder.setMessage("Once deleted than cannot be recovered.");

        builder.setPositiveButton("Download File", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(browserIntent);

            }
        });

        builder.setNegativeButton("Delete Message", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Incomplete",Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }
}
