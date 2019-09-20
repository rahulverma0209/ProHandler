package com.example.tryauth;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

public class UploadNotification extends AppCompatActivity {

    private Button selectDoc;
    private ImageView imageView;
    private Toolbar mtoolbar;
    private static final int DOCUMENT_PICK = 1;
    private Uri docPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notification);

        selectDoc = findViewById(R.id.selectDoc);
        imageView = findViewById(R.id.imageView);

        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Add Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
            }
        });
    }

    public void createNotification(){

        Intent documentIntent = new Intent();
        documentIntent.setType("*/*");
        documentIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(documentIntent,"Select Document"),DOCUMENT_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DOCUMENT_PICK && resultCode == RESULT_OK){
            Toast.makeText(this,"File Selected",Toast.LENGTH_LONG).show();
            docPath = data.getData();

            imageView.setBackgroundResource(R.drawable.doc_logo);
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
