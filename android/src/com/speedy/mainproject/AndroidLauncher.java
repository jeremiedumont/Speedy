package com.speedy.mainproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Base64;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.chartboost.sdk.Chartboost;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.FirebaseApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AndroidLauncher extends AndroidApplication {
	CallbackManager callbackManager;
	FacebookLeaderBoardAndroid fb;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		Log.d("bbb", "POINT1");
		super.onCreate(savedInstanceState);
		//On recupere la KeyHash
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.speedtest.mainmenu",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}

		GlobalAndroid.act=this;
		//On prepare le lancement de MyGdxGame
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		FirebaseApp.initializeApp(this);

		Chartboost.startWithAppId(this, "5ac6805c133e960ab6b6f64c", "4a2196c0037aac7d9de19f4f54733d3ffb039442");
		Chartboost.onCreate(this);

		callbackManager = CallbackManager.Factory.create();
		fb = new FacebookLeaderBoardAndroid(this, callbackManager);

		initialize(new MyGdxGame(new MessengerShareAndroid(this), fb, new ChartboostAndroid()), config);
		GlobalAndroid.shareDialog = new ShareDialog(this);
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		Chartboost.onStart(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Chartboost.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		Chartboost.onPause(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		Chartboost.onStop(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Chartboost.onDestroy(this);
	}

	@Override
	public void onBackPressed() {
		// If an interstitial is on screen, close it.
		if (Chartboost.onBackPressed())
			return;
		else
			super.onBackPressed();
	}

}
