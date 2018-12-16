package com.visoft.network.Util;

/**
 * Constants class
 */
public abstract class Constants {

    /**
     * Fragment Tags
     */
    public static final String LOGIN_FRAGMENT_TAG = "SignInFragment";
    public static final String SIGNUP_FRAGMENT_TAG = "SignUpActivity";
    public static final String MAIN_PAGE_FRAGMENT_TAG = "MainPageFragment";
    public static final String PRO_USER_FRAGMENT_TAG = "ProUserFragment";
    public static final String DEFAULT_USER_FRAGMENT_TAG = "DefaultUserFragment";
    public static final String WORK_SCOPE_FRAGMENT_TAG = "WorkScopeFragment";
    public static final String CONTACTO_FRAGMENT_TAG = "ContactoFragment";
    public static final String CV_FRAGMENT_TAG = "CVFragment";
    public static final String RUBRO_ESPECIFICO_MAIN_FRAGMENT_TAG = "RubroEspecificoMainFragment";
    public static final String RUBRO_ESPECIFICO_FRAGMENT_TAG = "RubroEspecificoFragment";
    public static final String RUBRO_ESPECIFICO_FRAGMENT_TAG2 = "RubroEspecificoFragment2";
    public static final String RUBRO_GENERAL_FRAGMENT_TAG = "RubroGeneralFragment";
    public static final String SUB_RUBROS_FRAGMENT_TAG = "SubRubrosFragment";
    public static final String SUB_RUBROS_FRAGMENT_TAG2 = "SubRubrosFragment2";
    public static final String SEARCH_RESULT_FRAGMENT_TAG = "SearchResultFragment";
    public static final String CHOOSE_PIC_FRAGMENT_TAG = "ChoosePicFragment";
    public static final String SOCIAL_FRAGMENT_TAG = "SocialAppsFragment";
    public static final String ALL_CHATS_FRAGMENT_NAME = "AllChatsFragment";
    public static final String SPECIFIC_CHAT_FRAGMENT_TAG = "SpecificChatFragmentTag";

    /**
     * Firebase database
     */
    public static final String FIREBASE_RUBRO_CONTAINER_NAME = "rubros_generales";
    public static final String FIREBASE_USERS_CONTAINER_NAME = "users";
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
    public static final int MAX_RESULTS_SIZE = 20;

    public static final String NOTIFICATION_CHAT_CHANNEL_ID = "notification_channel";
    public static final String NOTIFICATION_CHAT_CHANNEL_NAME = "chatNot";
}
