package com.example.tryauth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    Button blogin;
    TextView forgot_password,bsighup;
    EditText password,email;

    private FirebaseAuth mAuth;


    private ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        blogin = findViewById(R.id.blogin);
        bsighup = findViewById(R.id.bsighup);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgot_password = findViewById(R.id.forgot_password);

        mAuth = FirebaseAuth.getInstance();

        loginProgressDialog = new ProgressDialog(this);



        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaill=email.getText().toString();
                String pass=password.getText().toString();

                if(emaill.equals("") || pass.equals("")) {
                    Toast.makeText(StartActivity.this,"Empty Field Found",Toast.LENGTH_LONG).show();
                }
                else{
                    //Stating Progress Dialog
                    loginProgressDialog.setTitle("Login In");
                    loginProgressDialog.setMessage("Please wait checking credentials");
                    loginProgressDialog.setCanceledOnTouchOutside(false);
                    loginProgressDialog.show();

                    login(emaill,pass);
                }
            }
        });

        //Opening SignUp Page
        bsighup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this,SignupActivity.class);
                startActivity(reg_intent);
                finish();
            }
        });

        //Forgot Password - Mail to reset
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emaill=email.getText().toString();

                if(emaill.equals("")){
                    Toast.makeText(StartActivity.this,"Email Field is Empty",Toast.LENGTH_LONG).show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                    builder.setTitle("Want to reset password?");
                    builder.setMessage("Reset password link will be mailed to your mail id.\n\n" + emaill);
                    builder.setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mAuth.sendPasswordResetEmail(emaill).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(StartActivity.this,"Check Mail",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(StartActivity.this,"Error",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }

            }
        });
    }

    private void login(String emaill, String pass) {

        mAuth.signInWithEmailAndPassword(emaill,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    //Checking if mail is verified or not
                    if(currentUser.isEmailVerified()){
                        loginProgressDialog.dismiss();
                        Toast.makeText(StartActivity.this,"Logged In",Toast.LENGTH_LONG).show();

                        Intent startMainIntent=new Intent(StartActivity.this,MainActivity.class);
                        startActivity(startMainIntent);
                        finish();
                    }
                    else{
                        loginProgressDialog.hide();

                        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                        builder.setTitle("Mail Not Verified");
                        builder.setMessage("Check mail to verify");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton("Resend Mail", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(StartActivity.this,"Incomplete Code",Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.show();
                    }

                }
                else {
                    loginProgressDialog.hide();

                    Toast.makeText(StartActivity.this,"Login Error",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
