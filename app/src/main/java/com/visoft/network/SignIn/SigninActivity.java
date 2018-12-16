package com.visoft.network.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.visoft.network.Funcionalidades.AccountManager;
import com.visoft.network.Funcionalidades.AccountManagerFirebase;
import com.visoft.network.R;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout progressBarContainer;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText emailET, passwordET;

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.accountManager = AccountManagerFirebase.getInstance(this);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarLogIn);
        setSupportActionBar(toolbar);

        progressBarContainer = findViewById(R.id.progressBarContainer);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauthId))
                .requestEmail()
                .requestId()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

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
        accountManager.logInWithGoogle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignInWithGoogle:
                signInWithGoogle();
                break;
            case R.id.buttonAcceptLogIn:
                signInWithEmail(emailET.getText().toString(), passwordET.getText().toString());
                break;
            case R.id.buttonSignUp:
                startSignUpActivity();
                break;
        }
    }

    private void signInWithEmail(String email, String password) {
        try {
            accountManager.logInWithEmail(email, password);
        } catch (Exception e) {
            showSnackBar(e.getMessage());
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
        accountManager.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Inicializacion del fragment para registrarse
     */
    private void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);

        startActivity(intent);
    }

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.rootContainer),
                msg, Snackbar.LENGTH_SHORT).show();
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
