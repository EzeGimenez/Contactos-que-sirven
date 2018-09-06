package com.visoft.network;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.Objects.ChatOverview;
import com.visoft.network.Objects.Message;
import com.visoft.network.Objects.ProUser;
import com.visoft.network.Objects.QualityInfo;
import com.visoft.network.Objects.Review;
import com.visoft.network.Objects.User;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.GlideApp;
import com.visoft.network.Util.MapHighlighter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProUserFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private ProUser user;
    private GoogleMap map;
    private DatabaseReference database;
    private boolean isRunning;

    //Componentes graficas
    private TextView tvUsername, tvNumberReviews, tvHrAtencion, tvRubro;
    private SimpleRatingBar ratingBar;
    private MapView mapView;
    private ImageButton btnCV, btnShowContactInfo;
    private Button btnShowReviews;
    private FloatingActionButton btnSend;
    private EditText etChat;
    private ConstraintLayout msgContainer;
    private ImageView btnInstagram, btnWhatsapp, btnMail, btnFacebook;
    private ImageView ivProfilePic;

    private ConstraintLayout containerScreens;

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
        btnShowContactInfo = view.findViewById(R.id.btnShowContactInfo);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        btnInstagram = view.findViewById(R.id.btnInstagram);
        btnMail = view.findViewById(R.id.btnMail);
        btnFacebook = view.findViewById(R.id.btnFacebook);
        btnWhatsapp = view.findViewById(R.id.btnWhatsapp);
        btnSend = view.findViewById(R.id.btnSend);
        msgContainer = view.findViewById(R.id.msgContainer);
        etChat = view.findViewById(R.id.etChat);
        containerScreens = view.findViewById(R.id.ContainerScreen);

        isRunning = true;

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        iniciarUI();
    }

    private void iniciarUI() {
        tvUsername.setText(user.getUsername());

        int id1 = getResources().getIdentifier(user.getRubroGeneral(),
                "string",
                getActivity().getPackageName());

        int id3 = getResources().getIdentifier(user.getRubroEspecificoEspecifico(),
                "string",
                getActivity().getPackageName());

        tvRubro.setText(getString(id1) + " - " + getString(id3));

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
            btnCV.setVisibility(View.INVISIBLE);
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
            btnShowReviews.setVisibility(View.INVISIBLE);
        }

        if (user.getWhatsappNum() != null && user.getWhatsappNum().length() > 0) {
            btnWhatsapp.setVisibility(View.VISIBLE);
            btnWhatsapp.setOnClickListener(this);
        } else {
            btnWhatsapp.setVisibility(View.GONE);
        }

        if (user.getInstagramID() != null && user.getInstagramID().length() > 0) {
            btnInstagram.setVisibility(View.VISIBLE);
            btnInstagram.setOnClickListener(this);
        } else {
            btnInstagram.setVisibility(View.GONE);
        }

        if (user.getFacebookID() != null && user.getFacebookID().length() > 0) {
            btnFacebook.setOnClickListener(this);
            btnFacebook.setVisibility(View.VISIBLE);
        } else {
            btnFacebook.setVisibility(View.GONE);
        }

        if (user.getShowEmail()) {
            btnMail.setOnClickListener(this);
            btnMail.setVisibility(View.VISIBLE);
        } else {
            btnMail.setVisibility(View.GONE);
        }

        if (getActivity() instanceof OwnUserProfileActivity) {
            ((OwnUserProfileActivity) getActivity()).hideLoadingScreen();
            btnSend.setVisibility(View.GONE);
            msgContainer.setVisibility(View.GONE);
        } else {
            etChat.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        etChat.clearFocus();
                        return true;
                    } else return false;
                }
            });
            etChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });

            containerScreens.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    etChat.clearFocus();
                }
            });

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(etChat.getText().toString());
                }
            });
        }

        getProfilePic();
        getInsignias();
    }

    private void sendMessage(final String s) {
        etChat.setText("");
        if (s != null && s.length() > 0) {
            final String uidSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final String uidReceiver = user.getUid();

            database
                    .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                    .child(uidSender)
                    .child(uidReceiver)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ChatOverview chatOverview = dataSnapshot.getValue(ChatOverview.class);

                            if (chatOverview == null) {
                                String chatID = database
                                        .child(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                                        .push().getKey();
                                chatOverview = new ChatOverview();
                                chatOverview.setChatID(chatID);
                            }

                            chatOverview.setAuthor(uidSender);
                            chatOverview.setReceiverUID(uidReceiver);
                            chatOverview.setLastMessage(s);
                            chatOverview.setTimeStamp(new Date().getTime());
                            saveInChats(chatOverview);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void saveInChats(ChatOverview chatOverview) {
        database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(chatOverview.getAuthor())
                .child(chatOverview.getReceiver())
                .setValue(chatOverview);

        database
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(chatOverview.getReceiver())
                .child(chatOverview.getAuthor())
                .setValue(chatOverview);

        Message message = new Message();
        message.setAuthor(chatOverview.getAuthor());
        message.setTimeStamp(chatOverview.getTimeStamp());
        message.setText(chatOverview.getLastMessage());

        database
                .child(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                .child(chatOverview.getChatID())
                .push()
                .setValue(message);
    }

    private void getProfilePic() {
        if (user.getHasPic()) {
            StorageReference storage = FirebaseStorage.getInstance().getReference();

            StorageReference userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
            GlideApp.with(getContext())
                    .load(userRef)
                    .into(ivProfilePic);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInstagram:
                Intent intentInstagram = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + user.getInstagramID()));

                try {
                    intentInstagram.setPackage("com.instagram.android");
                    startActivity(intentInstagram);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + user.getInstagramID())));
                }
                break;

            case R.id.btnFacebook:

                Intent intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=http://facebook.com/" + user.getFacebookID()));
                try {
                    intentFacebook.setPackage("com.facebook.katana");
                    startActivity(intentFacebook);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/" + user.getFacebookID())));
                }
                break;

            case R.id.btnMail:

                Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
                intentEmail.setData(Uri.parse("mailto:" + user.getEmail()));
                startActivity(intentEmail);
                break;

            case R.id.btnWhatsapp:

                Intent intentWpp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + user.getWhatsappNum()));
                try {
                    intentWpp.setPackage("com.whatsapp");
                    startActivity(intentWpp);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "Whatsapp not installed", Toast.LENGTH_SHORT).show();
                }

                break;
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
                String uri = "";
                switch (v.getId()) {
                    case R.id.btnCall1:
                        uri = "tel:" + user.getTelefono1();
                        break;

                    case R.id.btnCall2:
                        uri = "tel:" + user.getTelefono2();
                        break;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
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
                        int i = 0;
                        ArrayList<CircleImageView> picList = new ArrayList<>();
                        ArrayList<String> uids = new ArrayList<>();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Review r = d.getValue(Review.class);
                            if (r != null) {
                                View comment = getLayoutInflater().inflate(R.layout.comment, null);
                                TextView tvUsername = comment.findViewById(R.id.tvUsername);
                                TextView msg = comment.findViewById(R.id.tvMessage);
                                SimpleRatingBar ratingBar = comment.findViewById(R.id.ratingBar);
                                CircleImageView ivPic = comment.findViewById(R.id.ivImage);

                                tvUsername.setText(r.getReviewerUsername());
                                msg.setText(r.getMsg());
                                ratingBar.setRating(r.getRating());

                                containerReviews.addView(comment);

                                picList.add(ivPic);
                                uids.add(r.getReviewerUID());

                                i++;
                            }
                            if (i > 10) {
                                break;
                            }
                        }
                        getCommentsPics(picList, uids);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getCommentsPics(final List<CircleImageView> picList, final List<String> uids) {
        for (int i = 0; i < uids.size(); i++) {

            final int finalI = i;
            database
                    .child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                    .child(uids.get(i))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null && user.getHasPic() && isRunning) {
                                StorageReference userRef = FirebaseStorage
                                        .getInstance()
                                        .getReference()
                                        .child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + uids.get(finalI) + user.getImgVersion() + ".jpg");
                                GlideApp.with(getContext())
                                        .load(userRef)
                                        .into(picList.get(finalI));
                            } else {
                                picList.get(finalI).setImageDrawable(getResources().getDrawable(R.drawable.profile_pic));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
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
