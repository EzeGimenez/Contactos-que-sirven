package com.visoft.network.objects;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.R;
import com.visoft.network.tab_search.FragmentSearchResults;
import com.visoft.network.util.Constants;
import com.visoft.network.util.GlideApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;

public class UserPro extends User implements IFilterable {
    private String telefono1 = "", telefono2 = "";
    private String cvText = "";
    private double mapCenterLat, mapCenterLng;
    private String horaAtencion = "", dni = "";
    private String facebookID = "", instagramID = "", whatsappNum = "", direccion, obrasocial, patente;
    private List<String> rubro;
    private List<Acompanante> acompanantes;
    private boolean showEmail, movilidadPropia, debit, credit;
    private float mapZoom;
    private int diasAtencion = -1;

    public UserPro() {
        rubro = new ArrayList<>();
        acompanantes = new ArrayList<>();
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

    public void setRubro(List<String> rubro) {
        this.rubro = rubro;
    }

    public List<String> getRubros() {
        return rubro;
    }

    @Override
    public boolean filter(Serializable constraint) {
        String filter = constraint.toString().toLowerCase().trim();

        if (getUsername().toLowerCase().trim().contains(filter)) {
            return true;
        }

        for (String a : rubro) {
            if (a.toLowerCase().contains(filter)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, FragmentSearchResults.ViewHolderProUser holder, int position, List<Object> payloads) {
        holder.ratingBar.setRating(getRating());
        holder.tvNumReviews.setText(getNumberReviews() + "");
        holder.tvUsername.setText(getUsername());

        String aux = "";
        Context context = holder.getContext();
        for (String s : getRubros()) {
            int id = context.getResources().getIdentifier(s, "string", context.getPackageName());
            aux += context.getString(id) + "   ";
        }

        holder.tvRubro.setText(aux);

        if (hasPic) {
            StorageReference storage = FirebaseStorage.getInstance().getReference();

            StorageReference userRef = storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + getUid() + getImgVersion() + ".jpg");
            GlideApp.with(context)
                    .load(userRef)
                    .into(holder.img);
        } else {
            holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.profile_pic));
        }
    }

    @Override
    public String getUid() {
        return uid;
    }

    public boolean isMovilidadPropia() {
        return movilidadPropia;
    }

    public void setMovilidadPropia(boolean movilidadPropia) {
        this.movilidadPropia = movilidadPropia;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getObrasocial() {
        return obrasocial;
    }

    public void setObrasocial(String obrasocial) {
        this.obrasocial = obrasocial;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public List<Acompanante> getAcompanantes() {
        return acompanantes;
    }

    public void setAcompanantes(List<Acompanante> acompanantes) {
        this.acompanantes = acompanantes;
    }

    public boolean isDebit() {
        return debit;
    }

    public void setDebit(boolean debit) {
        this.debit = debit;
    }

    public boolean isCredit() {
        return credit;
    }

    public void setCredit(boolean credit) {
        this.credit = credit;
    }
}
