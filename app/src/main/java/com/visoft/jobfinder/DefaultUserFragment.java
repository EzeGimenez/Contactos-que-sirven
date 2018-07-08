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


public class DefaultUserFragment extends Fragment {
    private User user;

    //Componentes gráficas
    private TextView tvUsername, tvNumberReviews;
    private RatingBar ratingBar;
    //private ImageView ivProfilePic;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable("user");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_user, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((UserProfileActivity) getActivity()).hideLoadingScreen();

        tvNumberReviews = view.findViewById(R.id.tvNumberReviews);
        tvUsername = view.findViewById(R.id.tvUsername);
        ratingBar = view.findViewById(R.id.ratingBar);

        iniciarUI();
    }

    private void iniciarUI() {
        tvUsername.setText(user.getUsername());
        if (user.getNumberReviews() > 0) {
            ratingBar.setRating(user.getRating());
            tvNumberReviews.setText(user.getRating() + "");
        } else {
            tvNumberReviews.setText("0 Reviews");
            ratingBar.setRating(0);
        }
    }
}
