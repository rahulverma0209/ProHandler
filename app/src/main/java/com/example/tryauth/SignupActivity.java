package com.example.tryauth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText name,usn,email,password,confirm_password,sem;
    Button signup,cancel;

    private Toolbar mtoolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseDatabase;

    private ProgressDialog signProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //ToolBar
        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        name = findViewById(R.id.name);
        usn = findViewById(R.id.usn);
        sem = findViewById(R.id.sem);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signup = findViewById(R.id.signup);
        cancel = findViewById(R.id.cancel);


        signProgressDialog = new ProgressDialog(this);


        // Opening Registering user function
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mname=name.getText().toString();
                String musn=usn.getText().toString();
                    musn = musn.toUpperCase();
                String msem=sem.getText().toString();
                String memail=email.getText().toString();
                String mpassword=password.getText().toString();
                String mconfirm_password=confirm_password.getText().toString();

                if(validateFields(mname,musn,msem,memail,mpassword,mconfirm_password))
                {
                    Toast.makeText(SignupActivity.this,"Valid Details",Toast.LENGTH_LONG).show();

                    //Stating Progress Dialog
                    signProgressDialog.setTitle("Registering User");
                    signProgressDialog.setMessage("Please wait while we create your account");
                    signProgressDialog.setCanceledOnTouchOutside(false);
                    signProgressDialog.show();

                    signUpUser(mname,musn,msem,memail,mpassword);
                }
            }
        });

        //Going Back to Login Page
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start=new Intent(SignupActivity.this,StartActivity.class);
                startActivity(start);
                finish();
            }
        });
    }

    //Validating all entered data
    Boolean validateFields(String mname, String musn, String msem, String memail, String mpassword, String mconfirm_password){

        if(mname.equals("") || musn.equals("") || memail.equals("") || mpassword.equals(""))
        {
            Toast.makeText(SignupActivity.this,"Empty Field",Toast.LENGTH_LONG).show();
            return false;
        }
        if(!musn.matches("[1][A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{3}")  && !musn.matches("[N][H][-][0-9]{4}"))
        {
            Toast.makeText(SignupActivity.this,"InValid USN",Toast.LENGTH_LONG).show();
            return false;
        }

        if(musn.startsWith("1NH") && !msem.matches("[3-8]{1}")){
            Toast.makeText(SignupActivity.this,"InValid Sem",Toast.LENGTH_LONG).show();
            return false;
        }

        memail=memail.toLowerCase();
        if(memail.endsWith(".com") && memail.contains("@") && !memail.startsWith("@"))
        {
            if(mpassword.equals(mconfirm_password))
            {
                if(mpassword.length()>=6)
                    return true;
                else
                {
                    Toast.makeText(SignupActivity.this,"Password length < 6",Toast.LENGTH_LONG).show();
                    return false;
                }

            }
            else
            {
                Toast.makeText(SignupActivity.this,"Password Doesn't match",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else
        {
            Toast.makeText(SignupActivity.this,"InValid email",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //Registering the user And Sending Verification mail
    private void signUpUser(final String mname, final String usn, final String msem, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser currentUser = mAuth.getCurrentUser();

                            //Saving all details to FireBase
                            String uid = currentUser.getUid();      //Getting FireBase UID
                            firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                            HashMap<String,String> userData = new HashMap<>();
                            userData.put("name",mname);
                            userData.put("usn",usn);
                            userData.put("email",email);
                            if(usn.startsWith("1NH"))
                            {
                                userData.put("user_type","student");
                                userData.put("sem",msem);
                            }
                            else
                            {
                                userData.put("user_type","teacher");
                                userData.put("sem","na");
                            }

                            //Adding data to Database
                            firebaseDatabase.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //If data is added
                                    if(task.isSuccessful()){

                                        //Sending Verification mail
                                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isComplete())
                                                {
                                                    //If task completed the dismiss the progress dialog
                                                    signProgressDialog.dismiss();


                                                    //AlertDialog to display sent
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                                    builder.setTitle("Sent");
                                                    builder.setMessage("Verification Mail Sent Successfully");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Send to Login Page
                                                            Intent start=new Intent(SignupActivity.this,StartActivity.class);
                                                            startActivity(start);
                                                            finish();
                                                        }
                                                    });
                                                    builder.show();

                                                    Toast.makeText(SignupActivity.this,"Mail Sent",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }
                        else {
                            signProgressDialog.hide();
                            Toast.makeText(SignupActivity.this,"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
