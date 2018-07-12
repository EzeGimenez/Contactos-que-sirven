package com.visoft.jobfinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.visoft.jobfinder.Objects.ProUser;
import com.visoft.jobfinder.misc.Constants;

public class ProfileActivity extends AppCompatActivity {
    private ProUser shownUser;
    private Toolbar toolbar;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        shownUser = (ProUser) getIntent().getSerializableExtra("user");

        iniciarUI();
    }

    private void iniciarUI() {
        Fragment fragment = new ProUserFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", shownUser);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ContainerProfileFragments, fragment, Constants.PRO_USER_FRAGMENT_TAG)
                .commit();

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
