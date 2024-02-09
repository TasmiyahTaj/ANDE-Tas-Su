package com.example.studylink_studio_dit2b03;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    public static void saveDataToSharedPreferences(Context context, String key, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static String getDataFromSharedPreferences(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}
