package com.visoft.jobfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private int userIdCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
