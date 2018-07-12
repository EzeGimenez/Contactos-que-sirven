package com.visoft.jobfinder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.jobfinder.Objects.QualityInfo;
import com.visoft.jobfinder.Objects.Review;
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.Database;

public class UserReviewActivity extends AppCompatActivity {
    private final int MAX_SLIDES = 4;
    private Review review;
    private DatabaseReference database;
    private QualityInfo qualityInfo;
    private String uid;

    //Componentes gráficas
    private Button btnNext, btnPrev;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        database = Database.getDatabase().getReference();

        uid = getIntent().getStringExtra("UID");

        database.child(Constants.FIREBASE_QUALITY_CONTAINER_NAME).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                QualityInfo qualityInfo1 = dataSnapshot.getValue(QualityInfo.class);
                if (qualityInfo != null) {
                    qualityInfo = qualityInfo1;
                } else {
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

        switch (shown) {
            case 0:
                viewPager.setCurrentItem(shown + 1);
                break;
            case 1:
                viewPager.setCurrentItem(shown + 1);
                break;
            case 2:
                viewPager.setCurrentItem(shown + 1);
                break;
            case 3:

                break;
        }

    }

    @Override
    public void onBackPressed() {
        int shown = viewPager.getCurrentItem();

    }

    private void setup() {
        review = new Review();
        viewPager.setAdapter(new LayoutAdapter());
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
            SimpleRatingBar ratingBar = view.findViewById(R.id.ratingBar);
            switch (position) {
                case 0:
                    tvQuery.setText("0");
                    ratingBar.setRating(0);
                    break;
                case 1:
                    ratingBar.setRating(1);
                    tvQuery.setText("1");
                    break;
                case 2:
                    ratingBar.setRating(2);
                    tvQuery.setText("2");
                    break;
                case 3:
                    ratingBar.setRating(3);
                    tvQuery.setText("3");
                    break;
            }

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
