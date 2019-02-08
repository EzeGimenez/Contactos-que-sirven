package com.visoft.network;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.visoft.network.custom_views.NonSwipeableViewPager;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvInfo;
    private ImageButton btnNext, btnPrev;
    private NonSwipeableViewPager viewPager;
    private Button btnFinalizar;
    private ImageAdapter adapter;

    private int[] images = new int[]{
            R.drawable.tutorial1,
            R.drawable.tutorial2,
            R.drawable.tutorial3,
            R.drawable.tutorial4,
            R.drawable.tutorial5
    };
    private int[] descriptionsID = new int[]{
            R.string.tutorial1,
            R.string.tutorial2,
            R.string.tutorial4,
            R.string.tutorial3,
            R.string.tutorial5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tvInfo = findViewById(R.id.tvInfo);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        viewPager = findViewById(R.id.ViewPagerTutorial);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        TabLayout tabLayout = findViewById(R.id.tabLayoutTutorial);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnFinalizar.setOnClickListener(this);

        tvInfo.setText(getString(descriptionsID[0]));

        adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager, true);

        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                avanzar();
                break;

            case R.id.btnPrev:
                retroceder();
                break;

            case R.id.btnFinalizar:
                finish();
                break;
        }
    }

    private void avanzar() {
        btnPrev.setVisibility(View.VISIBLE);

        tvInfo.setText(getString(descriptionsID[viewPager.getCurrentItem() + 1]));

        if (viewPager.getCurrentItem() + 1 == adapter.getCount() - 1) {
            btnFinalizar.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
        }

        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    private void retroceder() {
        btnNext.setVisibility(View.VISIBLE);
        btnFinalizar.setVisibility(View.INVISIBLE);

        tvInfo.setText(getString(descriptionsID[viewPager.getCurrentItem() - 1]));

        if (viewPager.getCurrentItem() == 1) {
            btnPrev.setVisibility(View.INVISIBLE);
        }

        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    public class ImageAdapter extends PagerAdapter {
        LayoutInflater mLayoutInflater;

        ImageAdapter(Context context) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (object);
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.tutorial_pager, container, false);

            ImageView imageView = itemView.findViewById(R.id.img);
            imageView.setImageResource(images[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
