package com.speedy.mainproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by test on 6/5/2017.
 */
public final class GlobalVariables {
    public static Music mainSound;
    public static com.speedy.mainproject.MyGdxGame game;
    public static int levelPlayer;
    public static boolean levelUp=false;
    public static int lastScore;
    public static int bestScore;
    public static int currentBonusPurchase;
    public static int actualLife;
    public static boolean alreadyWatchedAdDuringGame;
    public static MessengerShareInterface messShareInt;
    public static FacebookLeaderBoardInterface fbLeaderboardInt;
    public static CharboostInterface chartboostInterface;
    public static ArrayList<FriendID> listFriends = new ArrayList<FriendID>();
    public static boolean listFriendsArrayChanged = true;

    public static int getIndexOfFriendByID(String m_id){
        int i=0;
        while(!listFriends.get(i).getId().equals(m_id)) {
            i++;
        }
        return i;
    }


    public static void bonusPurchased(){
        FileHandle file;
        String[] bonusStates;
        String textFile;
        file = Gdx.files.local("data/bonusstate.txt");
        if(file.exists()){
            textFile = file.readString();
            bonusStates = textFile.split("-");
            if(currentBonusPurchase==0)
                file.writeString("1-"+bonusStates[1]+"-"+bonusStates[2], false);
            if(currentBonusPurchase==1)
                file.writeString(bonusStates[0]+"-1-"+bonusStates[2], false);
            if(currentBonusPurchase==2)
                file.writeString(bonusStates[0]+"-"+bonusStates[1]+"-1", false);
        }
        FileHandle fileD = Gdx.files.local("data/lastbonusbought.txt");
        fileD.writeString(TimeUtils.millis()+"", false);
    }

    public static void getLevelPlayer(){
        FileHandle file = Gdx.files.local("data/playerSpec.txt");
        if(file.exists()){
            levelPlayer=Integer.parseInt(file.readString().split("-")[0]);
        }else{
            file.writeString("1-0-0-0", false);
            levelPlayer=1;
        }
    }

    public enum typeTuto{
        playTuto,
        storeTuto;
    }

    public enum gameEnum{
        gameEasy,
        gameMedium,
        gameHard,
        gameIntense;
    }
}

