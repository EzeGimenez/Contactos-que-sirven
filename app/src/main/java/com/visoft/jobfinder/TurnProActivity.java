package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

        Fragment fragment = fragmentManager.findFragmentByTag(Constants.RUBRO_GENERAL_FRAGMENT_TAG);
        if (fragment != null && fragment.isVisible()) {
            btnPrev.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }
    }

    public ProUser getProUser() {
        return proUser;
    }
}
