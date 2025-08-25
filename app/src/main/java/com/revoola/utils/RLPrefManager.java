package com.revoola.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class RLPrefManager {
    private static final String PREF_NAME = "base_pref";
    public static String current_fragment = "Currentfragment";

    public static String isGuestUser = "isGuestUser";

    public static String user_model_data = "usermodeldata";



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

    public static String selectionPeriod = "selectionPeriod";
    public static String selectionClassType = "selectionClassType";
    public static String selectionFromDate = "selectionFromDate";
    public static String selectionToDate = "selectionToDate";



    public static SharedPreferences rl_getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public static String rl_getSomeStringValue(Context context, String key, String defaultValue) {
        return rl_getSharedPreferences(context).getString(key, defaultValue);
    }

    public static void rl_setSomeStringValue(Context context, String key, String newValue) {
        final SharedPreferences.Editor editor = rl_getSharedPreferences(context).edit();
        editor.putString(key, newValue);
        editor.commit(); // Synchronous write - ensures immediate persistence
        editor.apply();
    }

    public static void rl_setSomeJsonObjectValue(Context context, String key, JsonObject newValue) {
        final SharedPreferences.Editor editor = rl_getSharedPreferences(context).edit();
        editor.putString(key, newValue.toString());
        editor.apply();
    }

    public static void rl_setSomeBooleanValue(Context context, String key, Boolean newValue) {
        final SharedPreferences.Editor editor = rl_getSharedPreferences(context).edit();
        editor.putBoolean(key, newValue);
        editor.apply();
    }

    public static Boolean rl_getGuestUser(Context context) {
        return rl_getSharedPreferences(context).getBoolean(isGuestUser,false);
    }

    public static void rl_clearIndividual(Context context, String key) {
        final SharedPreferences.Editor editor = rl_getSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static void rl_clear_all(Context context) {
        final SharedPreferences.Editor editor = rl_getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    // Method to store a MutableList<Integer> as a comma-separated string
    public static void rl_setSomeIntListValue(Context context, String key, List<Integer> newList) {
        final SharedPreferences.Editor editor = rl_getSharedPreferences(context).edit();

        // Convert the list to a comma-separated string
        String listAsString = TextUtils.join(",", newList);

        // Save the string
        editor.putString(key, listAsString);
        editor.apply();
    }

    // Method to retrieve a MutableList<Integer> from SharedPreferences
    public static List<Integer> rl_getSomeIntListValue(Context context, String key) {
        // Get the string from SharedPreferences
        String listAsString = rl_getSharedPreferences(context).getString(key, "");

        // If the list is not empty, convert it to a List of Integers
        if (listAsString != null && !listAsString.isEmpty()) {
            String[] parts = listAsString.split(",");
            List<Integer> list = new ArrayList<>();
            for (String part : parts) {
                list.add(Integer.parseInt(part));  // Convert each part to an Integer and add it to the list
            }
            return list;
        } else {
            return new ArrayList<>();  // Return an empty list if no data is found
        }
    }

    // Method to store a MutableList<Integer> as a comma-separated string
    public static void rl_setSomeStringListValue(Context context, String key, List<String> newList) {
        final SharedPreferences.Editor editor = rl_getSharedPreferences(context).edit();

        // Convert the list to a comma-separated string
        String listAsString = TextUtils.join(",", newList);

        // Save the string
        editor.putString(key, listAsString);
        editor.apply();
    }

    // Method to retrieve a MutableList<Integer> from SharedPreferences
    public static List<String> rl_getSomeStringListValue(Context context, String key, List<String> defaultValue) {
        // Get the string from SharedPreferences
        String listAsString = rl_getSharedPreferences(context).getString(key, "");

        // If the list is not empty, convert it to a List of Strings
        if (listAsString != null && !listAsString.isEmpty()) {
            String[] parts = listAsString.split(",");
            List<String> list = new ArrayList<>();
            for (String part : parts) {
                list.add(part);  // Add each part to the list
            }
            return list;
        } else {
            return defaultValue;  // Return the provided default value if no data is found
        }
    }

}
