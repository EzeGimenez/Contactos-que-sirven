package com.visoft.network.profiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.R;
import com.visoft.network.custom_views.CustomSnackBar;
import com.visoft.network.funcionalidades.FillerRatingOptions;
import com.visoft.network.funcionalidades.GsonerUser;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.objects.QualityInfo;
import com.visoft.network.objects.Review;
import com.visoft.network.objects.User;
import com.visoft.network.objects.UserPro;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;

public class UserReviewActivity extends AppCompatActivity {
    private Review review;
    private DatabaseReference database;
    private QualityInfo qualityInfo;
    private UserPro proUserReviewed;
    private FirebaseAuth mAuth;
    private LoadingScreen loadingScreen;

    //Componentes gráficas
    private ViewPager viewPager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));

        database = Database.getDatabase().getReference();
        mAuth = FirebaseAuth.getInstance();
        proUserReviewed = (UserPro) getIntent().getSerializableExtra("user");

        database.child(Constants.FIREBASE_QUALITY_CONTAINER_NAME)
                .child(proUserReviewed.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        qualityInfo = dataSnapshot.getValue(QualityInfo.class);
                        if (qualityInfo == null) {
                            qualityInfo = new QualityInfo();
                        }
                        setup();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        viewPager = findViewById(R.id.viewPager);
    }

    private void setup() {
        review = new Review();
        viewPager.setAdapter(new LayoutAdapter());
    }

    private void saveReview() {
        review.setReviewerUID(mAuth.getCurrentUser().getUid());
        review.setReviewerUsername(mAuth.getCurrentUser().getDisplayName());

        float atencion = review.getAtencion();
        float tiempoResp = review.getTiempoResp();
        float calidad = review.getCalidad();

        if (atencion >= 4.5) {
            qualityInfo.setAtencion(qualityInfo.getAtencion() + 1);
        } else if (atencion <= 2 && qualityInfo.getAtencion() > 1) {
            qualityInfo.setAtencion(qualityInfo.getAtencion() - 2);
        }

        if (tiempoResp >= 4.5) {
            qualityInfo.setTiempoResp(qualityInfo.getTiempoResp() + 1);
        } else if (tiempoResp <= 2 && qualityInfo.getTiempoResp() > 1) {
            qualityInfo.setTiempoResp(qualityInfo.getTiempoResp() - 2);
        }

        if (calidad >= 4.5) {
            qualityInfo.setCalidad(qualityInfo.getCalidad() + 1);
        } else if (calidad <= 2 && qualityInfo.getCalidad() > 1) {
            qualityInfo.setCalidad(qualityInfo.getCalidad() - 2);
        }

        loadingScreen.show();

        database.child(Constants.FIREBASE_REVIEWS_CONTAINER_NAME)
                .child(proUserReviewed.getUid())
                .child(proUserReviewed.getNumberReviews() + "")
                .setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                saveQualityInfo();
            }
        });
    }

    private void saveQualityInfo() {
        database
                .child(Constants.FIREBASE_QUALITY_CONTAINER_NAME)
                .child(proUserReviewed.getUid())
                .setValue(qualityInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateProUserRating();
            }
        });
    }

    private void updateProUserRating() {
        final float valueToAdd = review.getRating();
        float newRating;
        if (proUserReviewed.getNumberReviews() > 0) {
            newRating = proUserReviewed.getRating() +
                    ((valueToAdd - proUserReviewed.getRating()) / proUserReviewed.getNumberReviews());
        } else {
            newRating = valueToAdd;
        }
        proUserReviewed.setRating(newRating);
        proUserReviewed.setNumberReviews(proUserReviewed.getNumberReviews() + 1);
        saveProUser(proUserReviewed);
    }

    private void saveProUser(UserPro user) {
        String json = GsonerUser.getGson().toJson(user, User.class);
        database.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME)
                .child(proUserReviewed.getUid())
                .setValue(json).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingScreen.hide();
                finish();
            }
        });


        database.child(Constants.COUNTER_CONTRACTS)
                .child("mock")
                .setValue("mock")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        database
                                .child(Constants.COUNTER_CONTRACTS)
                                .child("mock")
                                .removeValue();

                        database.child(Constants.COUNTER_CONTRACTS)
                                .child("cant")
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        long cant = (long) dataSnapshot.getValue();
                                        database.child(Constants.COUNTER_CONTRACTS).child("cant").setValue(++cant);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });
    }

    public void goBack() {
        CustomSnackBar.makeText(findViewById(R.id.rootView), getString(R.string.please_finish_review));
    }

    private void onForthPressed(int pos, int result) {
        switch (pos) {
            case 0:
                review.setCalidad(5 - result);
                break;
            case 1:
                if (result == 0) {
                    review.setTiempoResp(5);
                } else {
                    review.setTiempoResp(0);
                }
                break;
            case 2:
                review.setAtencion(5 - result);
                break;
        }
        viewPager.setCurrentItem(pos + 1);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private class LayoutAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.review_layout, container, false);

            String[] options = new String[0];
            TextView tvInfo = view.findViewById(R.id.tvQuery);
            String query = null;
            switch (position) {
                case 0:
                    query = getString(R.string.calidadTrabajoReview);
                    options = new String[]{
                            getString(R.string.muy_buena),
                            getString(R.string.buena),
                            getString(R.string.aceptable),
                            getString(R.string.mala),
                            getString(R.string.muy_mala)
                    };

                    break;
                case 1:
                    query = getString(R.string.tiempoRespReview);
                    options = new String[]{
                            getString(R.string.si),
                            getString(R.string.no)
                    };

                    break;
                case 2:
                    query = getString(R.string.atencionReview);
                    options = new String[]{
                            getString(R.string.muy_buena),
                            getString(R.string.buena),
                            getString(R.string.aceptable),
                            getString(R.string.mala),
                            getString(R.string.muy_mala)
                    };

                    break;
                case 3:
                    query = getString(R.string.calificacionFinalReview);
                    break;
            }

            tvInfo.setText(query);
            if (position < 3) {
                new FillerRatingOptions((ViewGroup) view.findViewById(R.id.ContainerButtonsRating), options, new FillerRatingOptions.ListenerButtonsRating() {
                    @Override
                    public void onClick(int result) {
                        onForthPressed(position, result);
                    }
                });
            } else {
                final SimpleRatingBar ratingBar = view.findViewById(R.id.ratingBar);
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setStarCornerRadius(10);
                view.findViewById(R.id.editText).setVisibility(View.VISIBLE);
                view.findViewById(R.id.buttonFinish).setVisibility(View.VISIBLE);
                view.findViewById(R.id.buttonFinish).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        review.setRating(ratingBar.getRating());
                        saveReview();
                    }
                });
            }

            viewPager.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, Object object) {
            return view == object;
        }
    }
}