package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.visoft.jobfinder.Util.Constants;

public class SigninActivity extends AppCompatActivity {
    private ConstraintLayout progressBarContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment fragment = new SignInFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentSignInSignUpContainer, fragment, Constants.LOGIN_FRAGMENT_TAG)
                .commit();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarLogIn);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBarContainer = findViewById(R.id.progressBarContainer);

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

        hideLoadingScreen();
    }

    public void showLoadingScreen() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
