package com.speedy.mainproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.facebook.AccessToken;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by test on 11/20/2017.
 */
public final class GlobalAndroid {
    public static AccessToken myToken;
    public static Activity act;
    public static ShareDialog shareDialog;

    public static void saveToken(AccessToken token){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_access_token", token.getToken());
        editor.apply(); // This line is IMPORTANT !!!
    }
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void setIdentificationState(boolean state){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("fb_identified_user", state);
        editor.apply(); // This line is IMPORTANT !!!
    }

    public static String getToken(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        return prefs.getString("fb_access_token", null);
    }


}
