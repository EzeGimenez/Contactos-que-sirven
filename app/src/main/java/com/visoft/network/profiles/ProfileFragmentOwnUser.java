package com.visoft.network.profiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.MainActivityNormal;
import com.visoft.network.MainActivityPro;
import com.visoft.network.R;
import com.visoft.network.custom_views.CustomDialog;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.objects.User;
import com.visoft.network.turnpro.TurnProActivity;
import com.visoft.network.util.Constants;

import static android.app.Activity.RESULT_OK;

public class ProfileFragmentOwnUser extends Fragment {
    private static final int RC_CURRENTUSER = 1, RC_DELETEACCOUNT = 2;

    private static LoadingScreen loadingScreen;
    private User user;
    private AccountManager accountManager;
    private AccountManager.ListenerRequestResult listener;

    public static void hideLoadingScreen() {
        loadingScreen.hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingScreen = new LoadingScreen(getContext(), (ViewGroup) view.findViewById(R.id.rootView));

        listener = new AccountManager.ListenerRequestResult() {
            @Override
            public void onRequestResult(boolean result, int requestCode, Bundle data) {
                if (requestCode == RC_CURRENTUSER) {
                    user = (User) data.get("user");
                    getFragmentManager().popBackStack();
                    iniciarUI();
                } else if (requestCode == RC_DELETEACCOUNT) {
                    if (result) {
                        //finish();
                    }
                }
                loadingScreen.hide();
            }
        };

        accountManager = HolderCurrentAccountManager.getCurrent(listener);
        user = accountManager.getCurrentUser(RC_CURRENTUSER);

        if (user == null) {
            loadingScreen.show();
        } else {
            iniciarUI();
        }

        //Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
    }

    private void iniciarUI() {
        if (user != null) {

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            Fragment fragment;
            String id;

            if (user.getIsPro()) {
                fragment = new UserProFragment();

                id = Constants.PRO_USER_FRAGMENT_TAG;
            } else {
                fragment = new UserFragment();
                id = Constants.DEFAULT_USER_FRAGMENT_TAG;
            }
            bundle.putSerializable("user", user);
            fragment.setArguments(bundle);
            transaction.add(R.id.ContainerProfileFragments, fragment, id).commit();
            loadingScreen.hide();
        }
    }

    public void signOut() {
        accountManager.logOut(1);
        if (getActivity() instanceof MainActivityPro) {
            ((MainActivityPro) getActivity()).update();
        } else {
            ((MainActivityNormal) getActivity()).update();
        }
    }

    public void eliminarCuenta() {
        CustomDialog dialog = new CustomDialog(getContext());
        dialog.setTitle(getString(R.string.seguro_eliminar))
                .setNegativeButton(getString(R.string.cancelar), null)
                .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        accountManager.deleteAccount(RC_DELETEACCOUNT);
                        if (getActivity() instanceof MainActivityPro) {
                            ((MainActivityPro) getActivity()).update();
                        } else {
                            ((MainActivityNormal) getActivity()).update();
                        }
                    }
                });

        dialog.show();
    }

    /**
     * Muestra pantalla de edicion del perfil
     */
    public void editarPerfil() {
        if (user != null) {
            Intent intent = new Intent(getContext(), TurnProActivity.class);

            String[] configurators = new String[8];
            configurators[0] = "ConfiguratorRubro";
            configurators[1] = "ConfiguratorWorkScope";
            configurators[2] = "ConfiguratorProfilePic";
            configurators[3] = "ConfiguratorContacto";
            configurators[4] = "ConfiguratorPersonalInfo";
            configurators[5] = "ConfiguratorAcompanante";
            configurators[6] = "ConfiguratorSocialApps";
            configurators[7] = "ConfiguratorCV";

            intent.putExtra("user", user);
            intent.putExtra("configurators", configurators);
            intent.putExtra("isNewUser", false);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            user = HolderCurrentAccountManager.getCurrent(listener).getCurrentUser(RC_CURRENTUSER);
            if (user != null) {
                iniciarUI();
            }
        }
    }
}
