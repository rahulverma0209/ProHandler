package com.example.tryauth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ChatStudent extends AppCompatActivity {

    private Toolbar mtoolbar;
    private EditText chat_upload_title,msg_file_name,chat_msg;
    private Button chat_choose_file,chat_upload;
    private static final int DOCUMENT_PICK = 1;
    private Uri docUri;
    private String uid,tid;

    private StorageReference firebaseStorage;
    private DatabaseReference firebaseDatabase;
    private ProgressDialog chatProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_student);

        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();
        if(bundle!=null)
        {
            uid =(String) bundle.get("uid");
            tid =(String) bundle.get("tid");
        }

        chat_upload_title = findViewById(R.id.chat_upload_title);
        chat_choose_file = findViewById(R.id.chat_choose_file);
        chat_upload = findViewById(R.id.chat_upload);
        msg_file_name = findViewById(R.id.msg_file_name);
        chat_msg=findViewById(R.id.chat_msg);

        chat_upload.setVisibility(View.INVISIBLE);
        msg_file_name.setVisibility(View.INVISIBLE);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        mtoolbar = findViewById(R.id.student_chat_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Send Document");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chat_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDocument();
            }
        });

        chat_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadChat();
            }
        });
    }

    public void chooseDocument(){

        Intent documentIntent = new Intent();
        documentIntent.setType("*/*");
        documentIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(documentIntent,"Select Document"),DOCUMENT_PICK);
    }

    //If document is successfully choosen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DOCUMENT_PICK && resultCode == RESULT_OK){
            chat_upload.setVisibility(View.VISIBLE);
            msg_file_name.setVisibility(View.VISIBLE);


            Toast.makeText(this,"File Selected",Toast.LENGTH_LONG).show();
            docUri = data.getData();
        }
    }

    private void uploadChat()
    {
        final String cut = chat_upload_title.getText().toString();
        final String mfn = msg_file_name.getText().toString();
        if(cut.equals(""))
            Toast.makeText(this,"Enter Title",Toast.LENGTH_LONG).show();
        else if(mfn.equals(""))
            Toast.makeText(this,"Enter Filename",Toast.LENGTH_LONG).show();
        else if(mfn.length()<5)
            Toast.makeText(this,"Enter filename with extension",Toast.LENGTH_LONG).show();
        else if(!mfn.substring(mfn.length()-4).equals(".pdf") && !mfn.substring(mfn.length()-4).equals(".doc") && !mfn.substring(mfn.length()-5).equals(".docx") && !mfn.substring(mfn.length()-4).equals(".ppt") && !mfn.substring(mfn.length()-5).equals(".pptx"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatStudent.this);
            builder.setTitle("File Type");
            builder.setMessage("File extension should be \n\n.pdf or .docx or .doc or .ppt or .pptx");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.show();
        }
        else {
            Toast.makeText(this,"Valid Details",Toast.LENGTH_LONG).show();

            chatProgressDialog = new ProgressDialog(this);
            chatProgressDialog.setTitle("Uploading");
            chatProgressDialog.setMessage("Please wait uploading notification");
            chatProgressDialog.setCanceledOnTouchOutside(false);
            chatProgressDialog.show();

            StorageReference docPath = firebaseStorage.child("Chat").child(uid).child(mfn);

            //Updoaling
            docPath.putFile(docUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatStudent.this,"File Uploaded",Toast.LENGTH_LONG).show();

                        //Uploading Details
                        firebaseStorage.child("Chat").child(uid).child(mfn).getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        //Toast.makeText(ChatStudent.this,task.getResult().toString(),Toast.LENGTH_LONG).show();
                                        Long temp = Long.parseLong("1570438972791");
                                        Long key = System.currentTimeMillis() - temp;
                                        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("chat").child(tid).child(uid).child(key.toString());


                                        HashMap<String,String> chatData = new HashMap<>();
                                        chatData.put("title",cut);
                                        chatData.put("status","Pending");
                                        if(chat_msg.getText().toString().equals(""))
                                            chatData.put("feedback","");
                                        else
                                            chatData.put("feedback","\nStudent : "+chat_msg.getText().toString());
                                        chatData.put("flink",task.getResult().toString());

                                        String timeStamp = new SimpleDateFormat("HH:mm a     dd-MM-yyyy").format(Calendar.getInstance().getTime());
                                        chatData.put("date",timeStamp);

                                        firebaseDatabase.setValue(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    chatProgressDialog.dismiss();
                                                    Toast.makeText(ChatStudent.this,"Message Uploaded",Toast.LENGTH_LONG).show();
                                                    showAlertBox();

                                                }
                                                else {
                                                    chatProgressDialog.dismiss();
                                                    Toast.makeText(ChatStudent.this,"Error in message uploading",Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });

                                    }
                                });
                    }
                    else {
                        Toast.makeText(ChatStudent.this,"Error in uploading",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    //For back arrow key <-
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    private void showAlertBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatStudent.this);
        builder.setTitle("Upload Status");
        builder.setMessage("Message Upload Successfully");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }
}
