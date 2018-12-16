package com.visoft.network.TurnProFragments;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.visoft.network.Objects.ProUser;
import com.visoft.network.Objects.QualityInfo;
import com.visoft.network.Objects.User;
import com.visoft.network.Profiles.OwnUserProfileActivity;
import com.visoft.network.R;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.DatabaseTimer;

import java.io.ByteArrayOutputStream;

public class TurnProActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ProUser proUser;
    private User user;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private DatabaseTimer timer;
    private Bitmap bitmap;
    private boolean isEditing = true;

    //Componentes gráficas
    private Button btnPrev, btnNext;
    private TextView tvInfo;
    private ConstraintLayout containerProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_pro);

        fragmentManager = getSupportFragmentManager();

        //Inicializacion variables
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tvInfo = findViewById(R.id.tvInfo);
        containerProgressBar = findViewById(R.id.progressBarContainer);

        proUser = (ProUser) getIntent().getSerializableExtra("proUser");

        if (proUser == null) {
            proUser = new ProUser();
            isEditing = false;
            user = (User) getIntent().getSerializableExtra("user");
        }

        database = Database.getDatabase().getReference();
        mAuth = FirebaseAuth.getInstance();

        iniciarUI();
    }

    private void iniciarUI() {
        if (!isEditing) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            RubroGeneralFragment rubroGeneralFragment = new RubroGeneralFragment();

            transaction.add(R.id.ContainerTurnProFragments, rubroGeneralFragment, Constants.RUBRO_GENERAL_FRAGMENT_TAG);
            transaction.commit();
            btnNext.setEnabled(false);
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            WorkScopeFragment fragment = new WorkScopeFragment();

            Bundle bundle = new Bundle();
            bundle.putDouble("latStart", proUser.getMapCenterLat());
            bundle.putDouble("lngStart", proUser.getMapCenterLng());
            bundle.putFloat("zoomStart", proUser.getMapZoom());
            fragment.setArguments(bundle);

            transaction.add(R.id.ContainerTurnProFragments, fragment, Constants.WORK_SCOPE_FRAGMENT_TAG);
            transaction.commit();
        }

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

        btnPrev.setEnabled(true);
        btnPrev.setText(R.string.cancelar);
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

            btnPrev.setText(R.string.previo);

        } else if (actualFragment instanceof RubroEspecificoFragment && actualFragment.getTag().equals(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG)) {

            proUser.setRubroEspecifico(fragmentConfig.getString("rubroEspecifico"));
            fragmentConfig.putString("RubroGeneral", fragmentConfig.getString("rubroEspecifico"));
            Fragment rubroEspecificoFragment = fragmentManager.findFragmentByTag(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG2);
            if (rubroEspecificoFragment == null) {
                rubroEspecificoFragment = new RubroEspecificoFragment();
            }

            rubroEspecificoFragment.setArguments(fragmentConfig);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, rubroEspecificoFragment, Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG2)
                    .addToBackStack(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG)
                    .commit();

            btnPrev.setEnabled(true);

        } else if (actualFragment instanceof RubroEspecificoFragment && actualFragment.getTag().equals(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG2)) {

            proUser.setRubroEspecificoEspecifico(fragmentConfig.getString("rubroEspecifico"));
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.WORK_SCOPE_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new WorkScopeFragment();
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction
                    .replace(R.id.ContainerTurnProFragments, fragment, Constants.WORK_SCOPE_FRAGMENT_TAG)
                    .addToBackStack(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG2)
                    .commit();

        } else if (actualFragment instanceof WorkScopeFragment) {

            ((WorkScopeFragment) actualFragment).setCameraBounds(proUser);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.CHOOSE_PIC_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new ProfilePicFragment();
            }

            if (bitmap != null || proUser.getHasPic()) {
                Bundle bundle = new Bundle();
                if (bitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bundle.putByteArray("bitmapByteArray", byteArray);
                } else {
                    bundle.putBoolean("hasPic", true);
                    bundle.putInt("imgVersion", proUser.getImgVersion());
                }
                fragment.setArguments(bundle);
            }

            transaction
                    .replace(R.id.ContainerTurnProFragments, fragment, Constants.CHOOSE_PIC_FRAGMENT_TAG)
                    .addToBackStack(Constants.WORK_SCOPE_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(true);
            btnPrev.setEnabled(true);
            btnPrev.setText(R.string.previo);

        } else if (actualFragment instanceof ProfilePicFragment) {

            bitmap = ((ProfilePicFragment) actualFragment).getFilePath();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.CONTACTO_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new ContactoFragment();
            }

            if (isEditing) {
                Bundle bundle = new Bundle();
                bundle.putString("tel1", proUser.getTelefono1());
                bundle.putString("tel2", proUser.getTelefono2());
                bundle.putInt("fecha1", proUser.getDiasAtencion() / 10);
                bundle.putInt("fecha2", proUser.getDiasAtencion() % 10);
                bundle.putString("hr1", proUser.getHoraAtencion().split(" - ")[0]);
                bundle.putString("hr2", proUser.getHoraAtencion().split(" - ")[1]);
                bundle.putBoolean("email", proUser.getShowEmail());
                fragment.setArguments(bundle);
            }

            transaction
                    .replace(R.id.ContainerTurnProFragments, fragment, Constants.CONTACTO_FRAGMENT_TAG)
                    .addToBackStack(Constants.CHOOSE_PIC_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(true);
            btnPrev.setEnabled(true);

        } else if (actualFragment instanceof ContactoFragment) {

            if (((ContactoFragment) actualFragment).isContactInfoOk()) {
                ((ContactoFragment) actualFragment).setContactInfo(proUser);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(Constants.SOCIAL_FRAGMENT_TAG);
                if (fragment == null) {
                    fragment = new SocialAppsFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", proUser);
                fragment.setArguments(bundle);

                transaction
                        .replace(R.id.ContainerTurnProFragments, fragment, Constants.SOCIAL_FRAGMENT_TAG)
                        .addToBackStack(Constants.CONTACTO_FRAGMENT_TAG)
                        .commit();

            } else {
                ((ContactoFragment) actualFragment).vibrate();
            }

        } else if (actualFragment instanceof SocialAppsFragment) {

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.CV_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new CVFragment();
            }
            if (isEditing) {
                Bundle bundle = new Bundle();
                bundle.putString("cv", proUser.getCvText());
                fragment.setArguments(bundle);
            }

            transaction
                    .replace(R.id.ContainerTurnProFragments, fragment, Constants.CV_FRAGMENT_TAG)
                    .addToBackStack(Constants.CONTACTO_FRAGMENT_TAG)
                    .commit();

            btnPrev.setEnabled(true);
            btnNext.setEnabled(true);
            btnNext.setText(R.string.finalizar);

        } else if (actualFragment instanceof CVFragment) {

            if (((CVFragment) actualFragment).isInputOk()) {
                ((CVFragment) actualFragment).setCv(proUser);
                saveProUser();
            } else {
                ((CVFragment) actualFragment).vibrate();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.ContainerTurnProFragments);
        if (actualFragment instanceof RubroGeneralFragment) {

            Intent intent = new Intent(this, OwnUserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (actualFragment instanceof RubroEspecificoFragment && actualFragment.getTag().equals(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG)) {

            Fragment rubroGeneralFragment = fragmentManager.findFragmentByTag(Constants.RUBRO_GENERAL_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, rubroGeneralFragment, Constants.RUBRO_GENERAL_FRAGMENT_TAG)
                    .commit();

            btnPrev.setText(R.string.cancelar);
            btnNext.setEnabled(false);
            btnPrev.setEnabled(true);

        } else if (actualFragment instanceof RubroEspecificoFragment && actualFragment.getTag().equals(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG2)) {

            Fragment rubroEspecificoFragment = fragmentManager.findFragmentByTag(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, rubroEspecificoFragment, Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG)
                    .commit();

            btnPrev.setText(R.string.previo);
            btnNext.setEnabled(false);
            btnPrev.setEnabled(false);

        } else if (actualFragment instanceof WorkScopeFragment) {

            if (!isEditing) {
                Fragment rubroEspecificoFragment = fragmentManager.findFragmentByTag(Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG2);
                fragmentManager.beginTransaction()
                        .replace(R.id.ContainerTurnProFragments, rubroEspecificoFragment, Constants.RUBRO_ESPECIFICO_FRAGMENT_TAG2)
                        .commit();

                btnPrev.setText(R.string.previo);
                btnNext.setEnabled(false);
                btnPrev.setEnabled(false);
            } else {
                Intent intent = new Intent(this, OwnUserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        } else if (actualFragment instanceof ProfilePicFragment) {

            Fragment workScopeFragment = fragmentManager.findFragmentByTag(Constants.WORK_SCOPE_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, workScopeFragment, Constants.WORK_SCOPE_FRAGMENT_TAG)
                    .commit();

            if (isEditing) {
                btnPrev.setText(getString(R.string.cancelar));
            }

        } else if (actualFragment instanceof ContactoFragment) {

            Fragment choosePicFragment = fragmentManager.findFragmentByTag(Constants.CHOOSE_PIC_FRAGMENT_TAG);
            if (bitmap != null) {
                Bundle bundle = new Bundle();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bundle.putByteArray("bitmapByteArray", byteArray);
                choosePicFragment.setArguments(bundle);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, choosePicFragment, Constants.CHOOSE_PIC_FRAGMENT_TAG)
                    .commit();

            btnPrev.setEnabled(true);

        } else if (actualFragment instanceof SocialAppsFragment) {

            Fragment contactoFragment = fragmentManager.findFragmentByTag(Constants.CONTACTO_FRAGMENT_TAG);

            Bundle bundle = new Bundle();
            bundle.putSerializable("user", proUser);
            contactoFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, contactoFragment, Constants.CONTACTO_FRAGMENT_TAG)
                    .commit();

        } else if (actualFragment instanceof CVFragment) {

            Fragment contactoFragment = fragmentManager.findFragmentByTag(Constants.SOCIAL_FRAGMENT_TAG);
            fragmentManager.beginTransaction()
                    .replace(R.id.ContainerTurnProFragments, contactoFragment, Constants.SOCIAL_FRAGMENT_TAG)
                    .commit();

            btnNext.setEnabled(true);
            btnPrev.setEnabled(true);
            btnNext.setText(R.string.siguiente);
        }
    }


    public void saveProUser() {
        showLoadingScreen();
        timer = new DatabaseTimer(16, this, false);

        if (!isEditing) {
            proUser.setImgVersion(0);
            proUser.setPro(true);
            proUser.setUsername(user.getUsername());
            proUser.setRating(user.getRating());
            proUser.setEmail(user.getEmail());
            proUser.setUid(user.getUid());
            proUser.setInstanceID(user.getInstanceID());
            proUser.setNumberReviews(user.getNumberReviews());
        }

        saveInPic();
    }

    private void saveInPic() {
        StorageReference storage = FirebaseStorage.getInstance().getReference();

        StorageReference userRef;
        if (isEditing && bitmap != null && proUser.getHasPic()) {
            storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + proUser.getUid() + proUser.getImgVersion() + ".jpg").delete();
            proUser.setImgVersion(proUser.getImgVersion() + 1);
        }

        userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + proUser.getUid() + proUser.getImgVersion() + ".jpg");

        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            userRef.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            proUser.setHasPic(true);
                            saveInUser();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            proUser.setHasPic(false);
                            saveInUser();
                        }
                    });
        } else {
            if (!isEditing) {
                proUser.setHasPic(false);
            }
            saveInUser();
        }
    }

    private void saveInUser() {
        database.child(Constants.FIREBASE_USERS_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid()).setValue(proUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                saveInRubro();
            }
        });
    }

    private void saveInRubro() {
        database.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME)
                .child(proUser.getRubroEspecificoEspecifico())
                .child(mAuth.getCurrentUser().getUid())
                .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getApplication(), OwnUserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                timer.cancel();
                if (!isEditing) {
                    saveInQuality();
                } else {
                    goBack();
                }
            }
        });
    }

    private void saveInQuality() {
        QualityInfo qualityInfo = new QualityInfo();
        database.child(Constants.FIREBASE_QUALITY_CONTAINER_NAME)
                .child(mAuth.getCurrentUser().getUid())
                .setValue(qualityInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                goBack();
            }
        });
    }

    private void goBack() {
        Intent intent = new Intent(getApplication(), OwnUserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        hideLoadingScreen();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    private void showLoadingScreen() {
        containerProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        containerProgressBar.setVisibility(View.GONE);
    }
}
