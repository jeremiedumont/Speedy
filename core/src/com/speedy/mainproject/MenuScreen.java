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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by test on 4/3/2017.
 */
public class MenuScreen implements Screen, InputProcessor {
    Stage stage, stage2, stageLevelUp, stageNotConnected;
    InputMultiplexer inputmult;//Permet de gerer plusieurs input a la fois
    SpriteBatch batch;
    Image background;
    TextButton buttonPlay;
    TextButton buttonStore;
    TextButton buttonTop;
    TextButton buttonLevel;
    Sprite quitSpriteUp, quitSpriteDown;
    ImageButton quitAppButton;
    ArrayList liLetter;
    Label appTitle, labLevelUp;
    Skin skin;
    Random rd;
    long timeAct, timePrec, timeLevelUp;
    boolean stopLetter, firstCall;
    FileHandle file;
    Label levelLab;
    Label scoreLab;
    NotConnectedWindow wdConnected;
    OfflineWindow offwindow;
    int bestScore;
    boolean ongoingNotConnected;

    public MenuScreen(MyGdxGame gameP, boolean firstCallP)
    {
        ongoingNotConnected = false;
        GlobalVariables.game=gameP;
        GlobalVariables.getLevelPlayer();

        firstCall=firstCallP;
        stopLetter=false;
        liLetter=new ArrayList();
        rd = new Random();
        timePrec=System.currentTimeMillis();
        skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
        skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1050.0f);
        stage=new Stage();
        stage2=new Stage();
        inputmult = new InputMultiplexer();
        inputmult.addProcessor(this);
        inputmult.addProcessor(stage);
        inputmult.addProcessor(stage2);
        Gdx.input.setInputProcessor(inputmult);

        batch= new SpriteBatch();
        file = Gdx.files.local("data/levelstate.txt");
        if(file.exists()){
            String textFile = file.readString();
            String[] levelStates = textFile.split("-");
            if(levelStates[0].equals("3") || levelStates[1].equals("3"))
                background = new Image(new Texture("background3.jpg"));
            else if(levelStates[2].equals("3"))
                background = new Image(new Texture("background3green.jpg"));
            else if(levelStates[3].equals("3"))
                background = new Image(new Texture("background3red.jpg"));
        }
        else {
            background = new Image(new Texture("background3.jpg"));}
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        appTitle = new Label("SPEEDY", skin);
        levelLab = new Label("LEVEL "+GlobalVariables.levelPlayer, skin);
        levelLab.setFontScale(Gdx.graphics.getWidth()/800.0f);
        levelLab.setY(Gdx.graphics.getHeight()/100.0f);
        levelLab.getStyle().fontColor=Color.BLACK;

        //On recupere le meilleur score du joueur suivant la difficulte actuelle
        file = Gdx.files.local("data/scores.txt");//fichier contenant les scores
        if(file.exists()){
            FileHandle fileLevel = Gdx.files.local("data/levelstate.txt");
            if(fileLevel.exists()){
                String textFile = fileLevel.readString();
                String[] levelStates = textFile.split("-");
                if(levelStates[0].equals("3"))
                    bestScore=Integer.parseInt(file.readString().split("-")[0]);
                else if(levelStates[1].equals("3"))
                    bestScore=Integer.parseInt(file.readString().split("-")[1]);
                else if(levelStates[2].equals("3"))
                    bestScore=Integer.parseInt(file.readString().split("-")[2]);
                else if(levelStates[3].equals("3"))
                    bestScore=Integer.parseInt(file.readString().split("-")[3]);
            }else
                bestScore=Integer.parseInt(file.readString().split("-")[0]);
        }
        scoreLab = new Label("BEST SCORE: "+bestScore, skin);
        scoreLab.setFontScale(Gdx.graphics.getWidth()/900.0f);
        if(bestScore <10)
            scoreLab.setPosition(Gdx.graphics.getWidth()*0.58f,Gdx.graphics.getHeight()/100.0f);
        else if(bestScore > 9 && bestScore<100)
            scoreLab.setPosition(Gdx.graphics.getWidth()*0.56f,Gdx.graphics.getHeight()/100.0f);
        else
            scoreLab.setPosition(Gdx.graphics.getWidth()*0.54f,Gdx.graphics.getHeight()/100.0f);
        scoreLab.getStyle().fontColor=Color.BLACK;

        labLevelUp = new Label("NEW LEVEL !!", skin);
        labLevelUp.setFontScale(Gdx.graphics.getWidth()/330.0f);
        labLevelUp.setPosition(0,Gdx.graphics.getHeight()/2-labLevelUp.getHeight()/2);
        labLevelUp.getStyle().fontColor=Color.BLACK;
        stageLevelUp = new Stage();
        stageLevelUp.addActor(labLevelUp);

        appTitle.setFontScale(Gdx.graphics.getWidth()/295.0f);
        appTitle.setPosition(Gdx.graphics.getWidth()*0.21f, Gdx.graphics.getHeight()/40*37);
        appTitle.getStyle().fontColor=Color.WHITE;

