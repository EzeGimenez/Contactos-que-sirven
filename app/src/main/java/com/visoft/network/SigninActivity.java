package com.visoft.network;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.visoft.network.Util.Constants;

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
        setSupportActionBar(toolbar);

        progressBarContainer = findViewById(R.id.progressBarContainer);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void showLoadingScreen() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
