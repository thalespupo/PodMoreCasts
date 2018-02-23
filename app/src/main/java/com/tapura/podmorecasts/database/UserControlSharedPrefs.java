package com.tapura.podmorecasts.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class UserControlSharedPrefs {

    private static final String SHARED_PREF_KEY = "podmorecasts_pref";
    private static final String LOGGED_USER_KEY = "logged_user";

    @Nullable
    public static String getAlreadyLoggedUserId(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        if (sharedPref.contains(LOGGED_USER_KEY)) {
            return sharedPref.getString(LOGGED_USER_KEY, null);
        }
        return null;
    }

    public static void setUserId(Context context, String userId) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LOGGED_USER_KEY, userId);
        editor.apply();
    }

}
