package com.visoft.network;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.visoft.network.tab_chats.ChatsFragment;
import com.visoft.network.tab_contacts.MainContactsFragment;
import com.visoft.network.tab_search.HolderFirstTab;
import com.visoft.network.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.MODE_PRIVATE;

public class MainActivityNormalFragment extends Fragment {
    public static final String RECEIVER_INTENT = "RECEIVER_INTENT";
    public static boolean isRunning;
    private HolderFirstTab holderFirstTab;
    private ChatsFragment chatsFragment;
    private MainContactsFragment mainContactsFragment;
    private SharedPreferences sharedPref;
    private FusedLocationProviderClient mFusedLocationClient;

    private TabLayout tabLayout;

    private int currentTab;
    private ViewPager viewPagerMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_activity_normal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = getContext().getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            holderFirstTab.setLocation(new LatLng(location.getLatitude(), location.getAltitude()));
                        } else {
                            holderFirstTab.setLocation(null);
                        }
                    }
                });
        updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                holderFirstTab.setLocation(new LatLng(location.getLatitude(), location.getAltitude()));
                            } else {
                                holderFirstTab.setLocation(null);
                            }
                        }
                    });
            updateUI();
        }
    }

    public void onBackPressed() {
        if (holderFirstTab != null && holderFirstTab.isVisible() && !holderFirstTab.onBackPressed()) {
            getActivity().finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewPagerMain != null) {
            viewPagerMain.setCurrentItem(currentTab);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        isRunning = false;
    }

    public void notifyNewMessage() {
        if (viewPagerMain.getCurrentItem() != 1) {
            View view = tabLayout.getTabAt(1).getCustomView();
            view.findViewById(R.id.notImg).setVisibility(View.VISIBLE);
        }
        sharedPref.edit().putBoolean("unreadMessages", false).apply();
    }

    private void updateUI() {
        holderFirstTab = holderFirstTab == null ? new HolderFirstTab() : holderFirstTab;
        chatsFragment = chatsFragment == null ? new ChatsFragment() : chatsFragment;
        mainContactsFragment = mainContactsFragment == null ? new MainContactsFragment() : mainContactsFragment;

        viewPagerMain = getView().findViewById(R.id.ViewPagerMain);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());

        tabLayout = getView().findViewById(R.id.tabLayoutMain);
        tabLayout.setupWithViewPager(viewPagerMain);

        viewPagerAdapter.addFragment(holderFirstTab, getString(R.string.buscar));
        viewPagerAdapter.addFragment(chatsFragment, getString(R.string.chats));
        viewPagerAdapter.addFragment(mainContactsFragment, getString(R.string.contactos));
        viewPagerMain.setOffscreenPageLimit(3);
        viewPagerMain.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (tab.getPosition() == 1) {
                    View view = tab.getCustomView();
                    if (view != null) {
                        view.findViewById(R.id.notImg).setVisibility(View.INVISIBLE);
                    }
                    sharedPref.edit().putBoolean("unreadMessages", false).apply();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(1).setCustomView(R.layout.chat_notified);

        if (!sharedPref.getBoolean("unreadMessages", false)) {
            tabLayout.getTabAt(1).getCustomView().findViewById(R.id.notImg).setVisibility(View.INVISIBLE);
        }
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
