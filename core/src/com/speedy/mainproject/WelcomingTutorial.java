package com.speedy.mainproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by test on 3/24/2018.
 */
public class WelcomingTutorial implements Screen{
    Stage stage;
    ImageButton background;
    Sprite sprite;
    int count = 0;
    GlobalVariables.typeTuto actTargeted;

    public WelcomingTutorial(GlobalVariables.typeTuto type){
        actTargeted = type;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        if(actTargeted == GlobalVariables.typeTuto.playTuto) {
            sprite = new Sprite(new Texture("tuto/TUTO1.jpg"));
            sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            background = new ImageButton(new SpriteDrawable(sprite));
            background.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            background.getImageCell().expand().fill();
            stage.addActor(background);

            background.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    count++;
                    if(actTargeted == GlobalVariables.typeTuto.playTuto) {
                        if(count==1) {
                            sprite.setTexture(new Texture("tuto/TUTO4.jpg"));
                            sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            background.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            background.getImageCell().expand().fill();
                        }
                        else if(count==2) {
                            sprite.setTexture(new Texture("tuto/TUTO5.jpg"));
                            sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            background.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            background.getImageCell().expand().fill();
                        }
                        else if(count==3){
                                    FileHandle file = Gdx.files.local("data/levelstate.txt");
                                    if (file.exists()) {
                                        String textFile = file.readString();
                                        String[] levelStates = textFile.split("-");
                                        if (levelStates[0].equals("3"))
                                            GlobalVariables.game.setScreen(new GameEasy());
                                        else if (levelStates[1].equals("3"))
                                            GlobalVariables.game.setScreen(new GameMedium());
                                        else if (levelStates[2].equals("3"))
                                            GlobalVariables.game.setScreen(new GameHard());
                                        else if (levelStates[3].equals("3"))
                                            GlobalVariables.game.setScreen(new GameIntense());
                                    } else {
                                        GlobalVariables.game.setScreen(new GameEasy());
                                    }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void render(float delta) {

        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
