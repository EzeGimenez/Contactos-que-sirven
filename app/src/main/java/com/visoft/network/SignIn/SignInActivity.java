package com.visoft.network.SignIn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.visoft.network.R;

public class SignInActivity extends AppCompatActivity {

    public static final int containerID = R.id.containerFragmentLogin;
    public static final String chooseID = "chooseID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction()
                .add(containerID, new SignInChoose(), chooseID)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }
}