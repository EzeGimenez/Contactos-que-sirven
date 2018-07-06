package com.visoft.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private Menu menu;
    private ConstraintLayout fragmentContainer;

    //Componentes graficas
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Inicializacion de variables
        fragmentContainer = findViewById(R.id.mainFragmentContainer);
        searchView = findViewById(R.id.searchView);

        //Creacion de toolbar_main
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
    }

    /**
     * Actualiza la interfaz de acuerdo si el usuario ya está registrado
     *
     * @param user firebase user, puede ser null
     */
    private void updateUI(@Nullable FirebaseUser user) {
        MenuItem signOutItem = menu.findItem(R.id.signOut);
        MenuItem goToProfileItem = menu.findItem(R.id.goToProfile);
        if (user != null) { // esta iniciado sesion

            signOutItem.setVisible(true);
            goToProfileItem.setVisible(true);
            searchView.setVisibility(View.VISIBLE);

            View view = menu.findItem(R.id.goToProfile).getActionView();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(menu.findItem(R.id.goToProfile));
                }
            });
            TextView tvusername = view.findViewById(R.id.tvUsername);
            tvusername.setText(user.getDisplayName());


            MainPageFragment mainPageFragment = new MainPageFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, mainPageFragment, Constants.MAIN_PAGE_FRAGMENT_TAG)
                    .commit();
        } else { // no esta iniciado sesion
            searchView.setVisibility(View.GONE);
            signOutItem.setVisible(false);
            goToProfileItem.setVisible(false);

            SignInFragment signInFragment = new SignInFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, signInFragment, Constants.SIGNIN_FRAGMENT_TAG)
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
            case R.id.signOut:
                mAuth.signOut();
                updateUI(null);
                return true;
            case R.id.goToProfile:
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateLogIn();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        this.menu = menu;
        return true;
    }
}
