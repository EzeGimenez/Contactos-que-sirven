package com.visoft.network;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.visoft.network.custom_views.CustomNavigationBar;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebaseNormal;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.profiles.ProfileFragmentOwnUser;
import com.visoft.network.sign_in.SignInActivity;
import com.visoft.network.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.visoft.network.MainActivityNormalFragment.RECEIVER_INTENT;

public class MainActivityNormal extends AppCompatActivity {
    public static boolean isRunning;
    private FirebaseAuth mAuth;
    private BroadcastReceiver broadcastReceiver;
    private ProfileFragmentOwnUser profileFragmentOwnUser;
    private MainActivityNormalFragment mainActivityNormalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_normal);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mainActivityNormalFragment.notifyNewMessage();
            }
        };

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            showLogInScreen();
        }

        AccountManager accountManager;
        SharedPreferences sharedPref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        if (sharedPref.getBoolean("asPro", false)) {
            Intent intent = new Intent(this, MainActivityPro.class);
            startActivity(intent);
            finish();
            return;
        } else {
            accountManager = AccountManagerFirebaseNormal.getInstance(null, this);
        }

        HolderCurrentAccountManager.setCurrent(accountManager);
        accountManager.getCurrentUser(1);

        mainActivityNormalFragment = new MainActivityNormalFragment();
        profileFragmentOwnUser = new ProfileFragmentOwnUser();

        updateLogIn();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    @Override
    protected void onStart() {
        super.onStart();
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(RECEIVER_INTENT)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    public void update() {
        if (mAuth.getCurrentUser() == null) {
            showLogInScreen();
        }
    }

    @Override
    public void onBackPressed() {
        if (mainActivityNormalFragment.isVisible()) {
            mainActivityNormalFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
        if (isRunning) {
            hideKeyboard();
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * If user is signed in it will updateUI accordingly
     */
    private void updateLogIn() {
        FirebaseUser acc = mAuth.getCurrentUser();
        updateUI(acc);
    }

    private void updateUI(@Nullable FirebaseUser user) {

        if (user != null) {
            CustomNavigationBar customNavigationBar = findViewById(R.id.customNavBar);
            customNavigationBar.setCantItems(3);
            ViewGroup viewGroup = customNavigationBar.getItem(2);

            final ImageView ivHome = customNavigationBar.getItem(1).findViewById(R.id.imageView);
            final ImageView ivBack = customNavigationBar.getItem(0).findViewById(R.id.imageView);
            final ImageView ivPerson = viewGroup.findViewById(R.id.imageView);

            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(mainActivityNormalFragment, "");
            adapter.addFragment(profileFragmentOwnUser, "");
            final ViewPager vp = findViewById(R.id.Container);
            vp.setAdapter(adapter);

            vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    if (i == 0) {
                        ivHome.setImageDrawable(getResources().getDrawable(R.drawable.home_filled));
                        ivPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_unfilled));
                    } else if (i == 1) {
                        ivHome.setImageDrawable(getResources().getDrawable(R.drawable.home_unfilled));
                        ivPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_filled));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            ivPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_unfilled));
            ivHome.setImageDrawable(getResources().getDrawable(R.drawable.home_filled));
            ivBack.setImageDrawable(getResources().getDrawable(R.drawable.arrow_back_2));

            viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vp.getCurrentItem() != 1) {
                        vp.setCurrentItem(1);
                        ivPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_filled));
                        ivHome.setImageDrawable(getResources().getDrawable(R.drawable.home_unfilled));
                    }
                }
            });

            customNavigationBar.getItem(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vp.getCurrentItem() != 0) {
                        vp.setCurrentItem(0);
                        ivPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_unfilled));
                        ivHome.setImageDrawable(getResources().getDrawable(R.drawable.home_filled));
                    }
                }
            });

            customNavigationBar.getItem(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private void showLogInScreen() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
