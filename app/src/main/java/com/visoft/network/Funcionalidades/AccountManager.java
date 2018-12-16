package com.visoft.network.Funcionalidades;

import android.content.Intent;

import com.visoft.network.Objects.User;
import com.visoft.network.exceptions.InvalidEmailException;
import com.visoft.network.exceptions.InvalidPasswordException;
import com.visoft.network.exceptions.InvalidUsernameException;

import exceptions.LogInException;

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
    void logInWithEmail(String nombre, String password) throws InvalidUsernameException, InvalidPasswordException, InvalidEmailException, LogInException, InvalidUsernameException, InvalidPasswordException, InvalidEmailException;

    /**
     * login with google
     */
    void logInWithGoogle();

    /**
     * Logs in with facebook
     */
    void logInWithFacebook();

    /**
     * Cerrar sesion
     */
    void logOut();

    /**
     * Obtiene cuenta
     *
     * @return User del usuario, null si no hay
     */
    User getCurrentAccount();

    /**
     * Crear cuenta con las credenciales propuestas
     *
     * @param nombre nombre de usuario
     * @param mail   mail del usuario
     * @param pw     contraseña del usuario
     * @return true si se pudo crear, false en caso contrario
     */
    void signUp(String nombre, String mail, String pw) throws InvalidUsernameException, InvalidPasswordException, InvalidEmailException;

    /**
     * @param requestCode
     * @param resultCode
     * @param intent
     * @return
     */
    boolean onActivityResult(int requestCode, int resultCode, Intent intent);
}
