package com.example.tryauth;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ProHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
