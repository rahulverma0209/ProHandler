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
import android.view.*;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UploadNotification extends AppCompatActivity {

    private Button selectDoc,upload;
    private EditText title,description,docName;
    private ImageView imageView;
    private Toolbar mtoolbar;
    private  LinearLayout linearLayout;
    private static final int DOCUMENT_PICK = 1;
    private Uri docUri;
    private StorageReference firebaseStorage;
    private DatabaseReference firebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notification);

        selectDoc = findViewById(R.id.selectDoc);
        upload = findViewById(R.id.upload);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        docName = findViewById(R.id.docName);
        linearLayout = findViewById(R.id.linearLayout);

        imageView = findViewById(R.id.imageView);

        mtoolbar = findViewById(R.id.main_page_toolbar);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Add Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDocument();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNotification();
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
            linearLayout.setVisibility(View.VISIBLE);
            Toast.makeText(this,"File Selected",Toast.LENGTH_LONG).show();
            docUri = data.getData();

            imageView.setBackgroundResource(R.drawable.doc_logo);
        }
    }


    //Upload Notification
    void uploadNotification(){
        if(title.getText().toString().equals(""))
            Toast.makeText(UploadNotification.this,"Title Field empty",Toast.LENGTH_LONG).show();
        else if(description.getText().toString().equals(""))
            Toast.makeText(UploadNotification.this,"Description Field empty",Toast.LENGTH_LONG).show();
        else if(docUri!=null)
        {
            if(docName.getText().toString().equals("") || !docName.getText().toString().contains("."))
                Toast.makeText(UploadNotification.this,"Enter file name with extension",Toast.LENGTH_LONG).show();
            else
            {
                final String docN = docName.getText().toString();

                loginProgressDialog = new ProgressDialog(this);
                loginProgressDialog.setTitle("Uploading");
                loginProgressDialog.setMessage("Please wait uploading notification");
                loginProgressDialog.setCanceledOnTouchOutside(false);
                loginProgressDialog.show();


                StorageReference docPath = firebaseStorage.child("MiniProject").child(docN);

                docPath.putFile(docUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())

                            firebaseStorage.child("MiniProject").child(docN).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    //description.setText(task.getResult().toString());

                                    //currentUser = mAuth.getCurrentUser();
                                    Long temp = Long.parseLong("1599223785549");
                                    Long key = temp - (System.currentTimeMillis());
                                    firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Notification").child("7").child(key.toString());


                                    HashMap<String,String> notificationData = new HashMap<>();
                                    notificationData.put("name",title.getText().toString());
                                    notificationData.put("dos",description.getText().toString());
                                    notificationData.put("file",task.getResult().toString());
                                    firebaseDatabase.setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                loginProgressDialog.dismiss();
                                                Toast.makeText(UploadNotification.this,"Uploaded",Toast.LENGTH_LONG).show();

                                                AlertDialog.Builder builder = new AlertDialog.Builder(UploadNotification.this);
                                                builder.setTitle("Upload Status");
                                                builder.setMessage("Notification Upload Successfully");
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                                builder.setNegativeButton("Add Notification", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                        startActivity(getIntent());
                                                    }
                                                });
                                                builder.show();
                                            }
                                            else {
                                                loginProgressDialog.dismiss();
                                                Toast.makeText(UploadNotification.this,"Error",Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                                }
                            });
                        else {
                            loginProgressDialog.dismiss();
                        }
                    }
                });
            }
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
}
