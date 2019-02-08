package com.visoft.network.objects;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class MessageMap extends Message {
    private LatLng pos;

    public MessageMap() {

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

        return this;
    }
}
