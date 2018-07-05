package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment fragment = new LogInFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentSignInSignUpContainer, fragment, Constants.LOGIN_FRAGMENT_TAG)
                .commit();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarLogIn);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ConstraintLayout progressBarContainer = findViewById(R.id.progressBarContainer);
        progressBarContainer.setVisibility(View.GONE);
    }
}
