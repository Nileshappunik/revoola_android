package com.revoola.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;

import java.util.Set;

public class RLPrefManager {
    private static final String PREF_NAME = "base_pref";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String APP_LANGUAGE = "appLanguage";
    public static String current_fragment = "Currentfragment";

    public static String user_model_data = "usermodeldata";

    public static String selected_schedule_date = "selectedscheduledate";

    public static String last_device_connect = "LastDeviceConnect";
    public static String last_device_connect_type = "LastDeviceConnectType";

    public static String login_email = "loginEmail";
    public static String login_password = "loginPassword";

    public static String change_device_name = "ChangeDeviceName";
    public static String last_device_connect_name = "LastDeviceConnectName";
    public static String current_user = "current_user";
    public static String current_user_email = "current_user_email";
    public static String start_help_content = "startHelpContent";
    public static String friends_help_content = "friendsHelpContent";
    public static String challenge_selectFor = "challengeSelectFor";

    public static String challenge_selectTarget = "challengeSelectTarget";
    public static String challenge_selectName = "challengeSelectName";


    private static boolean isFirst = true;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // shared pref mode
    private int PRIVATE_MODE = 0;

    public RLPrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SharedPreferences RLgetSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void RLsetFirebaseToken(String token, Context ctx) {
        SharedPreferences firebase_token = ctx.getApplicationContext().getSharedPreferences("firebase_token", 0);
        SharedPreferences.Editor token_editor = firebase_token.edit();
        //put the whole value to the shared prefances;
        token_editor.putString("fire_base_token", token);
        token_editor.apply();
    }

    public static String RLgetFirebaseToken(Context ctx) {
        SharedPreferences firebase_token = ctx.getSharedPreferences("firebase_token", 0);
        String token = firebase_token.getString("fire_base_token", "null");
        return token;
    }

    public static String RLgetSomeStringValue(Context context, String key, String defaultValue) {
        return RLgetSharedPreferences(context).getString(key, defaultValue);
    }

    public static void RLsetSomeStringValue(Context context, String key, String newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.putString(key, newValue);
        editor.apply();
    }
    public static void RLsetSomeJsonObjectValue(Context context, String key, JsonObject newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.putString(key, newValue.toString());
        editor.apply();
    }

    public static String RLgetSomeJsonObjectValue(Context context, String key, String defaultValue) {
        return RLgetSharedPreferences(context).getString(key, defaultValue);
    }

    public static int RLgetSomeIntValue(Context context, String key, int defaultValue) {
        return RLgetSharedPreferences(context).getInt(key, defaultValue);
    }

    public static void RLsetSomeIntValue(Context context, String key, int newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.putInt(key, newValue);
        editor.apply();
    }

    public static Boolean RLgetSomeBooleanValue(Context context, String key) {
        return RLgetSharedPreferences(context).getBoolean(key, false);
    }

    public static void RLsetSomeBooleanValue(Context context, String key, Boolean newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.putBoolean(key, newValue);
        editor.apply();
    }

    public static void RLsetSomeArrayValue(Context contexts, String key, Set<String> newValue) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(contexts).edit();
        editor.putStringSet(key, newValue);
        editor.apply();
    }

    public static void RLclearIndividual(Context context, String key) {
        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static void RLclear_all(Context context) {

        final SharedPreferences.Editor editor = RLgetSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    public boolean RLisFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void RLsetFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public String RLgetLanguage() {
        return pref.getString(APP_LANGUAGE, "");
    }

    public void RLsetLanguage(String language) {
        editor.putString(APP_LANGUAGE, language);
        editor.commit();
    }

}
