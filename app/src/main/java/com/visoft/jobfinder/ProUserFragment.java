package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProUserFragment extends Fragment {
    private ProUser user;

    //Componentes graficas
    private TextView tvUsername, tvNumberReviews, tvTelefono, tvHrAtencion;
    private RatingBar ratingBar;
    //private ImageView ivProfilePic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user = (ProUser) getArguments().getSerializable("user");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pro_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNumberReviews = view.findViewById(R.id.tvNumberReviews);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvHrAtencion = view.findViewById(R.id.tvHrAtencion);
        tvTelefono = view.findViewById(R.id.tvTelefono);
        ratingBar = view.findViewById(R.id.ratingBar);

        iniciarUI();
    }

    private void iniciarUI() {
        tvUsername.setText(user.getUsername());
        tvTelefono.setText(user.getTelefono1() + " / " + user.getTelefono2());
        tvHrAtencion.setText(user.getDiasAtencion() + " " + user.getHoraAtencion());

        if (user.getNumberReviews() > 0) {
            ratingBar.setRating(user.getRating());
            tvNumberReviews.setText(user.getRating() + "");
        } else {
            tvNumberReviews.setText("0 Reviews");
            ratingBar.setRating(0);
        }
    }
}
