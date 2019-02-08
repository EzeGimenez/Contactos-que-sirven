package com.visoft.network.util;

/**
 * Constants class
 */
public abstract class Constants {

    /**
     * Fragment Tags
     */
    public static final String PRO_USER_FRAGMENT_TAG = "UserProFragment";
    public static final String DEFAULT_USER_FRAGMENT_TAG = "UserFragment";
    public static final String ALL_CHATS_FRAGMENT_NAME = "AllChatsFragment";

    /**
     * Firebase database
     */
    public static final String FIREBASE_RUBRO_CONTAINER_NAME = "rubros";
    public static final String FIREBASE_USERS_NORMAL_CONTAINER_NAME = "usersNormal";
    public static final String FIREBASE_USERS_PRO_CONTAINER_NAME = "usersPro";
    public static final String FIREBASE_QUALITY_CONTAINER_NAME = "userQuality";
    public static final String FIREBASE_REVIEWS_CONTAINER_NAME = "reviews";
    public static final String FIREBASE_CONTACTS_CONTAINER_NAME = "contacts";
    public static final String FIREBASE_CHATS_CONTAINER_NAME = "chats";
    public static final String FIREBASE_MESSAGES_CONTAINER_NAME = "messages";
    public static final String FIREBASE_NOTIFICATIONS_CONTAINER_NAME = "notifications";

    /**
     * Constants
     */
    public static final int MAX_CARACTERES = 210;
    public static final int MIN_DISTANCE = 50 * 1000;
    public static final int MIN_CALIDAD_INSIGNIA = 50;
    public static final int MIN_TIEMPO_RESP_INSIGNIA = 50;
    public static final int MIN_ATENCION_INSIGNIA = 50;
    public static final String SHARED_PREF_NAME = "SharedPref";

    public static final String NOTIFICATION_CHAT_CHANNEL_ID = "notification_channel";
    public static final String NOTIFICATION_CHAT_CHANNEL_NAME = "chatNot";
    public static final String COUNTER_CONTRACTS = "contractsMade";
}
