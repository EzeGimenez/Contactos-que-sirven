package com.visoft.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private Button btnFinalizar;
    private ImageAdapter adapter;
    private int current;

    private int[] images = new int[]{
            R.drawable.tutorial1,
            R.drawable.tutorial2,
            R.drawable.tutorial3,
            R.drawable.tutorial4,
            R.drawable.tutorial5
    };

    private int[] descriptions = new int[]{
            R.string.tutorial1,
            R.string.tutorial2,
            R.string.tutorial3,
            R.string.tutorial4,
            R.string.tutorial5
    };

    private int[] descriptions1 = new int[]{
            R.string.tutorial11,
            R.string.tutorial22,
            R.string.tutorial33,
            R.string.tutorial44,
            R.string.tutorial55
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = findViewById(R.id.ViewPagerTutorial);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        TabLayout tabLayout = findViewById(R.id.tabLayoutTutorial);

        btnFinalizar.setOnClickListener(this);

        adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        current = 0;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (viewPager.getCurrentItem() > current) {
                    avanzar();
                    current++;
                } else {
                    retroceder();
                    current--;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;

            case R.id.btnPrev:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                break;

            case R.id.btnFinalizar:
                finish();
                break;
        }
    }

    private void avanzar() {

        if (viewPager.getCurrentItem() + 1 == adapter.getCount()) {
            btnFinalizar.setVisibility(View.VISIBLE);
        }
    }

    private void retroceder() {
        btnFinalizar.setVisibility(View.GONE);
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
            TextView tv = itemView.findViewById(R.id.tv);
            TextView tv1 = itemView.findViewById(R.id.tv1);
            ViewGroup containerImg = itemView.findViewById(R.id.containerImg);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    images[position]);
            //Obtenemos el color dominante
            final int color = bitmap.getPixel(0, 0);
            containerImg.setBackgroundColor(color);

            imageView.setImageResource(images[position]);
            tv.setText(getString(descriptions[position]));
            tv1.setText(getString(descriptions1[position]));

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ViewGroup) object);
        }
    }
}
