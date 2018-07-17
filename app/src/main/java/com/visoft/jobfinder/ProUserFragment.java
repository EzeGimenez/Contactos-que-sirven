package com.visoft.jobfinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.jobfinder.Objects.ProUser;
import com.visoft.jobfinder.Objects.QualityInfo;
import com.visoft.jobfinder.Objects.Review;
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
    private TextView tvUsername, tvNumberReviews, tvHrAtencion, tvRubro;
    private SimpleRatingBar ratingBar;
    private MapView mapView;
    private ImageButton btnCV, btnShowContactInfo;
    private Button btnShowReviews, btnMsg;
    private ImageView ivProfilePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user = (ProUser) getArguments().getSerializable("user");
        database = Database.getDatabase().getReference();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pro_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNumberReviews = view.findViewById(R.id.tvNumberReviews);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvHrAtencion = view.findViewById(R.id.tvHrAtencion);
        btnCV = view.findViewById(R.id.btnCV);
        tvRubro = view.findViewById(R.id.tvRubro);
        ratingBar = view.findViewById(R.id.ratingBar);
        mapView = view.findViewById(R.id.map);
        btnShowReviews = view.findViewById(R.id.btnShowReviews);
        btnMsg = view.findViewById(R.id.button);
        btnShowContactInfo = view.findViewById(R.id.btnShowContactInfo);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);

        isRunning = true;

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        iniciarUI();
    }

    @SuppressLint("SetTextI18n")
    private void iniciarUI() {
        tvUsername.setText(user.getUsername());

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

        String[] diasL = getResources().getStringArray(R.array.dias);
        String[] hrAtencion = user.getHoraAtencion().split(" - ");
        String[] diasAtencion = new String[2];
        diasAtencion[0] = diasL[user.getDiasAtencion() / 10];
        diasAtencion[1] = diasL[user.getDiasAtencion() % 10];

        tvHrAtencion.setText(diasAtencion[0] + " " + getString(R.string.a) + " " + diasAtencion[1]
                + "\n " + hrAtencion[0] + " " + getString(R.string.a) + " " + hrAtencion[1]);

        btnShowContactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactInfo();
            }
        });

        final String cv = user.getCvText();
        if (cv.trim().length() > 0) {
            btnCV.setVisibility(View.VISIBLE);
            btnCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(cv);
                    builder.setTitle(R.string.descripcion_personal);
                    builder.setPositiveButton(getString(R.string.aceptar), null);
                    builder.create().show();
                }
            });
        } else {
            btnCV.setVisibility(View.GONE);
        }

        if (user.getNumberReviews() > 0) {
            ratingBar.setRating(user.getRating());
            tvNumberReviews.setText(String.format("%.1f", user.getRating()));
            btnShowReviews.setText(user.getNumberReviews() + "");
            btnShowReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showComments();
                }
            });
        } else {
            tvNumberReviews.setText("0");
            ratingBar.setRating(0);
            btnShowReviews.setVisibility(View.GONE);
        }

        if (getActivity() instanceof OwnUserProfileActivity) {
            ((OwnUserProfileActivity) getActivity()).hideLoadingScreen();
            btnMsg.setVisibility(View.GONE);
        }
        getProfilePic();
        getInsignias();
    }

    private void getProfilePic() {
        if (user.getHasPic()) {
            StorageReference storage = FirebaseStorage.getInstance().getReference();

            StorageReference userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + user.getUid() + ".jpg");

            userRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    ivProfilePic.setImageBitmap(bm);
                    ivProfilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }

    }

    private void getInsignias() {
        database.child(Constants.FIREBASE_QUALITY_CONTAINER_NAME).child(user.getUid()).addValueEventListener(new ValueEventListener() {
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

    private void showContactInfo() {
        View view = getLayoutInflater().inflate(R.layout.contact_info, null);
        View containerTel2 = view.findViewById(R.id.containerTel2);
        View containerEmail = view.findViewById(R.id.containerMail);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call number
            }
        };

        view.findViewById(R.id.btnCall1).setOnClickListener(listener);
        TextView tvTel1 = view.findViewById(R.id.tvTel1);
        tvTel1.setText(user.getTelefono1());

        if (user.getTelefono2().isEmpty()) {
            containerTel2.setVisibility(View.GONE);

        } else {
            TextView tvTel2 = view.findViewById(R.id.tvTel2);
            tvTel2.setText(user.getTelefono2());
            view.findViewById(R.id.btnCall2).setOnClickListener(listener);
        }

        if (!user.getShowEmail()) {
            containerEmail.setVisibility(View.GONE);

        } else {
            TextView tvEmail = view.findViewById(R.id.tvEmail);
            tvEmail.setText(user.getEmail());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setTitle(R.string.contact_info);
        builder.setPositiveButton(R.string.aceptar, null);
        builder.create().show();

    }

    public void showComments() {

        final CountDownTimer timer = new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Toast.makeText(getContext(), "Revisa tu conexión", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        }.start();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final View view = getLayoutInflater().inflate(R.layout.reviews_alert_dialog, null);
        final List<Review> reviews = new ArrayList<Review>();

        final LinearLayout containerReviews = view.findViewById(R.id.ContainerReviews);

        builder.setView(view);
        builder.setPositiveButton(R.string.aceptar, null);
        builder.create().show();

        database
                .child(Constants.FIREBASE_REVIEWS_CONTAINER_NAME)
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        timer.cancel();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Review r = d.getValue(Review.class);
                            if (r != null) {
                                reviews.add(r);
                            }
                        }

                        for (Review r : reviews) {
                            View comment = getLayoutInflater().inflate(R.layout.comment, null);
                            TextView tvUsername = comment.findViewById(R.id.tvUsername);
                            TextView msg = comment.findViewById(R.id.tvMessage);
                            SimpleRatingBar ratingBar = comment.findViewById(R.id.ratingBar);

                            tvUsername.setText(r.getReviewerUsername());
                            msg.setText(r.getMsg());
                            ratingBar.setRating(r.getRating());

                            containerReviews.addView(comment);
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
