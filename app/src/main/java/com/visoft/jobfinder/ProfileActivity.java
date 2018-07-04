package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser fbUser;
    private User user;
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    //Componentes gráficas
    private TextView tvUsername, tvNumberReviews;
    private RatingBar ratingBar;
    //private ImageView ivProfilePic;
    private ConstraintLayout progressBarContainer;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance().getReference();

        logIn();


        //Inicializacion de componentes gráficas
        tvNumberReviews = findViewById(R.id.tvNumberReviews);
        tvUsername = findViewById(R.id.tvUsername);
        ratingBar = findViewById(R.id.ratingBar);
        progressBarContainer = findViewById(R.id.progressBarContainer);
        progressBar = findViewById(R.id.progressBar);

        progressBarContainer.setVisibility(View.VISIBLE);

        //Creacion del usuario
        database.child(Constants.FIREBASE_USERS_CONTAINER_NAME).child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                iniciarUI();

                //remover progress bar
                progressBarContainer.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                goBack();
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

    private void logIn() {
        this.fbUser = mAuth.getCurrentUser();
        if (fbUser == null) {
            goBack();
        }
    }

    private void iniciarUI() {
        tvUsername.setText(user.getUsername());
        if (user.getNumberReviews() > 0) {
            ratingBar.setRating(user.getRating());
            tvNumberReviews.setText(user.getRating() + " - " + user.getNumberReviews() + " Reviews");
        } else {
            tvNumberReviews.setText("0 Reviews");
            ratingBar.setRating(0);
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
        getMenuInflater().inflate(R.menu.toolbar_perfil, menu);
        this.menu = menu;
        return true;
    }
}
