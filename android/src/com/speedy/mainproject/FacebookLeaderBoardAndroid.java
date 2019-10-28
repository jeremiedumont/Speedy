package com.speedy.mainproject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLeaderBoardAndroid implements com.speedy.mainproject.FacebookLeaderBoardInterface {
    Activity context;
    CallbackManager callbackManager;
    DatabaseReference mDatabase;

    public FacebookLeaderBoardAndroid(Activity cont, CallbackManager call){
        context=cont;
        callbackManager=call;
        FirebaseApp.initializeApp(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void publishMyScore(int best) {
        Log.d("bbb", GlobalAndroid.getToken());
        if (alreadySignedOn()) {
            mDatabase.child("users").child(AccessToken.getCurrentAccessToken().getUserId()).child("score").setValue(best);
        }
        else{
            Log.d("bbb", "Not log on");
        }
    }

    public boolean alreadySignedOn(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GlobalAndroid.act);
        return prefs.getBoolean("fb_identified_user", false);
    }

    //Methode pour se connecter à facebook
    public void connect(){
        LoginButton but = new LoginButton(context);
        but.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));//On definit les permissions que l'on souhaite avoir
        but.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("bbbb", "Login successed");
                Log.d("bbbb", "Token :" + loginResult.getAccessToken().getToken());
                GlobalAndroid.saveToken(loginResult.getAccessToken());
                GlobalAndroid.setIdentificationState(true);
            }

            @Override
            public void onCancel() {
                Log.d("bbbb", "Login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("bbbb", "Login error : " + error.getMessage());
            }
        });

        but.performClick();//On declenche manuellement le clic

    }

    public void getFriendsList() {
        Log.d("bbb", GlobalAndroid.getToken());
        if (alreadySignedOn()) {
            LoginManager.getInstance().logInWithReadPermissions(GlobalAndroid.act, Arrays.asList("pages_show_list"));
            final GraphRequestAsyncTask request = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            Log.d("bbb GET", response.toString());
                            publishMyScore(32);

                            try {
                                Log.d("bbb GET", "length array = "+response.getJSONObject().getJSONArray("data").length());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray data = response.getJSONObject().getJSONArray("data");

                                for(int i =0;i<data.length();i++){

                                    JSONObject oneUser = data.getJSONObject(i);

                                    //Puis tu récup les infos..

                                    String name = oneUser.getString("name");
                                    //int score = oneUser.getInt("score");
                                    String id = oneUser.getString("id");
                                    FriendID fr = new FriendID(name, id, "0");
                                    GlobalVariables.listFriends.add(fr);
                                    String score ="";
                                    mDatabase.child("users").child(id).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String score;
                                            if(dataSnapshot.getValue()!=null)
                                                score = dataSnapshot.getValue().toString();
                                            else
                                                score = "0";
                                            int i = GlobalVariables.getIndexOfFriendByID(dataSnapshot.getRef().getParent().getKey());
                                            GlobalVariables.listFriends.get(i).setScore(score);
                                            GlobalVariables.listFriendsArrayChanged=true;
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // ...
                                        }
                                    });


                                    Log.d("bbb USER : ", name + " (id:" + id + ") score : " + score);
                                    //GlobalVariables.friendsScores.add(score);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("bbb", "erreur Exception : " + e.getMessage());
                            }

                        }
                    }
            ).executeAsync();
        }
    }

    public void publishOnFacebook(){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.facebook.com/SpeedyMobileGame/"))
                .build();
        GlobalAndroid.shareDialog.show(context, content);
    }
}
