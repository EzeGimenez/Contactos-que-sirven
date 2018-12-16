package com.visoft.network;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.visoft.network.MainPageChats.AllChatsFragment;
import com.visoft.network.MainPageChats.ChatsFragment;
import com.visoft.network.MainPageContacts.MainContactsFragment;
import com.visoft.network.MainPageSearch.HolderRubrosFragment;
import com.visoft.network.MainPageSearch.MainPageFragment;
import com.visoft.network.MainPageSearch.SearchResultFragment;
import com.visoft.network.Profiles.OwnUserProfileActivity;
import com.visoft.network.SignIn.SigninActivity;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    public static final String RECEIVER_INTENT = "RECEIVER_INTENT";
    public static final String RECEIVER_MESSAGE = "RECEIVER_MESSAGE";
    public static boolean isRunning;
    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private Location location;
    private boolean hasSearched;
    private SharedPreferences sharedPref;
    private DatabaseReference database;
    private ViewPagerAdapter adapter;
    private HolderRubrosFragment holderRubrosFragment;
    private ChatsFragment chatsFragment;
    private MainContactsFragment mainContactsFragment;
    private BroadcastReceiver broadcastReceiver;
    private TabLayout tabLayout;

    //Componentes graficas
    private Toolbar toolbar;
    private Menu menu;
    private ViewPager viewPagerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Database.getDatabase().getReference();
        sharedPref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notifyNewMessage();
            }
        };

        //Creacion de toolbar_main
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));

        //Getting one location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null || location.getTime() <= Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }
    }

    public void notifyNewMessage() {
        if (viewPagerMain.getCurrentItem() != 1) {
            View view = tabLayout.getTabAt(1).getCustomView();
            view.findViewById(R.id.notImg).setVisibility(View.VISIBLE);
        }

        AllChatsFragment allChatsFragment = (AllChatsFragment) chatsFragment.getChildFragmentManager().findFragmentByTag(Constants.ALL_CHATS_FRAGMENT_NAME);
        allChatsFragment.refresh();
        sharedPref.edit().putBoolean("unreadMessages", false).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(RECEIVER_INTENT)
        );
        isRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        isRunning = false;
    }

    @Override
    public void onBackPressed() {
        if (viewPagerMain.getCurrentItem() == 0) {
            FragmentManager fm = holderRubrosFragment.getChildFragmentManager();
            if (fm.findFragmentById(R.id.ContainerRubroFragments) instanceof MainPageFragment) {
                finish();
            } else {
                fm.popBackStack();
            }
        } else {
            Fragment shownFrag = adapter.getItem(viewPagerMain.getCurrentItem());
            if (shownFrag != null && shownFrag.getChildFragmentManager().getBackStackEntryCount() > 0) {
                shownFrag.getChildFragmentManager().popBackStack();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewPagerMain != null) {
            if (holderRubrosFragment.isAdded()) {
                hasSearched = viewPagerMain.getCurrentItem() == 0 &&
                        holderRubrosFragment.getChildFragmentManager().findFragmentById(R.id.ContainerRubroFragments) instanceof SearchResultFragment;
                sharedPref.edit().putBoolean("hasSearched", hasSearched).commit();
                if (!hasSearched) {
                    Fragment shownFrag = adapter.getItem(viewPagerMain.getCurrentItem());
                    if (shownFrag != null) {
                        for (int i = 0; i < shownFrag.getChildFragmentManager().getBackStackEntryCount(); i++) {
                            //shownFrag.getChildFragmentManager().popBackStack();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hasSearched = false;
        sharedPref.edit().putBoolean("hasSearched", hasSearched).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    /**
     * If user is signed in it will updateUI accordingly
     */
    private void updateLogIn() {
        FirebaseUser acc = mAuth.getCurrentUser();
        updateUI(acc);
        if (acc != null) {
            database.child(Constants.FIREBASE_USERS_CONTAINER_NAME).child(acc.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object object = dataSnapshot.getValue();
                    if (object == null) {
                        mAuth.signOut();
                        updateLogIn();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Actualiza la interfaz de acuerdo si el usuario ya está registrado
     *
     * @param user firebase user, puede ser null
     */
    private void updateUI(@Nullable FirebaseUser user) {
        final MenuItem goToProfileItem = menu.findItem(R.id.goToProfile);

        if (user != null) { // esta iniciado sesion
            final View view = menu.findItem(R.id.goToProfile).getActionView();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(goToProfileItem);
                }
            });
            TextView tvusername = view.findViewById(R.id.tvUsername);
            tvusername.setText(user.getDisplayName());
            tvusername.setVisibility(View.VISIBLE);
            goToProfileItem.setVisible(true);

            viewPagerMain = findViewById(R.id.ViewPagerMain);
            adapter = new ViewPagerAdapter(getSupportFragmentManager());

            holderRubrosFragment = holderRubrosFragment == null ? new HolderRubrosFragment() : holderRubrosFragment;
            chatsFragment = chatsFragment == null ? new ChatsFragment() : chatsFragment;
            mainContactsFragment = mainContactsFragment == null ? new MainContactsFragment() : mainContactsFragment;

            tabLayout = findViewById(R.id.tabLayoutMain);
            tabLayout.setupWithViewPager(viewPagerMain);
            adapter.addFragment(holderRubrosFragment, getString(R.string.buscar));
            adapter.addFragment(chatsFragment, getString(R.string.chats));
            adapter.addFragment(mainContactsFragment, getString(R.string.contactos));
            viewPagerMain.setAdapter(adapter);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        //Search for the result
                    }
                    if (tab.getPosition() == 1) {
                        Fragment shownFrag = chatsFragment.getChildFragmentManager().findFragmentById(R.id.ContainerFragmentChats);
                        if (shownFrag instanceof AllChatsFragment) {
                            ((AllChatsFragment) shownFrag).refresh();
                        }
                        View view = tab.getCustomView();
                        if (view != null) {
                            view.findViewById(R.id.notImg).setVisibility(View.INVISIBLE);
                        }
                        sharedPref.edit().putBoolean("unreadMessages", false).commit();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //Check whether searchResultsFragment was visible
                    if (tab.getPosition() == 0) {
                        hasSearched = false;
                        Fragment shownFrag = holderRubrosFragment.getChildFragmentManager().findFragmentById(R.id.ContainerRubroFragments);
                        if (shownFrag instanceof SearchResultFragment) {
                            hasSearched = true;
                        }

                        sharedPref.edit().putBoolean("hasSearched", hasSearched).commit();
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            tabLayout.getTabAt(1).setCustomView(R.layout.chat_notified);
            if (!sharedPref.getBoolean("unreadMessages", false)) {
                tabLayout.getTabAt(1).getCustomView().findViewById(R.id.notImg).setVisibility(View.INVISIBLE);
            }

        } else {
            goToProfileItem.setVisible(false);
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Oyente de la toolbar_main
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goToProfile:
                Intent intent = new Intent(this, OwnUserProfileActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        this.menu = menu;
        updateLogIn();
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation() {
        return location;
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
