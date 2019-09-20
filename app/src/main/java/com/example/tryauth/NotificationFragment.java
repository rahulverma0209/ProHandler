package com.example.tryauth;

import android.app.AlertDialog;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.*;

import static com.example.tryauth.MainActivity.currentUserData;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recycler_notif;
    private DatabaseReference firedb;
    private View mView;
    private FloatingActionButton fab;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        Toast.makeText(getContext(), currentUserData.getName(),Toast.LENGTH_LONG).show();

        mView = inflater.inflate(R.layout.fragment_notification, container, false);

        //Connecting to DB
        firedb = FirebaseDatabase.getInstance().getReference().child("Notification").child("7");
        firedb.keepSynced(true);

        //RecyclerView
        recycler_notif = mView.findViewById(R.id.rv);
        recycler_notif.setHasFixedSize(true);
        recycler_notif.setLayoutManager(new LinearLayoutManager(getContext()));

        fab =  mView.findViewById(R.id.fab);

        if(currentUserData.getUser_type().equals("teacher"))
            fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start=new Intent(getContext(),UploadNotification.class);
                startActivity(start);
            }
        });
        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Notification,NotificationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>
                (Notification.class,R.layout.notifi,NotificationViewHolder.class,firedb) {

            @Override
            protected void populateViewHolder(NotificationViewHolder notificationViewHolder, final Notification notification, int i) {

                notificationViewHolder.setTitle(notification.getName());
                notificationViewHolder.setDos(notification.getDos());
                notificationViewHolder.changeImage(notification.getFile());

                notificationViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //AlertBax
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(notification.getName());
                        builder.setMessage(notification.getDos());

                        String s=notification.getFile();

                        if(s.startsWith("https://firebasestorage.googleapis.com")) {
                            builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent browserIntent = new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(notification.getFile()));
                                    startActivity(browserIntent);

                                    Toast.makeText(getContext(),"Opening",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                });

            }
        };

        recycler_notif.setAdapter(firebaseRecyclerAdapter);
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        View mview;
        public NotificationViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setTitle(String title){
            TextView noti_title = mview.findViewById(R.id.noti_title);
            noti_title.setText(title);
        }

        public void setDos(String dos){
            TextView noti_dos = mview.findViewById(R.id.noti_dos);
            noti_dos.setText(dos);
        }

        public  void changeImage(String s){
            ImageView img = mview.findViewById(R.id.doc_image);
            if(!s.startsWith("https://firebasestorage.googleapis.com")){
                img.setBackgroundResource(R.drawable.notifi_logo);
            }
            else if(s.contains(".pdf"))
                img.setBackgroundResource(R.drawable.pdf_logo);
            else if(s.contains(".doc") || s.contains(".docx") || s.contains(".txt"))
                img.setBackgroundResource(R.drawable.doc_logo);
            else if(s.contains(".xlsx") || s.contains(".xlsm"))
                img.setBackgroundResource(R.drawable.excel_logo);
            else if(s.contains(".ppt") || s.contains(".pptx"))
                img.setBackgroundResource(R.drawable.powerpoint_logo);
            else {
                img.setBackgroundResource(R.drawable.notifi_logo);
            }
        }
    }
}
