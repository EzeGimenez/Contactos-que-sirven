package com.visoft.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.Database;
import com.visoft.jobfinder.misc.DatabaseTimer;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseUser fbUser;
    private User user;
    private ProUser proUser;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseUID;
    private static boolean isRunning;
    private DatabaseTimer timer;

    //Componentes gráficas
    private ConstraintLayout progressBarContainer;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Menu menu;
    private Button buttonShowReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.mAuth = FirebaseAuth.getInstance();
        this.usersDatabaseUID = Database.getDatabase()
                .getReference(
                        Constants.FIREBASE_USERS_CONTAINER_NAME + "/" +
                                mAuth.getCurrentUser().getUid());
        usersDatabaseUID.keepSynced(true);

        logIn();

        //Inicializacion de componentes gráficas
        progressBarContainer = findViewById(R.id.progressBarContainer);
        progressBar = findViewById(R.id.progressBar);
        buttonShowReviews = findViewById(R.id.buttonShowReviews);

        showLoadingScreen();

        //Creacion del usuario
        usersDatabaseUID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user == null || user.getIsPro()) {
                    getProUser();
                } else if (isRunning) {
                    iniciarUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                goBack();
            }
        });
        timer = new DatabaseTimer(8, this, true);

        buttonShowReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonShowReviews.setVisibility(View.GONE);
            }
        });


        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    private void getProUser() {
        //Checking if its proUser
        //Creacion del usuario
        usersDatabaseUID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                proUser = dataSnapshot.getValue(ProUser.class);
                user = proUser;
                if (isRunning) {
                    iniciarUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                goBack();
            }
        });
    }


    private void logIn() {
        this.fbUser = mAuth.getCurrentUser();
        if (fbUser == null) {
            goBack();
        }
    }

    private void iniciarUI() {

        if (menu != null && (user != null || proUser != null)) {
            if (proUser == null && user == null) {
                mAuth.signOut();
                finish();
                return;
            }

            if (user.getNumberReviews() > 0) {
                buttonShowReviews.setVisibility(View.VISIBLE);
                buttonShowReviews.setText(user.getNumberReviews() + " Opiniones");
            } else {
                buttonShowReviews.setVisibility(View.GONE);
            }

            MenuItem convertirEnProIcon = menu.findItem(R.id.convertirEnPro);
            MenuItem editarPerfil = menu.findItem(R.id.edit);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();

            Fragment fragment;
            String id;
            if ((user == null || user.getIsPro()) && proUser != null) {
                editarPerfil.setVisible(true);
                convertirEnProIcon.setVisible(false);
                fragment = new ProUserFragment();
                bundle.putSerializable("user", proUser);
                id = Constants.PRO_USER_FRAGMENT;
            } else {
                editarPerfil.setVisible(false);
                convertirEnProIcon.setVisible(true);
                fragment = new DefaultUserFragment();
                id = Constants.DEFAULT_USER_FRAGMENT;
                bundle.putSerializable("user", user);
            }
            fragment.setArguments(bundle);
            transaction.replace(R.id.ContainerProfileFragments, fragment, id);
            if (isRunning) {
                transaction.commit();
            }
            timer.cancel();
        }
    }

    private void goBack() {
        finish();
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
            case R.id.convertirEnPro:
                convertirEnPro();
                return true;
            case R.id.edit:
                editarPerfil();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Transforma el perfil de usuario en uno profesional
     */
    private void convertirEnPro() {
        //Iniciar actividad para convertirse en pro
        Intent intent = new Intent(this, TurnProActivity.class);
        intent.putExtra("user", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Muestra pantalla de edicion del perfil
     */
    private void editarPerfil() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("proUser", proUser);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_perfil, menu);
        this.menu = menu;
        iniciarUI();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isRunning = false;
        if (timer != null)
            timer.cancel();
    }

    public void showLoadingScreen() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
