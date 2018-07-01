package com.visoft.jobfinder;

import java.io.Serializable;

/**
 * Clase User, usada para almacenar la informacion de un usuario
 */
public class User implements Serializable {
    private String username;

    /**
     * Constructor de la clase
     */
    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }
}
