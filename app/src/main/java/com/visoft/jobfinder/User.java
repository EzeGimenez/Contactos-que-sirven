package com.visoft.jobfinder;

import java.io.Serializable;

/**
 * Clase User, usada para almacenar la informacion de un usuario
 */
public class User implements Serializable {
    protected String username;
    protected float rating;
    protected int numberReviews;
    protected boolean isPro;

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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getNumberReviews() {
        return numberReviews;
    }

    public void setNumberReviews(int numberReviews) {
        this.numberReviews = numberReviews;
    }

    public boolean getIsPro() {
        return isPro;
    }

    public void setPro(boolean pro) {
        isPro = pro;
    }
}
