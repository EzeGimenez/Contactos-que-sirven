package com.visoft.network.funcionalidades;

import android.content.Intent;
import android.os.Bundle;

import com.visoft.network.Objects.User;
import com.visoft.network.exceptions.InvalidEmailException;
import com.visoft.network.exceptions.InvalidPasswordException;
import com.visoft.network.exceptions.InvalidUsernameException;
import com.visoft.network.exceptions.LogInException;

/**
 * Administra la cuenta del cliente
 */
public abstract class AccountManager {

    /**
     * Iniciar sesion
     *
     * @param nombre   nombre de usuario
     * @param password contraseña
     */
    public abstract void logInWithEmail(String nombre, String password, int requestCode) throws LogInException, InvalidUsernameException, InvalidPasswordException, InvalidEmailException;

    /**
     * login with google
     */
    public abstract void logInWithGoogle(int requestCode);

    /**
     * Logs in with facebook
     */
    public abstract void logInWithFacebook(int requestCode);

    /**
     * Cerrar sesion
     */
    public abstract void logOut(int requestCode);

    /**
     * Obtiene cuenta
     *
     * @return UserNormal del usuario, null si no hay
     */
    public abstract User getCurrentUser(int requestCode);

    /**
     * Restarts its parameter
     */
    public abstract void invalidate();

    /**
     * Crear cuenta con las credenciales propuestas
     *
     * @param nombre nombre de usuario
     * @param mail   mail del usuario
     * @param pw     contraseña del usuario
     * @return true si se pudo crear, false en caso contrario
     */
    public abstract void signUp(String nombre, String mail, String pw, int requestCode) throws InvalidUsernameException, InvalidPasswordException, InvalidEmailException;

    /**
     * @param requestCode
     * @param resultCode
     * @param intent
     * @return
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent intent);

    public abstract void deleteAccount(int requestCode);

    public abstract void setListener(ListenerRequestResult l);

    public abstract static class ListenerRequestResult {
        public abstract void onRequestResult(boolean result, int requestCode, Bundle data);
    }
}
