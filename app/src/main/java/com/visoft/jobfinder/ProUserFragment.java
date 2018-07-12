package com.visoft.jobfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.jobfinder.Objects.ProUser;
import com.visoft.jobfinder.Objects.QualityInfo;
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.Database;
import com.visoft.jobfinder.misc.MapHighlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProUserFragment extends Fragment implements OnMapReadyCallback {
    private ProUser user;
    private GoogleMap map;
    private DatabaseReference database;
    private boolean isRunning;

    //Componentes graficas
    private TextView tvUsername, tvNumberReviews, tvTelefono, tvHrAtencion, tvEmail,
            tvCV, tvRubro;
    private SimpleRatingBar ratingBar;
    private MapView mapView;
    private Button btnCalificar;
    //private ImageView ivProfilePic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user = (ProUser) getArguments().getSerializable("user");
        database = Database.getDatabase().getReference(Constants.FIREBASE_QUALITY_CONTAINER_NAME);

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
        tvRubro = view.findViewById(R.id.tvRubro);
        ratingBar = view.findViewById(R.id.ratingBar);
        mapView = view.findViewById(R.id.map);
        btnCalificar = view.findViewById(R.id.btnReviewUser);

        isRunning = true;

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        iniciarUI();
    }

    private void iniciarUI() {
        tvUsername.setText(user.getUsername());
        String tel1 = user.getTelefono1();
        String tel2 = user.getTelefono2();

        int id = getResources().getIdentifier(user.getRubroGeneral() + "ID",
                "array",
                getActivity().getPackageName());
        final String[] rubroEspecificosID = getResources().getStringArray(id);
        id = getResources().getIdentifier(user.getRubroGeneral(),
                "array",
                getActivity().getPackageName());
        final String[] rubroEspecificos = getResources().getStringArray(id);

        final String[] rubroGeneral = getResources().getStringArray(R.array.rubrosGenerales);
        final String[] rubroGeneralID = getResources().getStringArray(R.array.rubrosGeneralesID);

        List<String> aux1 = new ArrayList<>(Arrays.asList(rubroGeneralID));
        List<String> aux2 = new ArrayList<>(Arrays.asList(rubroEspecificosID));

        String rubroGral = rubroGeneral[aux1.indexOf(user.getRubroGeneral())];
        String rubroEsp = rubroEspecificos[aux2.indexOf(user.getRubroEspecifico())];

        tvRubro.setText(rubroGral + " - " + rubroEsp);

        if (tel2.length() > 0) {
            tvTelefono.setText(tel1 + " / " + tel2);
        } else {
            tvTelefono.setText(tel1);
        }

        String[] diasL = getResources().getStringArray(R.array.dias);
        String[] hrAtencion = user.getHoraAtencion().split(" - ");
        String[] diasAtencion = new String[2];
        diasAtencion[0] = diasL[user.getDiasAtencion() / 10];
        diasAtencion[1] = diasL[user.getDiasAtencion() % 10];

        tvHrAtencion.setText(diasAtencion[0] + " " + getString(R.string.a) + " " + diasAtencion[1]
                + " , " + hrAtencion[0] + " " + getString(R.string.a) + " " + hrAtencion[1]);

        if (user.getShowEmail()) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(user.getEmail());
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
            tvNumberReviews.setText("0 " + getText(R.string.reviews));
            ratingBar.setRating(0);
        }

        if (getActivity() instanceof OwnUserProfileActivity) {
            ((OwnUserProfileActivity) getActivity()).hideLoadingScreen();
            btnCalificar.setVisibility(View.GONE);
        } else {
            btnCalificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UserReviewActivity.class);
                    intent.putExtra("UID", user.getUid());
                    startActivity(intent);
                }
            });
        }

        getInsignias();
    }

    private void getInsignias() {
        database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                QualityInfo qualityInfo = dataSnapshot.getValue(QualityInfo.class);
                if (isRunning) {
                    putInsignias(qualityInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRunning = false;
    }

    private void putInsignias(QualityInfo qualityInfo) {
        if (qualityInfo != null) {
            if (qualityInfo.getAtencion() >= Constants.MIN_ATENCION_INSIGNIA) {
                ImageView insigniaAtencion = getView().findViewById(R.id.ivInsigniaAtencion);
                insigniaAtencion.setVisibility(View.VISIBLE);
            }
            if (qualityInfo.getCalidad() >= Constants.MIN_CALIDAD_INSIGNIA) {
                ImageView insigniaCalidad = getView().findViewById(R.id.ivInsigniaCalidad);
                insigniaCalidad.setVisibility(View.VISIBLE);
            }
            if (qualityInfo.getTiempoResp() >= Constants.MIN_TIEMPO_RESP_INSIGNIA) {
                ImageView insigniaTiempoResp = getView().findViewById(R.id.ivInsigniaTiempoResp);
                insigniaTiempoResp.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(user.getMapCenterLat(), user.getMapCenterLng()),
                user.getMapZoom()));

        MapHighlighter mapHighlighter = new MapHighlighter(getContext(), map);
        mapHighlighter.highlightMap(new LatLng(user.getMapCenterLat(), user.getMapCenterLng()));
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
