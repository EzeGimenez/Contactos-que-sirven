package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.visoft.jobfinder.turnprofragments.CVFragment;
import com.visoft.jobfinder.turnprofragments.RubroEspecificoFragment;
import com.visoft.jobfinder.turnprofragments.RubroGeneralFragment;

public class TurnProActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ProUser proUser;

    //Componentes gráficas
    private Button btnPrev, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_pro);

        fragmentManager = getSupportFragmentManager();

        //Inicializacion variables
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        proUser = new ProUser();

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iniciarUI();
    }

    private void iniciarUI() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RubroGeneralFragment rubroGeneralFragment = new RubroGeneralFragment();

        transaction.replace(R.id.ContainerTurnProFragments, rubroGeneralFragment, Constants.RUBRO_GENERAL_FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment fragment = fragmentManager.findFragmentById(R.id.ContainerTurnProFragments);
        if (fragment != null) {
            if (fragment instanceof RubroGeneralFragment) {
                btnPrev.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
            } else if (fragment instanceof RubroEspecificoFragment) {
                btnNext.setVisibility(View.GONE);
            } else if (fragment instanceof CVFragment) {
                btnNext.setText("Siguiente");
            } else {
                btnNext.setVisibility(View.VISIBLE);
            }
        }
    }

    public ProUser getProUser() {
        return proUser;
    }

    public void saveProUser() {
        proUser.setPro(true);
        User user = (User) getIntent().getSerializableExtra("user");
        proUser.setUsername(user.getUsername());
        proUser.setRating(user.getRating());
        proUser.setNumberReviews(user.numberReviews);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database
                .child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid()).setValue(proUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }
}
