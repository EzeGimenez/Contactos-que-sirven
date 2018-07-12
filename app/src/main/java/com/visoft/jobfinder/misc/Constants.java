package com.visoft.jobfinder.misc;

/**
 * Constants class
 */
public abstract class Constants {

    /**
     * Fragment Tags
     */
    public static final String LOGIN_FRAGMENT_TAG = "SignInFragment";
    public static final String SIGNUP_FRAGMENT_TAG = "SignUpFragment";
    public static final String SIGNIN_FRAGMENT_TAG = "SignInFragment";
    public static final String MAIN_PAGE_FRAGMENT_TAG = "MainPageFragment";
    public static final String PRO_USER_FRAGMENT_TAG = "ProUserFragment";
    public static final String DEFAULT_USER_FRAGMENT = "DefaultUserFragment";
    public static final String WORK_SCOPE_FRAGMENT_TAG = "WorkScopeFragment";
    public static final String CONTACTO_FRAGMENT_TAG = "ContactoFragment";
    public static final String CV_FRAGMENT_TAG = "CVFragment";
    public static final String RUBRO_ESPECIFICO_MAIN_FRAGMENT_TAG = "RubroEspecificoMainFragment";
    public static final String RUBRO_ESPECIFICO_FRAGMENT_TAG = "RubroEspecificoFragment";
    public static final String RUBRO_GENERAL_FRAGMENT_TAG = "RubroGeneralFragment";
    public static final String SUB_RUBROS_FRAGMENT_TAG = "SubRubrosFragment";
    public static final String SEARCH_RESULT_FRAGMENT_TAG = "SearchResultFragment";

    /**
     * Firebase database
     */
    public static final String FIREBASE_RUBRO_CONTAINER_NAME = "RubrosGenerales";
    public static final String FIREBASE_USERS_CONTAINER_NAME = "users";
    public static final String FIREBASE_QUALITY_CONTAINER_NAME = "userQuality";

    /**
     * Constants
     */
    public static final int MAX_CARACTERES = 210;
    public static final int MIN_DISTANCE = 50 * 1000;
    public static final int MIN_CALIDAD_INSIGNIA = 50;
    public static final int MIN_TIEMPO_RESP_INSIGNIA = 50;
    public static final int MIN_ATENCION_INSIGNIA = 50;
    public static final String SHARED_PREF_NAME = "SharedPref";


}
