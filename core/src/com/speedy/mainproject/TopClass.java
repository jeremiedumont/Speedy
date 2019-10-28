package com.speedy.mainproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by test on 7/29/2017.
 */
public class TopClass implements InputProcessor, Screen {
    Stage stage;
    Skin skin;
    Image background;
    InputMultiplexer inputmult;
    ImageButton backButton;
    Sprite backSpriteUp, backSpriteDown;
    Table listFriends;
    Table listScores;
    Table listLeaderboard;
    ScrollPane scroll;

    public TopClass(){}

    @Override
    public void show() {
        stage = new Stage();

        inputmult = new InputMultiplexer();
        inputmult.addProcessor(this);
        inputmult.addProcessor(stage);
        Gdx.input.setInputProcessor(inputmult);

        skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
        skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1050.0f);

        background = new Image(new Texture("background3.jpg"));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        backSpriteUp = new Sprite(new Texture("backImage.png"));
        backSpriteDown = new Sprite(new Texture("backImageDown.png"));
        backSpriteUp.setSize(Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/16);
        backSpriteDown.setSize(Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/16);
        backButton = new ImageButton(new SpriteDrawable(backSpriteUp), new SpriteDrawable(backSpriteDown));
        backButton.setSize(Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/16);
        backButton.setPosition(0, Gdx.graphics.getHeight()-backButton.getHeight());
        backButton.getImageCell().expand().fill();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                    }
                })));
            }
        });

        GlobalVariables.listFriends.clear();


        FileHandle file = Gdx.files.local("data/scores.txt");
        int bestScore;
        if(file.exists()){
            bestScore=Integer.parseInt(file.readString().split("-")[0]);
        }else{
            bestScore=0;
        }

        if(GlobalVariables.fbLeaderboardInt!=null){
            GlobalVariables.fbLeaderboardInt.publishMyScore(bestScore);
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    GlobalVariables.fbLeaderboardInt.getFriendsList();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            listLeaderboard = new Table(skin);
                            listFriends = new Table(skin);
                            listScores = new Table(skin);

                            listLeaderboard.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            listFriends.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            listScores.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            listLeaderboard.setPosition(0,0);
                            listFriends.setPosition(0,0);
                            listScores.setPosition(0,0);

                            listLeaderboard.add(listFriends).width(Value.percentWidth(0.5f));
                            listLeaderboard.add(listScores).width(Value.percentWidth(0.5f));
                            scroll = new ScrollPane(listLeaderboard);
                            scroll.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                            scroll.setPosition(0,0);

                            stage.addActor(background);
                            stage.addActor(scroll);
                            stage.addActor(backButton);
                            stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));
                        }
                    }, 1f);
                }
            }, 0.5f);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(GlobalVariables.listFriendsArrayChanged && listScores != null){
            listScores.clear();
            for(int i = 0; i < GlobalVariables.listFriends.size(); i++)
            {
                Label txt = new Label(GlobalVariables.listFriends.get(i).getName(), skin);
                txt.getStyle().fontColor= Color.WHITE;
                listFriends.add(txt).padBottom(Value.percentHeight(0.2f));
                listFriends.row();
                txt = new Label(GlobalVariables.listFriends.get(i).getScore(), skin);
                listScores.add(txt).padBottom(Value.percentHeight(0.2f));
                listScores.row();
            }
            GlobalVariables.listFriendsArrayChanged=false;
        }

        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            stage.addAction(Actions.sequence(Actions.run(new Runnable() {
                @Override
                public void run() {
                    GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                }
            })));
        }
        return false;}
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

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

