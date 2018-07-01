package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment fragment = new LogInFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentSignInSignUp, fragment, Constants.LOGIN_FRAGMENT_TAG)
                .commit();
    }
}
