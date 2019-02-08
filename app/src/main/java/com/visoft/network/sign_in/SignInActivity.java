package com.visoft.network.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.visoft.network.R;
import com.visoft.network.WelcomeScreen;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;

public class SignInActivity extends AppCompatActivity {

    public static final int containerID = R.id.containerFragmentLogin;
    public static final String chooseID = "chooseID", welcomeId = "welcomeID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccountManager accountManager = HolderCurrentAccountManager.getCurrent(null);
        if (accountManager != null) {
            accountManager.invalidate();
        }
        getSupportFragmentManager().beginTransaction()
                .add(containerID, new WelcomeScreen(), chooseID)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(containerID);
        fragment.onActivityResult(requestCode, resultCode, data);
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