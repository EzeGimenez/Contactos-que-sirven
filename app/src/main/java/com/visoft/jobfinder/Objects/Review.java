package com.visoft.jobfinder.Objects;

import java.io.Serializable;

public class Review implements Serializable {
    private String reviewerUsername, msg, reviewerUID;
    private float rating, atencion, tiempoResp, calidad;
    private int reviewerImgVersion;

    public Review() {
        rating = atencion = tiempoResp = calidad = 0;
    }

    public float getCalidad() {
        return calidad;
    }

    public void setCalidad(float calidad) {
        this.calidad = calidad;
    }

    public float getTiempoResp() {
        return tiempoResp;
    }

    public void setTiempoResp(float tiempoResp) {
        this.tiempoResp = tiempoResp;
    }

    public float getAtencion() {
        return atencion;
    }

    public void setAtencion(float atencion) {
        this.atencion = atencion;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public void setReviewerUsername(String reviewerUsername) {
        this.reviewerUsername = reviewerUsername;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReviewerUID() {
        return reviewerUID;
    }

    public void setReviewerUID(String reviewerUID) {
        this.reviewerUID = reviewerUID;
    }

    public int getReviewerImgVersion() {
        return reviewerImgVersion;
    }

    public void setReviewerImgVersion(int reviewerImgVersion) {
        this.reviewerImgVersion = reviewerImgVersion;
    }
}
