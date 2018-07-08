package com.visoft.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.ErrorAnimator;

/**
 * Fragment del login
 */
public class SignInFragment extends Fragment implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText emailET, passwordET;

    private final static int RC_GOOGLE_SIGNIN = 1;

    public SignInFragment() {
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
        emailET = view.findViewById(R.id.etUsername);
        passwordET = view.findViewById(R.id.etPassword);

        //Listeners for buttons
        view.findViewById(R.id.buttonAcceptLogIn).setOnClickListener(this);
        view.findViewById(R.id.buttonSignInWithGoogle).setOnClickListener(this);
        view.findViewById(R.id.buttonSignUp).setOnClickListener(this);
    }

    /**
     * Método para actualizar la Interfaz de acuerdo a si se loggeo o no
     *
     * @param account Cuenta del usuario loggeado
     */
    private void updateUI(@Nullable FirebaseUser account) {
        if (account != null) {
            //Retornando a la pantalla principal, ya loggeado
            ((SigninActivity) getActivity()).onBackPressed();
        }

        hideLoadingScreen();
    }

    /**
     * Prompt the user to signIn with a google Account
     */
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGNIN);

        showLoadingScreen();
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
                startSignUpFragment();
                break;
        }
    }

    private void signInWithEmail(String email, String password) {
        if (!checkCredentials(email, password)) {
            showSnackBar(getString(R.string.credenciales_erroneas));
            return;
        }
        showLoadingScreen();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Correctly logged in
                            showSnackBar(getString(R.string.sesion_iniciada));
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            showSnackBar(getString(R.string.error_al_iniciar_sesion));
                            updateUI(null);
                        }
                        hideLoadingScreen();
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
        if (email == null || email.trim().length() < 5 || !email.contains("@")) {
            ErrorAnimator.shakeError(getContext(), emailET);
            return false;
        }
        if (password == null || password.trim().length() < 6) {
            ErrorAnimator.shakeError(getContext(), passwordET);
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
                showSnackBar(getText(R.string.error_al_iniciar_sesion).toString());
                updateUI(null);

                hideLoadingScreen();
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
                            showSnackBar(getText(R.string.sesion_iniciada).toString());
                            FirebaseUser userfb = mAuth.getCurrentUser();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                User user = new User();
                                user.setUsername(userfb.getDisplayName());
                                user.setRating(-1);
                                user.setNumberReviews(0);
                                user.setPro(false);
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                                        .child(userfb.getUid())
                                        .setValue(user);
                            }
                            updateUI(userfb);
                        } else {
                            showSnackBar(getText(R.string.error_al_iniciar_sesion).toString());
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Inicializacion del fragment para registrarse
     */
    private void startSignUpFragment() {
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(Constants.LOGIN_FRAGMENT_TAG);
        transaction.add(R.id.fragmentSignInSignUpContainer, signUpFragment, Constants.SIGNUP_FRAGMENT_TAG);
        transaction.commit();
    }

    private void showSnackBar(String msg) {
        Snackbar.make(getActivity().findViewById(R.id.rootContainer),
                msg, Snackbar.LENGTH_SHORT).show();
    }

    private void showLoadingScreen() {
        ((SigninActivity) getActivity()).showLoadingScreen();
    }

    private void hideLoadingScreen() {
        ((SigninActivity) getActivity()).hideLoadingScreen();
    }

}
