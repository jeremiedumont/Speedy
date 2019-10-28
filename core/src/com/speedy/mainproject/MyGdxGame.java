package com.speedy.mainproject;

import com.badlogic.gdx.Game;

public class MyGdxGame extends Game{

	public MyGdxGame(com.speedy.mainproject.MessengerShareInterface messShare, FacebookLeaderBoardInterface fbLB, CharboostInterface chart){
		//On initialise les variables globales passees en parametre depuis AndroidLauncher
		GlobalVariables.messShareInt = messShare;
		GlobalVariables.fbLeaderboardInt = fbLB;
		GlobalVariables.chartboostInterface = chart;
	}

	public void create () {
		setScreen( new MenuScreen(this, true));//On lance l'activite MenuScreen
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
}
