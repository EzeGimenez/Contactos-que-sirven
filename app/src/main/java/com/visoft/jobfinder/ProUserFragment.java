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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;

public class ProUserFragment extends Fragment implements OnMapReadyCallback {
    private ProUser user;
    private GoogleMap map;

    //Componentes graficas
    private TextView tvUsername, tvNumberReviews, tvTelefono, tvHrAtencion, tvEmail,
            tvCV;
    private RatingBar ratingBar;
    private MapView mapView;
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
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCV = view.findViewById(R.id.tvCV);
        ratingBar = view.findViewById(R.id.ratingBar);
        mapView = view.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        iniciarUI();
    }

    private void iniciarUI() {
        tvUsername.setText(user.getUsername());
        tvTelefono.setText(user.getTelefono1() + " / " + user.getTelefono2());
        tvHrAtencion.setText(user.getDiasAtencion() + " " + user.getHoraAtencion());
        if (user.getShowEmail()) {
            tvEmail.setVisibility(View.VISIBLE);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            tvEmail.setText(mAuth.getCurrentUser().getEmail());
        } else {
            tvEmail.setVisibility(View.GONE);
        }
        String cv = user.getCvText();
        if (cv.trim().length() > 0) {
            tvCV.setVisibility(View.VISIBLE);
            tvCV.setText(cv);
        } else {
            tvCV.setVisibility(View.GONE);
        }

        if (user.getNumberReviews() > 0) {
            ratingBar.setRating(user.getRating());
            tvNumberReviews.setText(user.getRating() + "");
        } else {
            tvNumberReviews.setText("0 Reviews");
            ratingBar.setRating(0);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Map bounds
        map.setLatLngBoundsForCameraTarget(new LatLngBounds(
                new LatLng(user.getMapBound1Lat(), user.getMapBound1Long()),
                new LatLng(user.getMapBound2Lat(), user.getMapBound2Long())
        ));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(user.getMapCenterLat(), user.getMapCenterLng()),
                user.getMapZoom()));
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
