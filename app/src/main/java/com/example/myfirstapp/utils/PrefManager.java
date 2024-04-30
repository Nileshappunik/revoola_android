package com.example.myfirstapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class PrefManager {
    private static final String PREF_NAME = "base_pref";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String APP_LANGUAGE = "appLanguage";
    public static String current_fragment = "Currentfragment";


    private static boolean isFirst = true;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // shared pref mode
    private int PRIVATE_MODE = 0;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setFirebaseToken(String token, Context ctx) {
        SharedPreferences firebase_token = ctx.getApplicationContext().getSharedPreferences("firebase_token", 0);
        SharedPreferences.Editor token_editor = firebase_token.edit();
        //put the whole value to the shared prefances;
        token_editor.putString("fire_base_token", token);
        token_editor.apply();
    }

    public static String getFirebaseToken(Context ctx) {
        SharedPreferences firebase_token = ctx.getSharedPreferences("firebase_token", 0);
        String token = firebase_token.getString("fire_base_token", "null");
        return token;
    }

    public static String getSomeStringValue(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public static void setSomeStringValue(Context context, String key, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, newValue);
        editor.apply();
    }

    public static int getSomeIntValue(Context context, String key, int defaultValue) {
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    public static void setSomeIntValue(Context context, String key, int newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, newValue);
        editor.apply();
    }

    public static Boolean getSomeBooleanValue(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public static void setSomeBooleanValue(Context context, String key, Boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, newValue);
        editor.apply();
    }

    public static void setSomeArrayValue(Context contexts, String key, Set<String> newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(contexts).edit();
        editor.putStringSet(key, newValue);
        editor.apply();
    }

    public static void clearIndividual(Context context, String key) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clear_all(Context context) {

        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public String getLanguage() {
        return pref.getString(APP_LANGUAGE, "");
    }

    public void setLanguage(String language) {
        editor.putString(APP_LANGUAGE, language);
        editor.commit();
    }

}
