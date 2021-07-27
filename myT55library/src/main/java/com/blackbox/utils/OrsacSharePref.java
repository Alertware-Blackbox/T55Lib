package com.blackbox.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class OrsacSharePref {

    public static final String orsacpreference = "orsacpref";
    public static final String IMEI = "imeiKey";
    public static final String USERID = "user_id";
    public static final String USERNAME = "user_name";
    private static SharedPreferences sharedpreferences;

    
    public static void saveSharePref(Activity activity){
        sharedpreferences = activity.getSharedPreferences(orsacpreference,
                Context.MODE_PRIVATE);
    }

    public static void saveImeiNumber(String imei, Activity activity){
        saveSharePref(activity);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(IMEI, imei);
        editor.commit();
    }

    public static String getImeiNumber(Activity activity){
        saveSharePref(activity);
        return sharedpreferences.getString(IMEI, "");
    }


    //User Id
    public static void saveUserId(String imei, Activity activity){
        saveSharePref(activity);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERID, imei);
        editor.commit();
    }

    public static String getUserId(Activity activity){
        saveSharePref(activity);
        return sharedpreferences.getString(USERID, "");
    }

    //User Name
    public static void saveUserName(String imei, Activity activity){
        saveSharePref(activity);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERNAME, imei);
        editor.commit();
    }

    public static String getUserName(Activity activity){
        saveSharePref(activity);
        return sharedpreferences.getString(USERNAME, "");
    }

    public static void clearAllSharePref(Activity activity){
        saveSharePref(activity);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
