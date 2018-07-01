package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private static final String LOGINFRAGMENT_TAG = "LogInFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fragmentManager = getSupportFragmentManager();

        //Start the UI
        LogInFragment logInFragment = new LogInFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentSignInSignUp,logInFragment, LOGINFRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }
}
