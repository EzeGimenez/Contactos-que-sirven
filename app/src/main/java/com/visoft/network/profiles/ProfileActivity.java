package com.visoft.network.profiles;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.objects.User;
import com.visoft.network.objects.UserPro;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;

public class ProfileActivity extends AppCompatActivity {
    private static LoadingScreen loadingScreen;
    private User shownUser;
    private Menu menu;
    private boolean esContacto = false;
    private DatabaseReference userContacts;

    public static void hideLoadingScreen() {
        loadingScreen.hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));
        shownUser = (User) getIntent().getSerializableExtra("user");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userContacts = Database.getDatabase()
                .getReference(Constants.FIREBASE_CONTACTS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid());
        userContacts.keepSynced(true);

        iniciarUI();
    }

    private void iniciarUI() {
        loadingScreen.show();
        Fragment fragment;
        String tag;
        Bundle bundle = new Bundle();
        if (shownUser.getIsPro()) {

            fragment = new UserProFragment();
            tag = Constants.PRO_USER_FRAGMENT_TAG;
            UserPro proUser = (UserPro) shownUser;
            bundle.putSerializable("user", proUser);

        } else {
            fragment = new UserFragment();
            tag = Constants.DEFAULT_USER_FRAGMENT_TAG;
            bundle.putSerializable("user", shownUser);
        }

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ContainerProfileFragments, fragment, tag)
                .commit();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userContacts.child(shownUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue();
                if (object == null) {
                    esContacto = false;
                } else {
                    esContacto = true;
                }

                if (esContacto && menu != null) {
                    menu.findItem(R.id.addContact).setIcon(R.drawable.ic_star_black_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addContact:
                if (!esContacto) {
                    añadirContacto();
                } else {
                    removerContacto();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void añadirContacto() {
        invalidateOptionsMenu();
        userContacts.child(shownUser.getUid()).setValue(true);
        esContacto = true;
        invalidateOptionsMenu();
    }

    private void removerContacto() {
        invalidateOptionsMenu();
        userContacts.child(shownUser.getUid()).removeValue();
        esContacto = false;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_perfil, menu);
        this.menu = menu;

        if (esContacto) {
            menu.findItem(R.id.addContact).setIcon(R.drawable.ic_star_black_24dp);
        } else {
            menu.findItem(R.id.addContact).setIcon(R.drawable.ic_star_border_black_24dp);
        }
        return true;
    }
}
