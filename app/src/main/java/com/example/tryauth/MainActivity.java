package com.example.tryauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    public static UserData currentUserData;

    //For BottomNavigation
    private BottomNavigationView mBottomNav;
    private FrameLayout mainPageFrame;
    private NotificationFragment notificationFragment;
    private ProgressFragment progressFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //If not signed in then direct to first page
        if(currentUser==null || !currentUser.isEmailVerified())
        {
            sendToLoginPage();
        }


        currentUserData = new UserData("null","null","null","null");

        //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        loadCurrentUserData();


        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ProHandler");

        //BottomNavigation
        mainPageFrame = findViewById(R.id.main_page_frame);
        mBottomNav = findViewById(R.id.bottom_nav);

        notificationFragment = new NotificationFragment();
        progressFragment = new ProgressFragment();

        setFragment(notificationFragment);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.home_bottom:
                        setFragment(notificationFragment);
                        return true;
                    case R.id.progress_bottom:
                        setFragment(progressFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    //Checking if user is already logged in or not
    public void onStart() {
        super.onStart();
        /*// Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //If not signed in then direct to first page
        if(currentUser==null || !currentUser.isEmailVerified())
        {
            sendToLoginPage();
        }*/
    }

    //Opening Login Page
    void sendToLoginPage() {
        Intent start=new Intent(MainActivity.this,StartActivity.class);
        startActivity(start);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.navigation_menu,menu);

        return true;
    }

    //Select 1:Account 2:Settings 3:SignOut
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.nav_account){
            Intent start=new Intent(MainActivity.this,AccountActivity.class);
            startActivity(start);
            Toast.makeText(MainActivity.this,"Account Selected",Toast.LENGTH_LONG).show();
        }
        else if(item.getItemId() == R.id.nav_setting){
            Toast.makeText(MainActivity.this,"Setting Selected",Toast.LENGTH_LONG).show();
        }
        else if(item.getItemId() == R.id.nav_signout){
            FirebaseAuth.getInstance().signOut();
            sendToLoginPage();
            Toast.makeText(MainActivity.this,"SignOut Successful ",Toast.LENGTH_LONG).show();
        }

        return true;
    }

    //to load new fragment when bottom is pressed
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_page_frame,fragment);
        fragmentTransaction.commit();
    }

    private void loadCurrentUserData(){

        if(currentUserData.getName().equals("null")) {

            ProgressDialog loginProgressDialog = new ProgressDialog(this);
            loginProgressDialog.setTitle("Loading");
            loginProgressDialog.setMessage("Please wait loading credentials");
            loginProgressDialog.setCanceledOnTouchOutside(false);
            loginProgressDialog.show();

            //Store Current data so that can be use later
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String uid = currentUser.getUid();

            DatabaseReference fireBaseDataBase;
            fireBaseDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            fireBaseDataBase.keepSynced(true);

            fireBaseDataBase.addValueEventListener(new ValueEventListener() {
                //For retrieving the data
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String usn = dataSnapshot.child("usn").getValue().toString();
                    String user_type = dataSnapshot.child("user_type").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    //Storing in static variable
                    currentUserData.setName(name);
                    currentUserData.setUsn(usn);
                    currentUserData.setUser_type(user_type);
                    currentUserData.setEmail(email);
                }

                //For handling errors
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            loginProgressDialog.hide();
            loginProgressDialog.dismiss();

        }
    }
}
