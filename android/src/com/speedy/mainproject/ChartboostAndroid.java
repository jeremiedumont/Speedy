package com.speedy.mainproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;

/**
 * Created by test on 5/22/2018.
 */
public class ChartboostAndroid implements CharboostInterface{

    public ChartboostAndroid(){

    }

    public void launchAds(){
        //Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
        Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)GlobalAndroid.act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
