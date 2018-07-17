package com.visoft.jobfinder;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.calificar:
                Intent intent = new Intent(ProfileActivity.this, UserReviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("user", shownUser);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_perfil, menu);
        this.menu = menu;
        menu.findItem(R.id.edit).setVisible(false);
        menu.findItem(R.id.convertirEnPro).setVisible(false);
        menu.findItem(R.id.signOut).setVisible(false);
        menu.findItem(R.id.eliminarCuenta).setVisible(false);
        return true;
    }
}
