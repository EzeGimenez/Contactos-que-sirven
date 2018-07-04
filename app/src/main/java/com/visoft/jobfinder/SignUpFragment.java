package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etConfirmPassword, etUsername;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        //Inicializar componentes gráficas
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.edConfirmPassword);

        view.findViewById(R.id.buttonAcceptSignUp).setOnClickListener(this);

        //etPassword.addTextChangedListener(new TextWatcher(){});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAcceptSignUp:
                signUp(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(), etConfirmPassword.getText().toString());
                ConstraintLayout progressBarContainer = getActivity().findViewById(R.id.progressBarContainer);
                progressBarContainer.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void signUp(final String username, String email, String pw, String pw2) {
        if (validateCredentials(username, email, pw, pw2)) {
            mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Created Account successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                registerNewUserFirebase(user.getUid(), username);
                                getActivity().onBackPressed();
                            } else {
                                Toast.makeText(getContext(), "Unsuccessfull registration", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        ConstraintLayout progressBarContainer = getActivity().findViewById(R.id.progressBarContainer);
        progressBarContainer.setVisibility(View.GONE);
    }

    private void registerNewUserFirebase(String uid, String username) {
        User user = new User();
        user.setUsername(username);
        user.setRating(-1);
        user.setNumberReviews(0);
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                .child(uid)
                .setValue(user);
    }

    /**
     * Validacion de credenciales
     *
     * @param email email a validar
     * @param pw    contraseña a validar
     * @param pw2   confirmacion de contraseña
     * @return true si son credenciales correctas, falso caso contrario
     */
    private boolean validateCredentials(String username, String email, String pw, String pw2) {
        //TODO Mejorar validacion de credenciales de registrarse
        if (email.trim().length() < 5 || pw.length() < 6 || username.trim().length() < 4) {
            return false;
        }
        if (!pw.equals(pw2)) {
            return false;
        }
        return true;
    }
}
