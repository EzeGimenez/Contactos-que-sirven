package com.visoft.jobfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
import com.visoft.jobfinder.Util.Constants;
import com.visoft.jobfinder.Util.Database;
import com.visoft.jobfinder.mainpagefragments.MainPageFragment;
import com.visoft.jobfinder.mainpagefragments.SearchResultFragment;
import com.visoft.jobfinder.mainpagefragments.SignInFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private Location location;
    private boolean hasSearched;
    private SharedPreferences sharedPref;
    private DatabaseReference database;

    //Componentes graficas
    private Toolbar toolbar;
    private Menu menu;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Database.getDatabase().getReference();
        sharedPref = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Inicializacion de variables
        searchView = findViewById(R.id.searchView);
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
        final SearchResultFragment fragment = new SearchResultFragment();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.ContainerMainFragments);
                    if (!(fragment1 instanceof SearchResultFragment) || !fragment.isVisible()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("searchQuery", newText);
                        fragment.setArguments(bundle);
                        fragment.resetSearch();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.ContainerMainFragments, fragment, Constants.SEARCH_RESULT_FRAGMENT_TAG)
                                .addToBackStack(Constants.MAIN_PAGE_FRAGMENT_TAG)
                                .commit();
                    } else {
                        fragment.searchForQuery(newText);
                    }
                    return true;
                }
                Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(Constants.MAIN_PAGE_FRAGMENT_TAG);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ContainerMainFragments, fragment1, Constants.MAIN_PAGE_FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit();

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.ContainerMainFragments);
        if (fragment instanceof MainPageFragment) {
            finish();
        } else {
            super.onBackPressed();
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.ContainerMainFragments);

        if (fragment != null && fragment instanceof SearchResultFragment) {
            hasSearched = true;
        } else {
            hasSearched = false;
        }
        sharedPref.edit().putBoolean("hasSearched", hasSearched).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPref.edit().putBoolean("hasSearched", false).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        hasSearched = sharedPreferences.getBoolean("hasSearched", false);
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
        final MenuItem goToContactsItem = menu.findItem(R.id.goToContacts);
        if (user != null) { // esta iniciado sesion
            searchView.setVisibility(View.VISIBLE);

            View view = menu.findItem(R.id.goToProfile).getActionView();
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
            goToContactsItem.setVisible(true);

            MainPageFragment mainPageFragment = new MainPageFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.ContainerMainFragments, mainPageFragment, Constants.MAIN_PAGE_FRAGMENT_TAG)
                    .commit();

            if (hasSearched) {
                String searchRequest = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).getString("searchRequest", "");
                boolean isRubro = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).getBoolean("isRubro", true);
                Fragment searchResultFragment = new SearchResultFragment();

                Bundle bundle = new Bundle();
                if (isRubro) {
                    bundle.putString("subRubroID", searchRequest);
                    int id = getResources().getIdentifier(searchRequest,
                            "string",
                            getPackageName());
                    String subRubro = getResources().getString(id);
                    bundle.putString("subRubro", subRubro);
                } else {
                    bundle.putString("searchQuery", searchRequest);
                }

                searchResultFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ContainerMainFragments, searchResultFragment, Constants.SEARCH_RESULT_FRAGMENT_TAG)
                        .addToBackStack(Constants.MAIN_PAGE_FRAGMENT_TAG)
                        .commit();
            }
        } else { // no esta iniciado sesion
            searchView.setVisibility(View.GONE);
            goToProfileItem.setVisible(false);
            goToContactsItem.setVisible(false);

            SignInFragment signInFragment = new SignInFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.ContainerMainFragments, signInFragment, Constants.SIGNIN_FRAGMENT_TAG)
                    .commit();
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
            case R.id.goToContacts:
                Intent intent2 = new Intent(this, ContactsActivity.class);
                startActivity(intent2);
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

}
