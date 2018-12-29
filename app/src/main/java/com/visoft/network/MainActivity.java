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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.visoft.network.MainPageChats.ChatsFragment;
import com.visoft.network.MainPageContacts.MainContactsFragment;
import com.visoft.network.MainPageSearch.HolderFirstTab;
import com.visoft.network.Profiles.ProfileActivityOwnUser;
import com.visoft.network.SignIn.SignInActivity;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String RECEIVER_INTENT = "RECEIVER_INTENT";

    public static boolean isRunning;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private DatabaseReference database;

    private HolderFirstTab fragmentGeneral;
    private ChatsFragment chatsFragment;
    private MainContactsFragment mainContactsFragment;

    private BroadcastReceiver broadcastReceiver;
    private TabLayout tabLayout;

    private Menu menu;
    private ViewPager viewPagerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Database.getDatabase().getReference();
        sharedPref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notifyNewMessage();
            }
        };

        AccountManager accountManager = AccountManagerFirebase.getInstance(new AccountManagerFirebase.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {

            }
        }, this);

        accountManager.getCurrentUser(1);

        fragmentGeneral = fragmentGeneral == null ? new HolderFirstTab() : fragmentGeneral;
        chatsFragment = chatsFragment == null ? new ChatsFragment() : chatsFragment;
        mainContactsFragment = mainContactsFragment == null ? new MainContactsFragment() : mainContactsFragment;

        //Creacion de toolbar_main
        //Componentes graficas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            fragmentGeneral.setLocation(new LatLng(location.getLatitude(), location.getAltitude()));
                        } else {
                            fragmentGeneral.setLocation(null);
                        }
                    }
                });
    }

    public void notifyNewMessage() {
        if (viewPagerMain.getCurrentItem() != 1) {
            View view = tabLayout.getTabAt(1).getCustomView();
            view.findViewById(R.id.notImg).setVisibility(View.VISIBLE);
        }
        sharedPref.edit().putBoolean("unreadMessages", false).apply();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
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
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

            tabLayout = findViewById(R.id.tabLayoutMain);
            tabLayout.setupWithViewPager(viewPagerMain);
            viewPagerAdapter.addFragment(fragmentGeneral, getString(R.string.buscar));
            viewPagerAdapter.addFragment(chatsFragment, getString(R.string.chats));
            viewPagerAdapter.addFragment(mainContactsFragment, getString(R.string.contactos));
            viewPagerMain.setOffscreenPageLimit(3);
            viewPagerMain.setAdapter(viewPagerAdapter);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        //Search for the result
                    }
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

        } else {
            goToProfileItem.setVisible(false);
            showLogInScreen();
        }
    }

    private void showLogInScreen() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goToProfile:
                Intent intent = new Intent(this, ProfileActivityOwnUser.class);
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
