package com.revoola.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;

import java.util.Set;

public class RLPrefManager {
    private static final String PREF_NAME = "base_pref";
    public static String current_fragment = "Currentfragment";

    public static String isGuestUser = "isGuestUser";

    public static String user_model_data = "usermodeldata";

    public static String selected_schedule_date = "selectedscheduledate";

    public static String last_device_connect = "LastDeviceConnect";
    public static String last_device_connect_type = "LastDeviceConnectType";

    public static String login_email = "loginEmail";
    public static String login_password = "loginPassword";

    public static String change_device_name = "ChangeDeviceName";

    public static String current_user = "current_user";
    public static String current_user_email = "current_user_email";

    public static String start_help_content = "startHelpContent";
    public static String friends_help_content = "friendsHelpContent";
    public static String challenge_selectFor = "challengeSelectFor";

    public static String challenge_selectTarget = "challengeSelectTarget";
    public static String challenge_selectName = "challengeSelectName";



    public static SharedPreferences RLgetSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public static String RLGetSomeStringValue(Context context, String key, String defaultValue) {
        return RLgetSharedPreferences(context).getString(key, defaultValue);
    }

    public static void RLSetSomeStringValue(Context context, String key, String newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.putString(key, newValue);
        editor.apply();
    }
    public static void RLSetSomeJsonObjectValue(Context context, String key, JsonObject newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.putString(key, newValue.toString());
        editor.apply();
    }

    public static void RLSetSomeBooleanValue(Context context, String key, Boolean newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.putBoolean(key, newValue);
        editor.apply();
    }

    public static Boolean RLGetGuestUser(Context context) {
        return RLgetSharedPreferences(context).getBoolean(isGuestUser,false);
    }

    public static void RLClearIndividual(Context context, String key) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static void RLClear_all(Context context) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

}
