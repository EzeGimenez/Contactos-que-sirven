package com.visoft.network.SignIn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
        boolean asPro = false;
        if (view.getId() == R.id.btnSesionProfesional) {
            asPro = true;
        }

        Bundle bundle = new Bundle();
        bundle.putBoolean("asPro", asPro);

        Fragment fragment = new SignInSpecific();
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .addToBackStack(SignInActivity.chooseID)
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(SignInActivity.containerID, fragment)
                .commit();
    }
}
