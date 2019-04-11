package com.example.bookmybook.data;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorageSharedPreferences {
    private static final String SHARED_PREFERENCES_NAME="bookMyBook";
    private static final String USER_ID_KEY="userID";
    public static void addUserToSharedPreferences(Context context, String uid){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(USER_ID_KEY,uid);
        editor.commit();
    }
    public static String getLoggedUser(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID_KEY,null);
    }
    public static void removeLoggedUser(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.remove(USER_ID_KEY);
        editor.commit();
    }
}
