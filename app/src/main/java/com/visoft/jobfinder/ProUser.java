package com.visoft.jobfinder;

public class ProUser extends User {
    private String telefono1, telefono2;
    private String cvText;
    private double mapBound1Lat, mapBound1Long, mapBound2Lat, mapBound2Long, mapCenterLat, mapCenterLng;
    private String diasAtencion, horaAtencion, rubroGeneral, rubroEspecifico;
    private boolean showEmail;
    private float mapZoom;

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

    public double getMapBound1Long() {
        return mapBound1Long;
    }

    public void setMapBound1Long(double mapBound1Long) {
        this.mapBound1Long = mapBound1Long;
    }

    public double getMapBound1Lat() {
        return mapBound1Lat;
    }

    public void setMapBound1Lat(double mapBound1Lat) {
        this.mapBound1Lat = mapBound1Lat;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getDiasAtencion() {
        return diasAtencion;
    }

    public void setDiasAtencion(String diasAtencion) {
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

    public double getMapBound2Long() {
        return mapBound2Long;
    }

    public void setMapBound2Long(double mapBound2Long) {
        this.mapBound2Long = mapBound2Long;
    }

    public double getMapBound2Lat() {
        return mapBound2Lat;
    }

    public void setMapBound2Lat(double mapBound2Lat) {
        this.mapBound2Lat = mapBound2Lat;
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
}
