package com.visoft.network.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.AccountActivity;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebase;
import com.visoft.network.funcionalidades.LoadingScreen;

public class SignInActivity extends AccountActivity implements View.OnClickListener {
    //CONSTANTS FIELDS
    private static final int RC_SIGNINEMAIL = 1, RC_SIGNINGOOGLE = 2, RC_SIGNUP = 4;

    private EditText emailET, passwordET;

    private AccountManager accountManager;
    private LoadingScreen loadingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.accountManager = AccountManagerFirebase.getInstance(this);
        this.loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarLogIn);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Initialization of UI Components
        emailET = findViewById(R.id.etUsername);
        passwordET = findViewById(R.id.etPassword);

        //Listeners for buttons
        findViewById(R.id.buttonAcceptLogIn).setOnClickListener(this);
        findViewById(R.id.buttonSignInWithGoogle).setOnClickListener(this);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
    }

    /**
     * Prompt the user to signIn with a google Account
     */
    private void signInWithGoogle() {
        accountManager.logInWithGoogle(RC_SIGNINGOOGLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignInWithGoogle:
                signInWithGoogle();
                loadingScreen.show();
                break;
            case R.id.buttonAcceptLogIn:
                loadingScreen.show();
                signInWithEmail(emailET.getText().toString(), passwordET.getText().toString());
                break;
            case R.id.buttonSignUp:
                startSignUpActivity();
                break;
        }

    }

    private void signInWithEmail(String email, String password) {
        try {
            accountManager.logInWithEmail(email, password, RC_SIGNINEMAIL);
        } catch (Exception e) {
            showSnackBar(e.getMessage());
            loadingScreen.hide();
        }
    }

    /**
     * Came back from google sign in prompt
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGNUP && intent != null) {
            if (intent.getBooleanExtra("loggeo", false)) {
                finish();
            }
        } else if (requestCode == RC_SIGNINGOOGLE) {
            accountManager.onActivityResult(requestCode, resultCode, intent);
        }
    }

    /**
     * Inicializacion del fragment para registrarse
     */
    private void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);

        startActivityForResult(intent, RC_SIGNUP);
    }

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.rootView),
                msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
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

    @Override
    public void onRequestResult(boolean result, int requestCode, Bundle data) {
        if (result) {
            finish();
        }

        loadingScreen.hide();
    }
}
