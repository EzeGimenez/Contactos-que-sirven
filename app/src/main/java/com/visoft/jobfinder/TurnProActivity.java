package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.visoft.jobfinder.turnprofragments.CVFragment;
import com.visoft.jobfinder.turnprofragments.ContactoFragment;
import com.visoft.jobfinder.turnprofragments.RubroEspecificoFragment;
import com.visoft.jobfinder.turnprofragments.RubroGeneralFragment;
import com.visoft.jobfinder.turnprofragments.WorkScopeFragment;

public class TurnProActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ProUser proUser;
    private User user;
    private DatabaseReference database;
    private FirebaseAuth mAuth;

    //Componentes gráficas
    private Button btnPrev, btnNext;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_pro);

        fragmentManager = getSupportFragmentManager();

        //Inicializacion variables
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tvInfo = findViewById(R.id.tvInfo);

        proUser = new ProUser();

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = (User) getIntent().getSerializableExtra("user");
        iniciarUI();
    }

    private void iniciarUI() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RubroGeneralFragment rubroGeneralFragment = new RubroGeneralFragment();

        transaction.add(R.id.ContainerTurnProFragments, rubroGeneralFragment, Constants.RUBRO_GENERAL_FRAGMENT_TAG);
        transaction.commit();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForthPressed(null);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnNext.setEnabled(false);
        btnPrev.setEnabled(false);

    }

    @Override
    public void onBackPressed() {
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.ContainerTurnProFragments);
        if (actualFragment instanceof RubroGeneralFragment) {

            finish();

        } else if (actualFragment instanceof RubroEspecificoFragment) {

            Fragment rubroGeneralFragment = fragmentManager.findFragmentByTag(Constants.RUBRO_GENERAL_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, rubroGeneralFragment, Constants.RUBRO_GENERAL_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(false);
            btnPrev.setEnabled(false);

        } else if (actualFragment instanceof WorkScopeFragment) {

            Fragment rubroEspecificoFragment = fragmentManager.findFragmentByTag(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, rubroEspecificoFragment, Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(false);
            btnPrev.setEnabled(false);

        } else if (actualFragment instanceof ContactoFragment) {

            Fragment workScopeFragment = fragmentManager.findFragmentByTag(Constants.WORK_SCOPE_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, workScopeFragment, Constants.WORK_SCOPE_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(true);
            btnPrev.setEnabled(true);

        } else if (actualFragment instanceof CVFragment) {

            Fragment contactoFragment = fragmentManager.findFragmentByTag(Constants.CONTACTO_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, contactoFragment, Constants.CONTACTO_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(true);
            btnPrev.setEnabled(true);
            btnNext.setText("Siguiente");

        }
    }

    /**
     * Manejo del avance de los fragments
     */
    public void onForthPressed(Bundle fragmentConfig) {
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.ContainerTurnProFragments);

        if (actualFragment instanceof RubroGeneralFragment) {

            String rubroGeneral = fragmentConfig.getString("RubroGeneral");
            Fragment rubroEspecificoFragment = fragmentManager.findFragmentByTag(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG);
            if (rubroEspecificoFragment == null) {
                rubroEspecificoFragment = new RubroEspecificoFragment();
            }
            rubroEspecificoFragment.setArguments(fragmentConfig);
            proUser.setRubroGeneral(rubroGeneral);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, rubroEspecificoFragment, Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG)
                    .addToBackStack(Constants.RUBRO_GENERAL_FRAGMENT_TAG)
                    .commit();

            btnPrev.setEnabled(true);

        } else if (actualFragment instanceof RubroEspecificoFragment) {

            proUser.setRubroEspecifico(fragmentConfig.getString("rubroEspecifico"));
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.WORK_SCOPE_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new WorkScopeFragment();
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction
                    .addToBackStack(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG)
                    .replace(R.id.ContainerTurnProFragments, fragment, Constants.WORK_SCOPE_FRAGMENT_TAG)
                    .commit();

            btnPrev.setEnabled(true);
            btnNext.setEnabled(true);

        } else if (actualFragment instanceof WorkScopeFragment) {

            ((WorkScopeFragment) actualFragment).setCameraBounds(proUser);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.CONTACTO_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new ContactoFragment();
            }
            transaction
                    .replace(R.id.ContainerTurnProFragments, fragment, Constants.CONTACTO_FRAGMENT_TAG)
                    .addToBackStack(Constants.WORK_SCOPE_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(true);
            btnPrev.setEnabled(true);

        } else if (actualFragment instanceof ContactoFragment) {

            if (((ContactoFragment) actualFragment).isContactInfoOk()) {
                ((ContactoFragment) actualFragment).setContactInfo(proUser);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(Constants.CV_FRAGMENT_TAG);
                if (fragment == null) {
                    fragment = new CVFragment();
                }
                transaction
                        .replace(R.id.ContainerTurnProFragments, fragment, Constants.CV_FRAGMENT_TAG)
                        .addToBackStack(Constants.CONTACTO_FRAGMENT_TAG)
                        .commit();

                btnPrev.setEnabled(true);
                btnNext.setEnabled(true);
                btnNext.setText("Finalizar");

            } else {
                ((ContactoFragment) actualFragment).vibrate();
            }

        } else if (actualFragment instanceof CVFragment) {

            if (((CVFragment) actualFragment).isInputOk()) {
                ((CVFragment) actualFragment).setCv(proUser);
                saveProUser();
            }

        }
    }

    public void saveProUser() {
        proUser.setPro(true);
        proUser.setUsername(user.getUsername());
        proUser.setRating(user.getRating());
        proUser.setNumberReviews(user.getNumberReviews());

        database.child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid()).setValue(proUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                saveInRubro();
            }
        });

    }

    private void saveInRubro() {
        database.child(Constants.FIREBASE_RUBRO_GENERAL_CONTAINER_NAME)
                .child(proUser.getRubroGeneral())
                .child(proUser.getRubroEspecifico())
                .child(mAuth.getCurrentUser().getUid())
                .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }
}
