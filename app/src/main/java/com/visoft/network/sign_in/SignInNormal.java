package com.visoft.network.sign_in;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.visoft.network.MainActivityNormal;
import com.visoft.network.R;
import com.visoft.network.custom_views.CustomSnackBar;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebaseNormal;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.util.Constants;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SignInNormal extends Fragment implements View.OnClickListener {
    private static final int RC_SIGNINEMAIL = 1, RC_SIGNINGOOGLE = 2, RC_SIGNUP = 4;

    private EditText etEmail, etPassword;
    private AccountManager accountManager;
    private ImageView btnShowPassword;
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
        etEmail = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);

        //Listeners for buttons
        view.findViewById(R.id.buttonAcceptLogIn).setOnClickListener(this);
        view.findViewById(R.id.buttonSignInWithGoogle).setOnClickListener(this);
        view.findViewById(R.id.buttonSignUp).setOnClickListener(this);

        btnShowPassword = view.findViewById(R.id.btnShowPassword);
        btnShowPassword.setOnClickListener(this);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    btnShowPassword.setVisibility(View.VISIBLE);
                } else {
                    btnShowPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                hideKeyboard();
                break;
            case R.id.buttonAcceptLogIn:
                loadingScreen.show();
                hideKeyboard();
                signInWithEmail(etEmail.getText().toString(), etPassword.getText().toString());
                break;
            case R.id.buttonSignUp:
                startSignUpActivity();
                break;
            case R.id.btnShowPassword:
                if (etPassword.getTransformationMethod() == null) {
                    btnShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_show_password));
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    btnShowPassword.setImageDrawable(getResources().getDrawable(R.drawable.ic_hide_password));
                    etPassword.setTransformationMethod(null);
                }
                etPassword.setSelection(etPassword.getText().length());
                break;
        }

    }

    public void hideKeyboard() {
        Context context = getContext();
        View view = getView().getRootView();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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