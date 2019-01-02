package com.visoft.network.Objects;

import android.view.View;

import com.visoft.network.MainPageSearch.FragmentSearchResults;
import com.visoft.network.R;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Clase UserNormal, usada para almacenar la informacion de un usuario
 */
public abstract class User extends AbstractFlexibleItem<FragmentSearchResults.ViewHolderProUser> implements Serializable {
    protected String uid;
    protected boolean isPro, hasPic;
    private String username;
    private String email;
    private float rating;
    private int numberReviews, imgVersion;

    /**
     * Constructor de la clase
     */
    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            return getUid().equals(((User) o).getUid());
        }
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.pro_user_layout;
    }

    @Override
    public FragmentSearchResults.ViewHolderProUser createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new FragmentSearchResults.ViewHolderProUser(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, FragmentSearchResults.ViewHolderProUser holder, int position, List<Object> payloads) {
        holder.ratingBar.setRating(getRating());
        holder.tvNumReviews.setText(getNumberReviews() + "");
        holder.tvUsername.setText(getUsername());
    }

    public User setInstanceID(String instanceID) {
        String instanceID1 = instanceID;
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

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public abstract String getUid();

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

    public boolean getIsPro() {
        return isPro;
    }

    public void setPro(boolean pro) {
        isPro = pro;
    }
}
