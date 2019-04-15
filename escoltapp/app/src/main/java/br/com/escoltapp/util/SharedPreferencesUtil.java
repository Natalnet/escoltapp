package br.com.escoltapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static final String UNIQUE_ID = "LOCAL_STORAGE";

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

    public static void putLong(Context context, String key, Long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static Long getLong(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key,-1);
    }

    public static void putDouble(Context context, String key, Double value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.apply();
    }

    public static Double getDouble(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        return Double.longBitsToDouble(sharedPreferences.getLong(key,-1));
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_ID,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }


}
