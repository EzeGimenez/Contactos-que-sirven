package com.visoft.network.sign_in;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.R;

public class SignInChoose extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnSesionProfesional).setOnClickListener(this);
        view.findViewById(R.id.btnSesionNormal).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Fragment fragment;

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .addToBackStack(SignInActivity.chooseID);

        if (view.getId() == R.id.btnSesionProfesional) {
            fragment = new SignInPro();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            fragment = new SignInNormal();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        }

        transaction.replace(SignInActivity.containerID, fragment)
                .commit();
    }
}
