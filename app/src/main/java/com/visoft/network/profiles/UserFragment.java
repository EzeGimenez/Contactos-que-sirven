package com.visoft.network.profiles;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visoft.network.R;
import com.visoft.network.custom_views.CustomDialog;
import com.visoft.network.objects.UserNormal;

public class UserFragment extends Fragment {
    private UserNormal user;

    //Componentes gráficas
    private TextView tvUsername;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = (UserNormal) getArguments().getSerializable("user");
        return inflater.inflate(R.layout.fragment_default_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);

        if (getActivity() instanceof ProfileActivity) {
            ProfileActivity.hideLoadingScreen();
        } else {
            ProfileFragmentOwnUser.hideLoadingScreen();
        }

        iniciarUI();
    }

    private void iniciarUI() {
        tvUsername.setText(user.getUsername());

        getView().findViewById(R.id.infoApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog dialog = new CustomDialog(getContext());
                dialog.setPositiveButton(getString(R.string.aceptar), null);
                dialog.setMessage("Ezequiel Gimenez\neze.gimenez.98@gmail.com");
                dialog.show();
            }
        });

        getView().findViewById(R.id.cerrarSesion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileFragmentOwnUser) getParentFragment()).signOut();
            }
        });
        getView().findViewById(R.id.eliminarCuenta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileFragmentOwnUser) getParentFragment()).eliminarCuenta();
            }
        });
    }
}
