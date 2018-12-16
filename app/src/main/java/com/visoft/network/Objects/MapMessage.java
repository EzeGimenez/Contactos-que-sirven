package com.visoft.network.Objects;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class MapMessage extends Message {
    private LatLng pos;

    public MapMessage() {

    }

    public Message setPosition(LatLng a) {
        this.pos = a;
        return this;
    }

    @Override
    public String getOverview() {
        return "Location";
    }

    @Override
    public Message fillHolder(Context context, ViewHolderChats holder) {

        holder.disableText();
        holder.enableMap();
        holder.setPosition(pos);

        return null;
    }
}
