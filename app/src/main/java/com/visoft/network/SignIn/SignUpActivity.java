package com.visoft.network.SignIn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.visoft.network.Funcionalidades.AccountManager;
import com.visoft.network.Funcionalidades.AccountManagerFirebase;
import com.visoft.network.R;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail, etPassword, etConfirmPassword, etUsername;

    private AccountManager accountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);

        this.accountManager = AccountManagerFirebase.getInstance(this);

        //Inicializar componentes gráficas
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.edConfirmPassword);

        findViewById(R.id.buttonAcceptSignUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAcceptSignUp:
                signUp(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(), etConfirmPassword.getText().toString());

                break;
        }
    }

    private void signUp(final String username, String email, String pw, String pw2) {
        if (pw.equals(pw2)) {
            try {
                accountManager.signUp(username, email, pw2);
            } catch (Exception e) {
                showSnackBar(e.getMessage());
            }
        } else {
            showSnackBar("no coinciden contraseñas");
        }
    }
    
    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.rootContainer),
                msg, Snackbar.LENGTH_SHORT).show();
    }

}
