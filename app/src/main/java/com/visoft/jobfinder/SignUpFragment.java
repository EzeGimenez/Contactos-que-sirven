package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.ErrorAnimator;


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

                break;
        }
    }

    private void signUp(final String username, String email, String pw, String pw2) {
        if (checkCredentials(username, email, pw, pw2)) {

            showLoadingScreen();

            mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                showSnackBar("Usuario Creado");
                                FirebaseUser user = mAuth.getCurrentUser();
                                registerNewUserFirebase(user.getUid(), username);
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        getActivity().finish();
                                    }
                                });

                            } else {
                                showSnackBar("Error al registrarse");
                            }
                            hideLoadingScreen();
                        }
                    });
        } else {
            showSnackBar("Credenciales Erróneas");
        }
    }

    private void registerNewUserFirebase(String uid, String username) {
        User user = new User();
        user.setUsername(username);
        user.setRating(-1);
        user.setNumberReviews(0);
        user.setPro(false);
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
    private boolean checkCredentials(String username, String email, String pw, String pw2) {
        //TODO Mejorar validacion de credenciales de registrarse
        if (username.trim().length() < 4) {
            ErrorAnimator.shakeError(getContext(), etUsername);
            return false;
        }
        if (email.trim().length() < 5) {
            ErrorAnimator.shakeError(getContext(), etEmail);
            return false;
        }
        if (pw.length() < 6) {
            ErrorAnimator.shakeError(getContext(), etPassword);
            return false;
        }
        if (!pw.equals(pw2)) {
            ErrorAnimator.shakeError(getContext(), etConfirmPassword);
            return false;
        }
        return true;
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
