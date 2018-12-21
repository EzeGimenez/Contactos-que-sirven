package com.visoft.network.Profiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.Objects.User;
import com.visoft.network.R;
import com.visoft.network.TurnProFragments.TurnProActivity;
import com.visoft.network.Util.Constants;
import com.visoft.network.custom_views.CustomDialog;
import com.visoft.network.funcionalidades.AccountActivity;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebase;
import com.visoft.network.funcionalidades.LoadingScreen;

public class ProfileActivityOwnUser extends AccountActivity {
    private static final int RC_CURRENTUSER = 1, RC_DELETEACCOUNT = 2;

    private static boolean isRunning;
    private User user;

    private AccountManager accountManager;
    private LoadingScreen loadingScreen;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.accountManager = AccountManagerFirebase.getInstance(this);
        this.loadingScreen = new LoadingScreen(this, (ViewGroup) findViewById(R.id.rootView));

        user = accountManager.getCurrentUser(RC_CURRENTUSER);
        loadingScreen.show();

        //Toolbar
        //Componentes gráficas
        Toolbar toolbar = findViewById(R.id.toolbar);
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

    private void iniciarUI() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));
        if (menu != null && user != null) {
            loadingScreen.hide();
            MenuItem convertirEnProIcon = menu.findItem(R.id.convertirEnPro);
            MenuItem editarPerfil = menu.findItem(R.id.edit);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            Fragment fragment;
            String id;
            if ((user == null || user.getIsPro())) {
                editarPerfil.setVisible(true);
                convertirEnProIcon.setVisible(false);
                fragment = new UserProFragment();
                bundle.putSerializable("user", user);
                id = Constants.PRO_USER_FRAGMENT_TAG;
            } else {
                editarPerfil.setVisible(false);
                convertirEnProIcon.setVisible(true);
                fragment = new UserFragment();
                id = Constants.DEFAULT_USER_FRAGMENT_TAG;
                bundle.putSerializable("user", user);
            }
            fragment.setArguments(bundle);
            transaction.replace(R.id.ContainerProfileFragments, fragment, id);
            if (isRunning) {
                transaction.commit();
            }
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
                accountManager.logOut(1);
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
        CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(getString(R.string.seguro_eliminar))
                .setNegativeButton(getString(R.string.cancelar), null)
                .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        accountManager.deleteAccount(RC_DELETEACCOUNT);
                    }
                });

        dialog.show();
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
        intent.putExtra("proUser", user);
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isRunning = false;
    }

    @Override
    public void onRequestResult(boolean result, int requestCode, Bundle data) {
        if (requestCode == RC_CURRENTUSER) {
            user = (User) data.get("user");
            invalidateOptionsMenu();
        } else if (requestCode == RC_DELETEACCOUNT) {
            if (result) {
                finish();
            }
        }
        loadingScreen.hide();
    }
}
