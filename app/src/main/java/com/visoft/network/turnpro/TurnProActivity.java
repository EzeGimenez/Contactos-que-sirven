package com.visoft.network.turnpro;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.visoft.network.R;
import com.visoft.network.custom_views.CustomDialog;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.GsonerUser;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.objects.QualityInfo;
import com.visoft.network.objects.User;
import com.visoft.network.objects.UserPro;
import com.visoft.network.util.Constants;

import java.util.ArrayList;

public class TurnProActivity extends AppCompatActivity implements View.OnClickListener {

    private final int containerFrags = R.id.ContainerConfigurators;
    private FragmentManager fragmentManager;
    private UserPro user;
    private DatabaseReference ref;
    private boolean isNewUser;
    private int current;
    private boolean finished = false;

    private TextView tvInfo;

    private ArrayList<ConfiguratorTurnPro> configurators;

    private LoadingScreen loadingScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_pro);

        fragmentManager = getSupportFragmentManager();
        loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));

        tvInfo = findViewById(R.id.tvInfo);

        user = (UserPro) getIntent().getSerializableExtra("user");
        isNewUser = getIntent().getBooleanExtra("isNewUser", false);

        findViewById(R.id.btnNext).setOnClickListener(this);
        findViewById(R.id.btnPrev).setOnClickListener(this);

        String[] aux = getIntent().getStringArrayExtra("configurators");
        configurators = new ArrayList<>();
        for (String s : aux) {
            ConfiguratorTurnPro f = null;
            try {
                f = (ConfiguratorTurnPro) (Class.forName("com.visoft.network.turnpro." + s).newInstance());
                f.setUser(user);
            } catch (Exception ignored) {
            }

            if (f != null) {
                configurators.add(f);
            }
        }

        ref = FirebaseDatabase.getInstance().getReference();
        current = 0;

        fragmentManager.beginTransaction()
                .add(containerFrags, configurators.get(0), configurators.get(0).getDescriptor())
                .commit();

        int id = getResources().getIdentifier(configurators.get(0).getDescriptor(), "string", getPackageName());
        tvInfo.setText(getString(id));
    }

    @Override
    public void onBackPressed() {
        ConfiguratorTurnPro aux = (ConfiguratorTurnPro) fragmentManager.findFragmentById(containerFrags);
        if (aux.handleBackPress()) {
            return;
        }

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            current--;
            int id = getResources().getIdentifier(configurators.get(current).getDescriptor(), "string", getPackageName());
            tvInfo.setText(getString(id));
        } else {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle(getString(R.string.seguro_salir))
                    .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TurnProActivity.super.onBackPressed();
                        }
                    });
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                avanzar();
                break;
            case R.id.btnPrev:
                onBackPressed();
                break;
        }
    }

    private void avanzar() {
        ConfiguratorTurnPro actual = configurators.get(current);
        if (actual.canContinue()) {
            if (current < configurators.size() - 1) {

                hideKeyboard();
                ConfiguratorTurnPro siguiente = configurators.get(current + 1);

                fragmentManager.beginTransaction()
                        .addToBackStack(actual.getTag())
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(containerFrags, siguiente, siguiente.getDescriptor())
                        .commit();

                int id = getResources().getIdentifier(siguiente.getDescriptor(), "string", getPackageName());
                tvInfo.setText(getString(id));

                current++;
            } else {
                actual.finalizar();
                exitSuccessfully();
            }
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void exitSuccessfully() {
        loadingScreen.show();
        finished = true;
        saveInUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!finished
        ) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void saveInUser() {
        String json = GsonerUser.getGson().toJson(user, User.class);
        ref.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME)
                .child(user.getUid()).setValue(json).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                saveInRubro();
            }
        });

        AccountManager accountManager = HolderCurrentAccountManager.getCurrent(null);
        accountManager.invalidate();
    }

    private void saveInRubro() {

        ref
                .child(Constants.FIREBASE_RUBRO_CONTAINER_NAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            d.child(user.getUid()).getRef().removeValue();
                        }

                        for (String a : user.getRubros()) {
                            ref.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME)
                                    .child(a)
                                    .child(user.getUid())
                                    .setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (isNewUser) {
            saveInQuality();
        } else {
            goBack(true);
        }
    }

    private void saveInQuality() {
        QualityInfo qualityInfo = new QualityInfo();
        ref.child(Constants.FIREBASE_QUALITY_CONTAINER_NAME)
                .child(user.getUid())
                .setValue(qualityInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                goBack(true);
            }
        });
    }

    private void goBack(boolean result) {
        if (result) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
