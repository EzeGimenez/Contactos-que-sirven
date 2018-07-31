package com.visoft.network.Objects;

public class QualityInfo {
    private int calidad, tiempoResp, atencion;

    public QualityInfo() {
        this.calidad = 0;
        this.tiempoResp = 0;
        this.atencion = 0;
    }

    public QualityInfo(int r, int t, int a) {
        this.calidad = r;
        this.tiempoResp = t;
        this.atencion = a;
    }

    public int getAtencion() {
        return atencion;
    }

    public void setAtencion(int atencion) {
        this.atencion = atencion;
    }

    public int getTiempoResp() {
        return tiempoResp;
    }

    public void setTiempoResp(int tiempoResp) {
        this.tiempoResp = tiempoResp;
    }

    public int getCalidad() {
        return calidad;
    }

    public void setCalidad(int calidad) {
        this.calidad = calidad;
    }
}
