package com.visoft.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LogInFragment extends Fragment implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton buttonGoogleSignIn;
    private FirebaseAuth mAuth;
    private EditText emailET, passwordET;

    private final static int RC_GOOGLE_SIGNIN = 1;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauthId))
                .requestEmail()
                .requestId()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

        //Initialization of UI Components
        buttonGoogleSignIn = view.findViewById(R.id.buttonSignInWithGoogle);
        emailET = view.findViewById(R.id.etUsername);
        passwordET = view.findViewById(R.id.etPassword);

        //Listeners for buttons
        view.findViewById(R.id.buttonAcceptLogIn).setOnClickListener(this);
        buttonGoogleSignIn.setOnClickListener(this);
    }


    /**
     * Method to update UI and to check whether its null
     *
     * @param account account of the logged in user, can be null
     */
    private void updateUI(@Nullable FirebaseUser account) {
        if (account != null) {
            ((LoginActivity)getActivity()).onBackPressed();
        } else {

        }
    }

    /**
     * Prompt the user to signIn with a google Account
     */
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGNIN);
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
        }
    }

    private void signInWithEmail(String email, String password) {
        if (!checkCredentials(email, password)) {
            Toast.makeText(getContext(), "wrong credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Correctly logged in
                            Toast.makeText(getContext(), "Correctly signed in", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(getContext(), "Sign in failed" + task.getException(), Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Validates the email and the password
     *
     * @param email    email
     * @param password password
     * @return true if they're correctly formatted, false otherwise
     */
    private boolean checkCredentials(String email, String password) {
        //TODO Improve checkCredentials
        if (email == null || password == null) {
            return false;
        }
        if (email.trim().length() < 5 || password.trim().length() < 6) {
            return false;
        }
        if (!email.contains("@")) {
            return false;
        }
        return true;
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
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_GOOGLE_SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                //Recibido un task completado conteniendo la cuenta de google
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                authWithGoogle(acc);
            } catch (ApiException e) {
                //TODO arreglar excepción
                Toast.makeText(getContext(), "error al iniciar sesion", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void authWithGoogle(GoogleSignInAccount acc) {
        AuthCredential cred = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Log in success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(getContext(), "Log in failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
