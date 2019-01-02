package com.visoft.network.Objects;

import com.visoft.network.MainPageSearch.FragmentSearchResults;
import com.visoft.network.MainPageSearch.VisitorUser;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;

public class UserPro extends User implements IFilterable {
    private String telefono1, telefono2;
    private String cvText;
    private double mapCenterLat, mapCenterLng;
    private String horaAtencion, rubroGeneral, rubroEspecifico, rubroNombre;
    private String facebookID, instagramID, whatsappNum;
    private boolean showEmail;
    private float mapZoom;
    private int diasAtencion;
    private String rubroEspecificoEspecifico;

    public UserPro() {
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

    public String getRubroEspecificoEspecifico() {
        return rubroEspecificoEspecifico;
    }

    public void setRubroEspecificoEspecifico(String rubroEspecificoEspecifico) {
        this.rubroEspecificoEspecifico = rubroEspecificoEspecifico;
    }

    public void acept(VisitorUser v) {
        v.visit(this);
    }

    public void setRubroNombre(String a) {
        this.rubroNombre = a;
    }

    @Override
    public boolean filter(Serializable constraint) {
        String filter = constraint.toString().toLowerCase().trim();

        if (getUsername().toLowerCase().trim().contains(filter)) {
            return true;
        }
        if (getRubroEspecificoEspecifico().toLowerCase().trim().contains(filter)) {
            return true;
        }

        return false;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, FragmentSearchResults.ViewHolderProUser holder, int position, List<Object> payloads) {
        holder.ratingBar.setRating(getRating());
        holder.tvNumReviews.setText(getNumberReviews() + "");
        holder.tvUsername.setText(getUsername());
        holder.tvRubro.setText(rubroNombre);
    }

    @Override
    public String getUid() {
        return uid;
    }
}
