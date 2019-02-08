package com.visoft.network.objects;

import android.content.Context;

public class MessageContractFinished extends Message {

    private String finishedAt;

    public MessageContractFinished(String a) {
        finishedAt = a;
    }

    @Override
    public String getOverview() {
        return finishedAt;
    }

    @Override
    public Message fillHolder(Context context, ViewHolderChats holder) {
        holder.enableText();
        holder.setText(finishedAt);
        holder.disableMap();

        return this;
    }

}
