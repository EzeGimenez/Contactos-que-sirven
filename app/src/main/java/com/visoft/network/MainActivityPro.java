package com.visoft.network;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.visoft.network.custom_views.CustomNavigationBar;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebasePro;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.profiles.ProfileFragmentOwnUser;
import com.visoft.network.sign_in.SignInActivity;
import com.visoft.network.tab_chats.ChatsFragment;
import com.visoft.network.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivityPro extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private boolean updated = false;

    private ProfileFragmentOwnUser profileFragmentOwnUser;
    private ChatsFragment chatsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pro);

        sharedPref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        AccountManager.ListenerRequestResult l = new AccountManager.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                if (result && data != null && data.getBoolean("isNewUser", false)) {
                    showLogInScreen();
                    mAuth.signOut();
                } else {
                    updateUI(mAuth.getCurrentUser());
                }
            }
        };

        profileFragmentOwnUser = new ProfileFragmentOwnUser();
        chatsFragment = new ChatsFragment();
        AccountManager accountManager = AccountManagerFirebasePro.getInstance(l, this);
        HolderCurrentAccountManager.setCurrent(accountManager);
        accountManager.getCurrentUser(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        update();
    }

    public void update() {
        if (mAuth.getCurrentUser() == null) {
            showLogInScreen();
        } else {
            updateUI(mAuth.getCurrentUser());
        }
    }

    /**
     * Actualiza la interfaz de acuerdo si el usuario ya está registrado
     *
     * @param user firebase user, puede ser null
     */
    private void updateUI(@Nullable FirebaseUser user) {
        if (!updated && user != null) {
            final CustomNavigationBar customNavigationBar = findViewById(R.id.customNavBar);
            customNavigationBar.setCantItems(2);
            ViewGroup viewGroup = customNavigationBar.getItem(1);
            final ImageView icHome = customNavigationBar.getItem(0).findViewById(R.id.imageView);
            final ImageView icPerson = viewGroup.findViewById(R.id.imageView);

            final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(chatsFragment, "");
            adapter.addFragment(profileFragmentOwnUser, "");
            final ViewPager vp = findViewById(R.id.ContainerMainPro);
            vp.setAdapter(adapter);
            vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    if (i == 0) {
                        icHome.setImageDrawable(getResources().getDrawable(R.drawable.home_filled));
                        icPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_unfilled));
                    } else {
                        icHome.setImageDrawable(getResources().getDrawable(R.drawable.home_unfilled));
                        icPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_filled));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            icHome.setImageDrawable(getResources().getDrawable(R.drawable.home_filled));
            icPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_unfilled));
            viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vp.getCurrentItem() != 1) {
                        vp.setCurrentItem(1);
                        icHome.setImageDrawable(getResources().getDrawable(R.drawable.home_unfilled));
                        icPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_filled));
                    }
                }
            });
            customNavigationBar.getItem(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vp.getCurrentItem() != 0) {
                        vp.setCurrentItem(0);
                        icHome.setImageDrawable(getResources().getDrawable(R.drawable.home_filled));
                        icPerson.setImageDrawable(getResources().getDrawable(R.drawable.person_unfilled));
                    }
                }
            });

            if (!sharedPref.getBoolean("unreadMessages", false)) {
                //TODO no hay mensajes nuevos
            }
            updated = true;
        }
    }

    private void showLogInScreen() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        profileFragmentOwnUser.onActivityResult(requestCode, resultCode, data);
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