        quitSpriteUp = new Sprite(new Texture("closeButton.png"));
        quitSpriteDown = new Sprite(new Texture("closeButtonDown.png"));
        quitSpriteUp.setSize(Gdx.graphics.getWidth()/11, Gdx.graphics.getHeight()/18);
        quitSpriteDown.setSize(Gdx.graphics.getWidth()/11, Gdx.graphics.getHeight()/18);
        quitAppButton = new ImageButton(new SpriteDrawable(quitSpriteUp), new SpriteDrawable(quitSpriteDown));
        quitAppButton.setSize(Gdx.graphics.getWidth()/11, Gdx.graphics.getHeight()/18);
        quitAppButton.setPosition(0, Gdx.graphics.getHeight()-quitAppButton.getHeight());
        quitAppButton.getImageCell().expand().fill();
        quitAppButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                })));
            }
        });

        stage2.addActor(background);
        stage2.addActor(appTitle);
        stage2.addActor(levelLab);
        stage2.addActor(scoreLab);
        stage2.addActor(quitAppButton);


        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
        stage2.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));

        if(GlobalVariables.levelUp){
            timeLevelUp = System.currentTimeMillis();
            stageLevelUp.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));
            GlobalVariables.levelUp=false;
        /*Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                stageLevelUp.addAction(Actions.fadeOut(0.5f));
            }
        }, 1f);*/
        }
    }

    @Override
    public void show() {

        buttonPlay = new TextButton("PLAY", skin);
        buttonStore = new TextButton("STORE", skin);
        buttonTop = new TextButton("TOP FRIENDS SCORES", skin);
        buttonLevel = new TextButton("SELECT LEVEL", skin);

        buttonTop.getLabel().setFontScale(Gdx.graphics.getWidth()/1000.0f);
        buttonPlay.getLabel().setFontScale(Gdx.graphics.getWidth()/1000.0f);
        buttonStore.getLabel().setFontScale(Gdx.graphics.getWidth()/1000.0f);
        buttonLevel.getLabel().setFontScale(Gdx.graphics.getWidth()/1000.0f);

        buttonStore.setColor(skin.getColor("selection"));
        buttonTop.setColor(skin.getColor("selection"));
        buttonLevel.setColor(skin.getColor("selection"));

        stage.addActor(buttonPlay);
        stage.addActor(buttonStore);
        stage.addActor(buttonTop);
        stage.addActor(buttonLevel);

        buttonPlay.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()*0.675f, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);
        buttonStore.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()*0.475f, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);
        buttonLevel.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()*0.275f, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);
        buttonTop.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()*0.075f, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);

        buttonPlay.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stopLetter=true;
                stage.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), 0, 0.6f, Interpolation.elasticIn));
                stage2.addAction(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if (!Gdx.files.local("data/scores.txt").exists()) {
                            GlobalVariables.game.setScreen(new WelcomingTutorial(GlobalVariables.typeTuto.playTuto));
                        } else {
                            file = Gdx.files.local("data/levelstate.txt");
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
                })));
            }
        });

        buttonStore.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stopLetter=true;
                stage.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), 0, 0.6f, Interpolation.elasticIn));
                stage2.addAction(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables.game.setScreen(new StoreClass());
                    }
                })));
            }
        });

        buttonLevel.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stopLetter=true;
                stage.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), 0, 0.6f, Interpolation.elasticIn));
                stage2.addAction(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables.game.setScreen(new LevelClass());
                    }
                })));
            }
        });

        buttonTop.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    if (GlobalVariables.fbLeaderboardInt.alreadySignedOn() && GlobalVariables.chartboostInterface.isOnline()) {
                        stopLetter = true;
                        stage.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), 0, 0.6f, Interpolation.elasticIn));
                        stage2.addAction(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                GlobalVariables.game.setScreen(new TopClass());
                            }
                        })));
                    } else {
                        stageNotConnected = new Stage();
                        if(GlobalVariables.chartboostInterface.isOnline()) {
                            wdConnected = new NotConnectedWindow("", skin, stageNotConnected);
                            stageNotConnected.addActor(wdConnected);
                        }else{
                            offwindow = new OfflineWindow("", skin, stageNotConnected);
                            stageNotConnected.addActor(offwindow);
                        }
                        Gdx.input.setInputProcessor(stageNotConnected);
                        ongoingNotConnected = true;
                        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                    }
            }
        });

        ManageMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timeAct=System.currentTimeMillis();
        if(timeAct-timePrec > 75) {
            addLetter();
            timePrec=timeAct;
        }

        stage2.getViewport().apply();
        stage2.act();
        stage2.draw();
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        if(!stopLetter) {
            batch.begin();
            manageLetter(batch);
            batch.end();
        }

        //On gère le rendu d'un passage au niveau superieur
        if(timeAct-timeLevelUp<3000){
            stageLevelUp.act();
            stageLevelUp.draw();
        }

        if(ongoingNotConnected){
            stageNotConnected.act();
            stageNotConnected.draw();
        }
    }

    public void ManageMusic()//Gestion de la musique
    {
        if(firstCall) {
            FileHandle fileMainSound = Gdx.files.local("data/musicStates.txt");
            String strMusic="sounds/AMBIANCE 1.mp3";
            if(fileMainSound.exists()) {
                for (int i = 0; i < fileMainSound.readString().split("-").length; i++)
                    if (fileMainSound.readString().split("-")[i].equals("3"))
                        strMusic = "sounds/AMBIANCE " + (i + 1) + ".mp3";
            }

            GlobalVariables.mainSound = Gdx.audio.newMusic(Gdx.files.internal(strMusic));
            GlobalVariables.mainSound.setLooping(true);
            GlobalVariables.mainSound.setVolume(0.5f);
            GlobalVariables.mainSound.play();
        }else{
            if(!GlobalVariables.mainSound.isPlaying())
                GlobalVariables.mainSound.setVolume(0.5f);
            GlobalVariables.mainSound.play();
        }
    }

    /*On ajoute une lettre qui tombe effet neige dans la collection qui contient deja toutes les autres*/
    public void addLetter()
    {
        Label lab = new Label(Character.toString((char)(rd.nextInt(9)+49)), skin);
        lab.setPosition(rd.nextInt((int) (Gdx.graphics.getWidth()-lab.getWidth())), Gdx.graphics.getHeight());
        lab.setFontScale(Gdx.graphics.getWidth()/1000.0f);
        lab.getStyle().fontColor=Color.WHITE;
        Group gr= new Group();
        gr.addActor(lab);
        gr.setOrigin(lab.getX()+lab.getWidth()/2, lab.getY()+lab.getHeight()/2);
        liLetter.add(gr);
    }

    /*On gère l'ensemble de la collection de lettres qui tombent effet neige*/
    public void manageLetter(SpriteBatch batch)
    {
        float rotation = Gdx.graphics.getDeltaTime()*400;
        for(int i = 0; i < liLetter.size(); i++) {
            if(((Group)liLetter.get(i)).getChildren().get(0).getY()<-((Group)liLetter.get(i)).getChildren().get(0).getHeight()*2)
                liLetter.remove(i);
            else {
                ((Group)liLetter.get(i)).setOrigin(((Group)liLetter.get(i)).getChildren().get(0).getX()+((Group)liLetter.get(i)).getChildren().get(0).getWidth()/2, ((Group)liLetter.get(i)).getChildren().get(0).getY()+((Group)liLetter.get(i)).getChildren().get(0).getHeight()/2);

                ((Group)liLetter.get(i)).getChildren().get(0).setY(((Group)liLetter.get(i)).getChildren().get(0).getY()-Gdx.graphics.getHeight()/250.0f);
                ((Group)liLetter.get(i)).draw(batch, 1);
            }
        }
    }

    public class OfflineWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stageR;
        TextButton but;
        Label lab;

        public OfflineWindow(String m_title, Skin m_skin, Stage m_stage) {
            super(m_title, m_skin);
            skin=m_skin;
            stageR = m_stage;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            but = new TextButton("BACK", skin);
            but.setSize(getWidth()*2/3, getHeight()/2);
            lab = new Label("You are actually offline", skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);

            but.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stageR.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingNotConnected=false;
                            Gdx.input.setInputProcessor(stage);
                        }
                    })));
                }
            });

            this.setBackground(new SpriteDrawable(back));
            add(lab).row();
            add(but);
        }
    }

    public class NotConnectedWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stageR;
        TextButton butRedirect, butOK;
        Label lab;

        public NotConnectedWindow(String m_title, Skin m_skin, Stage m_stage) {
            super(m_title, m_skin);
            skin=m_skin;
            stageR = m_stage;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            butRedirect = new TextButton("CONNECT", skin);
            butRedirect.setSize(getWidth()*1/3, getHeight()/2);
            butOK = new TextButton("NO THANKS", skin);
            butOK.setSize(getWidth()*1/3, getHeight()/2);
            lab = new Label("YOU NEED TO BE CONNECTED TO FACEBOOK", skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);
            lab.setFontScale(Gdx.graphics.getWidth()/1450f);
            lab.getStyle().fontColor=Color.WHITE;
            butRedirect.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stageR.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingNotConnected=false;
                            Gdx.input.setInputProcessor(stage);
                            GlobalVariables.fbLeaderboardInt.connect();
                        }
                    })));
                    stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeOut(0.7f)));
                }
            });
            butOK.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stageR.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingNotConnected=false;
                            Gdx.input.setInputProcessor(stage);
                        }
                    })));
                }
            });

            this.setBackground(new SpriteDrawable(back));
            add(lab).colspan(2).row();
            add(butRedirect).padLeft(-Gdx.graphics.getWidth()/30f).width(Value.percentWidth(0.5f, this));
            add(butOK).width(Value.percentWidth(0.5f, this));
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            Gdx.app.exit();
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }
}
