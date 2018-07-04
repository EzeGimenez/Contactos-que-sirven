package com.visoft.jobfinder;

public class ProUser extends User {
    private String telefono1, telefono2;
    private String cvText;
    private float mapBoundX, mapBoundY;
    private String horarioAtencion;

    public ProUser() {
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

    public float getMapBoundY() {
        return mapBoundY;
    }

    public void setMapBoundY(float mapBoundY) {
        this.mapBoundY = mapBoundY;
    }

    public float getMapBoundX() {
        return mapBoundX;
    }

    public void setMapBoundX(float mapBoundX) {
        this.mapBoundX = mapBoundX;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getHorarioAtencion() {
        return horarioAtencion;
    }

    public void setHorarioAtencion(String horarioAtencion) {
        this.horarioAtencion = horarioAtencion;
    }
}
