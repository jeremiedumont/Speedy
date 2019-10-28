package com.speedy.mainproject;

/**
 * Created by test on 11/20/2017.
 */
public interface FacebookLeaderBoardInterface {
    public void getFriendsList();
    public void publishMyScore(int best);
    public void publishOnFacebook();
    public boolean alreadySignedOn();
    public void connect();
}
