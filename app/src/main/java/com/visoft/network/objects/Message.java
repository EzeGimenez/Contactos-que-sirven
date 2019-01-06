package com.visoft.network.objects;

import android.content.Context;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private String author;
    private long timeStamp;

    /**
     * Getter for the timestamp
     *
     * @return long timestamp
     */
    public final long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Setter for the timeStamp
     *
     * @param timeStamp long timestamp
     * @return this to follow the "Builder" pattern
     */
    public final Message setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * Getter for the author
     *
     * @return String of the author
     */
    public final String getAuthor() {
        return author;
    }

    /**
     * Setter for the author
     *
     * @param author String containing the id of the author
     * @return this to follow the "Builder" pattern
     */
    public final Message setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Used to retrieve an overview of the message as String
     *
     * @return String containing the overview
     */
    public abstract String getOverview();


    /**
     * Method to fill the view holder
     *
     * @param holder holder to fill
     * @return this to follow the "Builder" pattern
     */
    public abstract Message fillHolder(Context context, ViewHolderChats holder);

}
