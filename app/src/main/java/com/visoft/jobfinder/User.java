package com.visoft.jobfinder;

/**
 * Clase User, usada para almacenar la informacion de un usuario
 */
public class User {
    private String name;
    private String email;
    private String password;
    private String userId;

    /**
     * Constructor de la clase
     *
     * @param n  nombre del Usuario
     * @param e  email del usuario
     * @param pw contraseña del usuario
     */
    public User(String id, String n, String e, String pw) {
        this.userId = id;
        this.name = n;
        this.email = e;
        this.password = pw;
    }

    public User() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id){
        this.userId = id;
    }
}
