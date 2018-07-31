package com.visoft.network;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.Objects.ChatOverview;
import com.visoft.network.Objects.ProUser;
import com.visoft.network.Objects.User;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.DatabaseTimer;

import java.util.ArrayList;

public class OwnUserProfileActivity extends AppCompatActivity {
    private static boolean isRunning;
    private FirebaseUser fbUser;
    private User user;
    private ProUser proUser;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseUID;
    private DatabaseTimer timer;

    //Componentes gráficas
    private ConstraintLayout progressBarContainer;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signOut();
            finish();
        }
        this.usersDatabaseUID = Database.getDatabase()
                .getReference(
                        Constants.FIREBASE_USERS_CONTAINER_NAME + "/" +
                                mAuth.getCurrentUser().getUid());
        usersDatabaseUID.keepSynced(true);

        logIn();

        //Inicializacion de componentes gráficas
        progressBarContainer = findViewById(R.id.progressBarContainer);
        progressBar = findViewById(R.id.progressBar);

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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));
        if (menu != null && (user != null || proUser != null)) {
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
                id = Constants.PRO_USER_FRAGMENT_TAG;
            } else {
                editarPerfil.setVisible(false);
                convertirEnProIcon.setVisible(true);
                fragment = new DefaultUserFragment();
                id = Constants.DEFAULT_USER_FRAGMENT_TAG;
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
            case R.id.signOut:
                mAuth.signOut();
                finish();
                return true;
            case R.id.convertirEnPro:
                convertirEnPro();
                return true;
            case R.id.edit:
                editarPerfil();
                return true;
            case R.id.eliminarCuenta:
                eliminarCuenta();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void eliminarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.seguro_eliminar);
        builder.setNegativeButton(R.string.cancelar, null);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DatabaseReference rootRef = Database.getDatabase().getReference();

                //Removing from users;
                rootRef.child(Constants.FIREBASE_USERS_CONTAINER_NAME).child(user.getUid()).removeValue();

                //Removing contacts
                rootRef.child(Constants.FIREBASE_CONTACTS_CONTAINER_NAME).child(user.getUid()).removeValue();

                //proUser removing
                if (user.getIsPro() && proUser != null) {
                    //Removing from rubros
                    rootRef
                            .child(Constants.FIREBASE_RUBRO_CONTAINER_NAME)
                            .child(proUser.getRubroEspecifico())
                            .child(proUser.getUid())
                            .removeValue();

                    //removing reviews
                    rootRef
                            .child(Constants.FIREBASE_REVIEWS_CONTAINER_NAME)
                            .child(proUser.getUid())
                            .removeValue();

                    //removing user Quality
                    rootRef
                            .child(Constants.FIREBASE_QUALITY_CONTAINER_NAME)
                            .child(proUser.getUid())
                            .removeValue();
                }

                //Remove chats
                final ArrayList<String> uidsChat = new ArrayList<>();
                final ArrayList<String> chatIds = new ArrayList<>();

                rootRef
                        .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                        .child(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    uidsChat.add(ds.getKey());
                                    ChatOverview chatOverview = ds.getValue(ChatOverview.class);
                                    chatIds.add(chatOverview.getChatID());
                                }

                                rootRef.child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                                        .child(user.getUid())
                                        .removeValue();

                                for (String uid : uidsChat) {
                                    rootRef
                                            .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                                            .child(uid)
                                            .child(user.getUid()).removeValue();
                                }

                                for (String chatId : chatIds) {
                                    rootRef
                                            .child(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                                            .child(chatId)
                                            .removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                //Removing image
                StorageReference storage = FirebaseStorage.getInstance().getReference();
                StorageReference userRef;
                userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
                userRef.delete();

                //Removing from Firebase Auth
                fbUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Signing out
                        mAuth.signOut();
                        goBack();
                    }
                });

            }
        });

        builder.create().show();
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
        Intent intent = new Intent(this, TurnProActivity.class);
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
        menu.findItem(R.id.calificar).setVisible(false);
        menu.findItem(R.id.addContact).setVisible(false);
        iniciarUI();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isRunning = false;
    }

    public void showLoadingScreen() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {
        progressBarContainer.setVisibility(View.GONE);
    }
}
