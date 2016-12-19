package com.example.savi.auth.constant;


public class Constants {
    public final static int LIST_ITEMS  = 0 ;
    public final static int EDIT_TEXT  = LIST_ITEMS +  1  ;
    public final static int IMAGE_PICK  = 2 ;
    public final static int IMAGE_CROP  = 3 ;
    public final static int INITIAL_LIMIT = 10;

    public final static int SUCCESS_IMAGE_UPLOAD  = 5 ;
    public final static int FAIL_IMAGE_UPLOAD  = 1 ;
    public final static int SUCCESS_USER_VALUE_SET = 3 ;

    public final static String TODOCLOUD_FIREBASE_STORAGE_URL = "gs://todocloudsavi.appspot.com/" ;
    public final static String TODOCLOUD_ROOT_FIREBASE_URL = "https://todocloudsavi.firebaseio.com/";
    public final static String MESSAGE_CENTER = "message_center" ;
    public final static String USER_DETAIL = "user_details";
    public final static String CONTACTED_PERSON_MAP ="contactedPersonsMap";
    public final static String CIRCLE ="circle";
    public final static String UID = "uid";

    //Listener Type
    public final static int SINGLE_VALUE_EVENT_LISTENER = -111 ;
    public final static int VALUE_EVENT_LISTENER = -222;

    public final static String[] viewPagerFragmentTitle= {"All Users","Messages","Friends"};

}
