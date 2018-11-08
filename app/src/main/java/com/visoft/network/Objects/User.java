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

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean getHasPic() {
        return this.hasPic;
    }

    public void setHasPic(boolean p) {
        this.hasPic = p;
    }

    public int getImgVersion() {
        return imgVersion;
    }

    public void setImgVersion(int imgVersion) {
        this.imgVersion = imgVersion;
    }
}
