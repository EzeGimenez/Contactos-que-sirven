package com.visoft.network.funcionalidades;

import android.content.Intent;

import com.visoft.network.Objects.User;
import com.visoft.network.exceptions.InvalidEmailException;
import com.visoft.network.exceptions.InvalidPasswordException;
import com.visoft.network.exceptions.InvalidUsernameException;
import com.visoft.network.exceptions.LogInException;

/**
 * Administra la cuenta del cliente
 */
public interface AccountManager {

    /**
     * Iniciar sesion
     *
     * @param nombre   nombre de usuario
     * @param password contraseña
     */
    void logInWithEmail(String nombre, String password, int requestCode) throws LogInException, InvalidUsernameException, InvalidPasswordException, InvalidEmailException;

    /**
     * login with google
     */
    void logInWithGoogle(int requestCode);

    /**
     * Logs in with facebook
     */
    void logInWithFacebook(int requestCode);

    /**
     * Cerrar sesion
     */
    void logOut(int requestCode);

    /**
     * Obtiene cuenta
     *
     * @return UserNormal del usuario, null si no hay
     */
    User getCurrentUser(int requestCode);

    /**
     * Restarts its parameter
     */
    void invalidate();

    /**
     * Crear cuenta con las credenciales propuestas
     *
     * @param nombre nombre de usuario
     * @param mail   mail del usuario
     * @param pw     contraseña del usuario
     * @return true si se pudo crear, false en caso contrario
     */
    void signUp(String nombre, String mail, String pw, int requestCode) throws InvalidUsernameException, InvalidPasswordException, InvalidEmailException;

    /**
     * @param requestCode
     * @param resultCode
     * @param intent
     * @return
     */
    void onActivityResult(int requestCode, int resultCode, Intent intent);

    void deleteAccount(int requestCode);
}
