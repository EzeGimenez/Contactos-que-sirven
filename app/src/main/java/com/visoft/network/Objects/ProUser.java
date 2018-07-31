package com.visoft.network.Objects;

public class ProUser extends User {
    private String telefono1, telefono2;
    private String cvText;
    private double mapCenterLat, mapCenterLng;
    private String horaAtencion, rubroGeneral, rubroEspecifico;
    private String facebookID, instagramID, whatsappNum;
    private boolean showEmail;
    private float mapZoom;
    private int diasAtencion;

    public ProUser() {
        isPro = true;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getCvText() {
        return cvText;
    }

    public void setCvText(String cvText) {
        this.cvText = cvText;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public int getDiasAtencion() {
        return diasAtencion;
    }

    public void setDiasAtencion(int diasAtencion) {
        this.diasAtencion = diasAtencion;
    }

    public String getRubroGeneral() {
        return rubroGeneral;
    }

    public void setRubroGeneral(String rubroGeneral) {
        this.rubroGeneral = rubroGeneral;
    }

    public String getRubroEspecifico() {
        return rubroEspecifico;
    }

    public void setRubroEspecifico(String rubroEspecifico) {
        this.rubroEspecifico = rubroEspecifico;
    }

    public boolean getShowEmail() {
        return showEmail;
    }

    public void setShowEmail(boolean showEmail) {
        this.showEmail = showEmail;
    }

    public String getHoraAtencion() {
        return horaAtencion;
    }

    public void setHoraAtencion(String horaAtencion) {
        this.horaAtencion = horaAtencion;
    }

    public float getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(float mapZoom) {
        this.mapZoom = mapZoom;
    }

    public double getMapCenterLng() {
        return mapCenterLng;
    }

    public void setMapCenterLng(double mapCenterLng) {
        this.mapCenterLng = mapCenterLng;
    }

    public double getMapCenterLat() {
        return mapCenterLat;
    }

    public void setMapCenterLat(double mapCenterLat) {
        this.mapCenterLat = mapCenterLat;
    }

    public String getWhatsappNum() {
        return whatsappNum;
    }

    public void setWhatsappNum(String whatsappNum) {
        this.whatsappNum = whatsappNum;
    }

    public String getInstagramID() {
        return instagramID;
    }

    public void setInstagramID(String instagramID) {
        this.instagramID = instagramID;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }
}
