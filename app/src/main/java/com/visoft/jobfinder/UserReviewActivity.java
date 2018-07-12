package com.visoft.jobfinder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.visoft.jobfinder.Objects.QualityInfo;
import com.visoft.jobfinder.Objects.Review;
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.Database;

import java.util.HashMap;
import java.util.Map;

public class UserReviewActivity extends AppCompatActivity {
    private final int MAX_SLIDES = 4;
    private Review review;
    private DatabaseReference database;
    private QualityInfo qualityInfo;
    private String uidReviewed;
    private Map<Integer, View> mapPage;
    private FirebaseAuth mAuth;

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
        uidReviewed = getIntent().getStringExtra("UID");

        database.child(Constants.FIREBASE_QUALITY_CONTAINER_NAME)
                .child(uidReviewed)
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

        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

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

                EditText et = mapPage.get(0).findViewById(R.id.editText);
                review.setMsg(et.getText().toString());
                review.setRating(ratingBar.getRating());
                saveReview();

                break;
        }

    }

    private void saveReview() {

        review.setReviewerUID(mAuth.getCurrentUser().getUid());
        review.setReviewerUsername(mAuth.getCurrentUser().getDisplayName());

        float atencion = review.getAtencion();
        float tiempoResp = review.getTiempoResp();
        float calidad = review.getCalidad();

        if (atencion >= 4.5) {
            qualityInfo.setAtencion(qualityInfo.getAtencion() + 1);
        } else if (atencion <= 2 && qualityInfo.getAtencion() > 0) {
            qualityInfo.setAtencion(qualityInfo.getAtencion() - 1);
        }

        if (tiempoResp >= 4.5) {
            qualityInfo.setTiempoResp(qualityInfo.getTiempoResp() + 1);
        } else if (tiempoResp <= 2 && qualityInfo.getTiempoResp() > 0) {
            qualityInfo.setTiempoResp(qualityInfo.getTiempoResp() - 1);
        }

        if (calidad >= 4.5) {
            qualityInfo.setCalidad(qualityInfo.getCalidad() + 1);
        } else if (calidad <= 2 && qualityInfo.getCalidad() > 0) {
            qualityInfo.setCalidad(qualityInfo.getCalidad() - 1);
        }

        showLoadingScreen();
        database.child(Constants.FIREBASE_REVIEWS_CONTAINER_NAME)
                .child(uidReviewed)
                .child(mAuth.getCurrentUser().getUid())
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
                .child(uidReviewed)
                .setValue(qualityInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideLoadingScreen();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        int shown = viewPager.getCurrentItem();

        switch (shown) {
            case 0:
                finish();
                break;
            case 1:
                btnPrev.setText(getString(R.string.cancelar));
                viewPager.setCurrentItem(shown - 1);
                break;
            case 2:
                viewPager.setCurrentItem(shown - 1);
                break;
            case 3:
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
