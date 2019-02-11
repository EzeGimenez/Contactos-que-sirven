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

import com.visoft.network.R;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebaseNormal;
import com.visoft.network.funcionalidades.CustomSnackBar;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.objects.UserPro;
import com.visoft.network.turnpro.TurnProActivity;

public class SignUpProActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGNUP = 3;

    private EditText etEmail, etPassword, etUsername;
    private LoadingScreen loadingScreen;
    private AccountManager accountManager;
    private UserPro user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnShowPassword).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    findViewById(R.id.btnShowPassword).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.btnShowPassword).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.accountManager = HolderCurrentAccountManager.getCurrent(new AccountManagerFirebaseNormal.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                loadingScreen.hide();
                if (requestCode == RC_SIGNUP && result) {
                    user = (UserPro) data.get("user");
                    startProConfig();
                } else if (!result) {
                    if (data != null) {
                        showSnackBar(data.getString("error"));
                    }
                }
            }
        });
    }

    private void startProConfig() {
        Intent intent = new Intent(this, TurnProActivity.class);

        String[] configurators = new String[8];
        configurators[0] = "ConfiguratorRubro";
        configurators[1] = "ConfiguratorWorkScope";
        configurators[2] = "ConfiguratorProfilePic";
        configurators[3] = "ConfiguratorContacto";
        configurators[4] = "ConfiguratorPersonalInfo";
        configurators[5] = "ConfiguratorAcompanante";
        configurators[6] = "ConfiguratorSocialApps";
        configurators[7] = "ConfiguratorCV";

        intent.putExtra("user", user);
        intent.putExtra("configurators", configurators);
        intent.putExtra("isNewUser", true);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, null);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                signUp(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString());
                break;
            case R.id.btnShowPassword:
                if (etPassword.getTransformationMethod() == null) {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                } else {
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