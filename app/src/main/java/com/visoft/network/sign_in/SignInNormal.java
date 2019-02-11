package com.visoft.network.sign_in;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.visoft.network.MainActivityNormal;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebaseNormal;
import com.visoft.network.funcionalidades.CustomSnackBar;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.util.Constants;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SignInNormal extends Fragment implements View.OnClickListener {
    private static final int RC_SIGNINEMAIL = 1, RC_SIGNINGOOGLE = 2, RC_SIGNUP = 4;

    private EditText emailET, passwordET;
    private AccountManager accountManager;
    private LoadingScreen loadingScreen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_normal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.loadingScreen = new LoadingScreen(getContext(), (ViewGroup) view.findViewById(R.id.rootView));

        //Initialization of UI Components
        emailET = view.findViewById(R.id.etUsername);
        passwordET = view.findViewById(R.id.etPassword);

        //Listeners for buttons
        view.findViewById(R.id.buttonAcceptLogIn).setOnClickListener(this);
        view.findViewById(R.id.buttonSignInWithGoogle).setOnClickListener(this);
        view.findViewById(R.id.buttonSignUp).setOnClickListener(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGNINGOOGLE) {
            accountManager.onActivityResult(requestCode, resultCode, intent);
        } else if (resultCode == RESULT_OK) {
            goBackSuccesfully();
        }
    }

    private void goBackSuccesfully() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("asPro", false).commit();
        Intent intent = new Intent(getContext(), MainActivityNormal.class);
        loadingScreen.hide();
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * Inicializacion del fragment para registrarse
     */
    private void startSignUpActivity() {
        Intent intent = new Intent(getContext(), SignUpNormalActivity.class);
        startActivityForResult(intent, RC_SIGNUP);
    }

    private void showSnackBar(String msg) {
        CustomSnackBar.makeText(getView().findViewById(R.id.rootView),
                msg);
    }

    @Override
    public void onResume() {
        super.onResume();

        accountManager = AccountManagerFirebaseNormal.getInstance(new AccountManager.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                if (result) {
                    goBackSuccesfully();
                } else {
                    if (data != null) {
                        showSnackBar(data.getString("error"));
                    }
                }

                loadingScreen.hide();
            }
        }, (AppCompatActivity) getActivity());
        HolderCurrentAccountManager.setCurrent(accountManager);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("asPro", false).commit();
    }
}