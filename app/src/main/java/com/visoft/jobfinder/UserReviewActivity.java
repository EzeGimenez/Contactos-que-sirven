package com.visoft.jobfinder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.jobfinder.Objects.ProUser;
import com.visoft.jobfinder.Objects.QualityInfo;
import com.visoft.jobfinder.Objects.Review;
import com.visoft.jobfinder.Util.Constants;
import com.visoft.jobfinder.Util.Database;

import java.util.HashMap;
import java.util.Map;

public class UserReviewActivity extends AppCompatActivity {
    private final int MAX_SLIDES = 4;
    private Review review;
    private DatabaseReference database;
    private QualityInfo qualityInfo;
    private ProUser proUserReviewed;
    private Map<Integer, View> mapPage;
    private FirebaseAuth mAuth;
    private CountDownTimer timer;

    //Componentes gráficas
    private Button btnNext, btnPrev;
    private ViewPager viewPager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        database = Database.getDatabase().getReference();
        mAuth = FirebaseAuth.getInstance();
        mapPage = new HashMap<Integer, View>();
        proUserReviewed = (ProUser) getIntent().getSerializableExtra("user");

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
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onForthPressed();
                            }
                        });
                        btnPrev.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        viewPager = findViewById(R.id.viewPager);
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

        showLoadingScreen();
        timer = new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                finish();
            }
        };

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

    private void saveProUser(ProUser user) {
        database.child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                .child(proUserReviewed.getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideLoadingScreen();
                goBack();
            }
        });
    }

    public void goBack() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", proUserReviewed);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void onForthPressed() {
        int shown = viewPager.getCurrentItem();

        SimpleRatingBar ratingBar = mapPage.get(shown).findViewById(R.id.ratingBar);
        switch (shown) {
            case 0:

                viewPager.setCurrentItem(shown + 1);
                btnPrev.setText(getString(R.string.previo));
                review.setTiempoResp(ratingBar.getRating());

                break;
            case 1:

                viewPager.setCurrentItem(shown + 1);
                review.setAtencion(ratingBar.getRating());

                break;
            case 2:

                viewPager.setCurrentItem(shown + 1);
                review.setCalidad(ratingBar.getRating());
                btnNext.setText(getString(R.string.finalizar));

                break;
            case 3:

                EditText et = mapPage.get(shown).findViewById(R.id.editText);
                review.setMsg(et.getText().toString());
                review.setRating(ratingBar.getRating());
                saveReview();

                break;
        }

    }

    @Override
    public void onBackPressed() {
        int shown = viewPager.getCurrentItem();

        SimpleRatingBar ratingBar = null;
        if (shown > 0) {
            ratingBar = mapPage.get(shown - 1).findViewById(R.id.ratingBar);
        }
        switch (shown) {
            case 0:
                goBack();
                break;
            case 1:
                ratingBar.setRating(review.getTiempoResp());
                btnPrev.setText(getString(R.string.cancelar));
                viewPager.setCurrentItem(shown - 1);
                break;
            case 2:
                ratingBar.setRating(review.getAtencion());
                viewPager.setCurrentItem(shown - 1);
                break;
            case 3:
                ratingBar.setRating(review.getCalidad());
                btnNext.setText(getString(R.string.siguiente));
                viewPager.setCurrentItem(shown - 1);
                break;
        }

    }

    private void setup() {
        review = new Review();
        viewPager.setAdapter(new LayoutAdapter());
    }

    private void showLoadingScreen() {
        findViewById(R.id.progressBarContainer).setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        findViewById(R.id.progressBarContainer).setVisibility(View.GONE);
    }

    private class LayoutAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_SLIDES;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.review_layout, container, false);

            TextView tvQuery = view.findViewById(R.id.tvQuery);
            switch (position) {
                case 0:
                    tvQuery.setText(getString(R.string.tiempoRespReview));
                    break;
                case 1:
                    tvQuery.setText(getString(R.string.atencionReview));
                    break;
                case 2:
                    tvQuery.setText(getString(R.string.calidadTrabajoReview));
                    break;
                case 3:
                    view.findViewById(R.id.editText).setVisibility(View.VISIBLE);
                    tvQuery.setText(getString(R.string.calificacionFinalReview));
                    break;
            }

            mapPage.put(position, view);
            viewPager.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
