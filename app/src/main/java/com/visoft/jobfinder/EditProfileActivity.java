package com.visoft.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.Database;
import com.visoft.jobfinder.misc.DatabaseTimer;
import com.visoft.jobfinder.turnprofragments.CVFragment;
import com.visoft.jobfinder.turnprofragments.ContactoFragment;

public class EditProfileActivity extends AppCompatActivity {
    private ProUser proUser;
    private FirebaseAuth mAuth;
    private FragmentManager fragmentManager;
    private DatabaseTimer timer;

    //Componentes gráficas
    private ConstraintLayout containerFragments;
    private TextView tvInfo;
    private Button btnNext, btnPrev;
    private ConstraintLayout progressBarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_pro);

        proUser = (ProUser) getIntent().getSerializableExtra("proUser");
        mAuth = FirebaseAuth.getInstance();

        containerFragments = findViewById(R.id.ContainerTurnProFragments);
        tvInfo = findViewById(R.id.tvInfo);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        progressBarContainer = findViewById(R.id.progressBarContainer);

        fragmentManager = getSupportFragmentManager();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForthPressed();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iniciarUI();
    }

    private void iniciarUI() {
        Fragment fragment = new ContactoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tel1", proUser.getTelefono1());
        bundle.putString("tel2", proUser.getTelefono2());
        bundle.putInt("fecha1", proUser.getDiasAtencion() / 10);
        bundle.putInt("fecha2", proUser.getDiasAtencion() % 10);
        bundle.putString("hr1", proUser.getHoraAtencion().split(" - ")[0]);
        bundle.putString("hr2", proUser.getHoraAtencion().split(" - ")[1]);
        bundle.putBoolean("email", proUser.getShowEmail());
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.ContainerTurnProFragments, fragment, Constants.CONTACTO_FRAGMENT_TAG)
                .commit();
        btnNext.setEnabled(true);
        btnPrev.setEnabled(true);
        btnPrev.setText(R.string.cancelar);
    }

    private void onForthPressed() {
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.ContainerTurnProFragments);
        if (actualFragment instanceof ContactoFragment) {

            if (((ContactoFragment) actualFragment).isContactInfoOk()) {
                ((ContactoFragment) actualFragment).setContactInfo(proUser);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(Constants.CV_FRAGMENT_TAG);
                if (fragment == null) {
                    fragment = new CVFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putString("cv", proUser.getCvText());
                fragment.setArguments(bundle);
                transaction
                        .replace(R.id.ContainerTurnProFragments, fragment, Constants.CV_FRAGMENT_TAG)
                        .addToBackStack(Constants.CONTACTO_FRAGMENT_TAG)
                        .commit();

                btnPrev.setEnabled(true);
                btnPrev.setText(R.string.previo);
                btnNext.setEnabled(true);
                btnNext.setText(R.string.finalizar);

            } else {
                ((ContactoFragment) actualFragment).vibrate();
            }

        } else if (actualFragment instanceof CVFragment) {

            if (((CVFragment) actualFragment).isInputOk()) {
                ((CVFragment) actualFragment).setCv(proUser);
                submitChanges();
            } else {
                ((CVFragment) actualFragment).vibrate();
            }
        }
    }

    private void submitChanges() {
        timer = new DatabaseTimer(8, this, false);
        showLoadingScreen();
        DatabaseReference database = Database.getDatabase().getReference();
        database
                .child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid())
                .setValue(proUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getApplication(), UserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                hideLoadingScreen();
                timer.cancel();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.ContainerTurnProFragments);
        if (actualFragment instanceof ContactoFragment) {

            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (actualFragment instanceof CVFragment) {

            Fragment contactoFragment = fragmentManager.findFragmentByTag(Constants.CONTACTO_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, contactoFragment, Constants.CONTACTO_FRAGMENT_TAG)
                    .addToBackStack(Constants.CV_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(true);
            btnPrev.setText(R.string.cancelar);
            btnPrev.setEnabled(true);
            btnNext.setText(R.string.siguiente);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    private void showLoadingScreen() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
