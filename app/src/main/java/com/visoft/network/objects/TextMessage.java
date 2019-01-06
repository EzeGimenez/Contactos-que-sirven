package com.visoft.network.objects;

import android.content.Context;

public class TextMessage extends Message {

    private String text;

    public TextMessage() {

    }

    public Message setMessage(String a) {
        this.text = a;
        return this;
    }

    @Override
    public String getOverview() {
        return text;
    }

    @Override
    public Message fillHolder(Context context, ViewHolderChats holder) {
        holder.enableText();
        holder.setText(text);
        holder.disableMap();
        return this;
    }

}
