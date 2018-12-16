package com.visoft.network.Objects;

import java.io.Serializable;

/**
 * Clase User, usada para almacenar la informacion de un usuario
 */
public class User implements Serializable {
    protected String username, email, uid;
    protected float rating;
    protected int numberReviews, imgVersion;
    protected boolean isPro, hasPic;
    protected String instanceID;

    /**
     * Constructor de la clase
     */
    public User() {
    }

    public String getInstanceID() {
        return instanceID;
    }

    public User setInstanceID(String instanceID) {
        this.instanceID = instanceID;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String name) {
        this.username = name;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public User setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public int getNumberReviews() {
        return numberReviews;
    }

    public User setNumberReviews(int numberReviews) {
        this.numberReviews = numberReviews;
        return this;
    }

    public boolean getIsPro() {
        return isPro;
    }

    public User setPro(boolean pro) {
        isPro = pro;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public boolean getHasPic() {
        return this.hasPic;
    }

    public User setHasPic(boolean p) {
        this.hasPic = p;
        return this;
    }

    public int getImgVersion() {
        return imgVersion;
    }

    public User setImgVersion(int imgVersion) {
        this.imgVersion = imgVersion;
        return this;
    }
}
