package com.example.tryauth;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager mviewPager;                               //For creating tabs
    private SelectionPagerAdapter selectionPagerAdapter;        //For Selection Tabs

    private TabLayout mtabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ProHandler");

        //Tabs
        mviewPager = findViewById(R.id.tabPager);
        selectionPagerAdapter = new SelectionPagerAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(selectionPagerAdapter);


        mtabLayout = findViewById(R.id.mainTab);
        mtabLayout.setupWithViewPager(mviewPager);
    }

    //Checking if user is already logged in or not
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //If not signed in then direct to first page
        if(currentUser==null || !currentUser.isEmailVerified())
        {
            sendToLoginPage();
        }
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
}
