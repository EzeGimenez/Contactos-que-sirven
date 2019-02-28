package com.visoft.network.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.visoft.network.R;
import com.visoft.network.custom_views.CustomSnackBar;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebaseNormal;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;


public class SignUpNormalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGNUP = 3;

    private EditText etEmail, etPassword, etUsername;
    private ImageView btnShowPassword;
    private LoadingScreen loadingScreen;
    private AccountManager accountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.accountManager = HolderCurrentAccountManager.getCurrent(new AccountManagerFirebaseNormal.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                loadingScreen.hide();
                if (result) {
                    finishSuccessfully();
                } else {
                    if (data != null) {
                        showSnackBar(data.getString("error"));
                    }
                }
            }
        });
        this.loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnShowPassword = findViewById(R.id.btnShowPassword);
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

        findViewById(R.id.btnSignUp).setOnClickListener(this);
    }

    private void finishSuccessfully() {
        Intent intent = new Intent();
        intent.putExtra("loggeo", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                signUp(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString());
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

    private void signUp(final String username, String email, String pw) {
        try {
            loadingScreen.show();
            accountManager.signUp(username, email, pw, RC_SIGNUP);
        } catch (Exception e) {
            loadingScreen.hide();
            showSnackBar(e.getMessage());
        }

    }

    private void showSnackBar(String msg) {
        CustomSnackBar.makeText(findViewById(R.id.rootView),
                msg);
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
}