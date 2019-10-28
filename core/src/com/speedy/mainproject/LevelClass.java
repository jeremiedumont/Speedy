package com.speedy.mainproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by test on 5/31/2017.
 */
public class LevelClass  implements InputProcessor, Screen {
    Stage stage, stageRedirect;
    Skin skin;
    Image background;
    InputMultiplexer inputmult;
    TextButton button1;
    TextButton button2;
    TextButton button3;
    TextButton button4;
    ImageButton backButton;
    Sprite backSpriteUp, backSpriteDown;
    FileHandle file;
    ClassSounds sounds;
    String[] levelStates;
    String textFile;
    UnavailableWindow redWd;
    int currentLevel;
    boolean ongoingRedirect=false;

    public LevelClass()
    {}

    @Override
    public void show() {
        stage = new Stage();

        inputmult = new InputMultiplexer();
        inputmult.addProcessor(this);
        inputmult.addProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
        skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1050.0f);

        file = Gdx.files.local("data/levelstate.txt");
        if(file.exists()){
            String textFile = file.readString();
            String[] levelStates = textFile.split("-");
            if(levelStates[0].equals("3"))
                background = new Image(new Texture("background3.jpg"));
            else if(levelStates[1].equals("3"))
                background = new Image(new Texture("background3.jpg"));
            else if(levelStates[2].equals("3"))
                background = new Image(new Texture("background3green.jpg"));
            else if(levelStates[3].equals("3"))
                background = new Image(new Texture("background3red.jpg"));
        }else
            background = new Image(new Texture("background3.jpg"));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        button1 = new TextButton("EASY", skin);
        button1.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()/10*7, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);



        button2 = new TextButton("MEDIUM", skin);
        button2.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()/10*5, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);



        button3 = new TextButton("HARDCORE", skin);
        button3.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()/10*3, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);


        button4 = new TextButton("INTENSE", skin);
        button4.setBounds(Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()/10*1, Gdx.graphics.getWidth()/3*2, Gdx.graphics.getHeight()/10*2);

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
                        file.writeString(levelStates[0]+"-"+levelStates[1]+"-"+levelStates[2]+"-"+levelStates[3], false);
                        GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                    }
                })));
            }
        });

        stage.addActor(background);
        stage.addActor(button1);
        stage.addActor(button2);
        stage.addActor(button3);
        stage.addActor(button4);
        stage.addActor(backButton);
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));

        levelStates= new String[4];
        file = Gdx.files.local("data/levelstate.txt");
        if(file.exists()){
            textFile = file.readString();
            levelStates = textFile.split("-");
        }else{
                file.writeString("3-2-1-1", false);
                levelStates[0]="3";
                levelStates[1]="2";
                levelStates[2]="1";
                levelStates[3]="1";
            }
        ManageLevelState();
        ManageListeners();

        sounds = new ClassSounds();
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(ongoingRedirect)
            Gdx.input.setInputProcessor(stageRedirect);
        else
            Gdx.input.setInputProcessor(inputmult);

        stage.act();
        stage.draw();

        if(ongoingRedirect){
            stageRedirect.act();
            stageRedirect.draw();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            stage.addAction(Actions.sequence(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    file.writeString(levelStates[0]+"-"+levelStates[1]+"-"+levelStates[2]+"-"+levelStates[3], false);
                    GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                }
            }))));
        }
        return false;
    }

    public void ManageLevelState() {
        if(levelStates[0].equals("1"))
            button1.setColor(Color.BLACK);
        else if(levelStates[0].equals("2"))
            button1.setColor(skin.getColor("selection"));
        else if(levelStates[0].equals("3"))
            button1.setColor(Color.WHITE);

        if(levelStates[1].equals("1"))
            button2.setColor(Color.BLACK);
        else if(levelStates[1].equals("2"))
            button2.setColor(skin.getColor("selection"));
        else if(levelStates[1].equals("3"))
            button2.setColor(Color.WHITE);

        if(levelStates[2].equals("1"))
            button3.setColor(Color.BLACK);
        else if(levelStates[2].equals("2"))
            button3.setColor(skin.getColor("selection"));
        else if(levelStates[2].equals("3"))
            button3.setColor(Color.WHITE);

        if(levelStates[3].equals("1"))
            button4.setColor(Color.BLACK);
        else if(levelStates[3].equals("2"))
            button4.setColor(skin.getColor("selection"));
        else if(levelStates[3].equals("3"))
            button4.setColor(Color.WHITE);
    }

    public void ManageListeners(){
        button1.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!levelStates[0].equals("1")) {
                    levelStates[0] = "3";
                    if (levelStates[1].equals("3"))
                        levelStates[1] = "2";
                    if (levelStates[2].equals("3"))
                        levelStates[2] = "2";
                    if (levelStates[3].equals("3"))
                        levelStates[3] = "2";
                    ManageLevelState();
                    sounds.soundChange.play();
                    stage.addAction(Actions.sequence(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            file.writeString(levelStates[0]+"-"+levelStates[1]+"-"+levelStates[2]+"-"+levelStates[3], false);
                            GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                        }
                    }))));
                }else{
                    sounds.soundImpossible.play();
                }
            }
        });
        button2.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!levelStates[1].equals("1")) {
                    levelStates[1] = "3";
                    if (levelStates[0].equals("3"))
                        levelStates[0] = "2";
                    if (levelStates[2].equals("3"))
                        levelStates[2] = "2";
                    if (levelStates[3].equals("3"))
                        levelStates[3] = "2";
                    ManageLevelState();
                    sounds.soundChange.play();
                    stage.addAction(Actions.sequence(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            file.writeString(levelStates[0]+"-"+levelStates[1]+"-"+levelStates[2]+"-"+levelStates[3], false);
                            GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                        }
                    }))));
                }else{
                    sounds.soundImpossible.play();
                }
            }
        });
        button3.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!levelStates[2].equals("1")) {
                    levelStates[2] = "3";
                    if (levelStates[1].equals("3"))
                        levelStates[1] = "2";
                    if (levelStates[0].equals("3"))
                        levelStates[0] = "2";
                    if (levelStates[3].equals("3"))
                        levelStates[3] = "2";
                    ManageLevelState();
                    sounds.soundChange.play();
                    stage.addAction(Actions.sequence(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            file.writeString(levelStates[0]+"-"+levelStates[1]+"-"+levelStates[2]+"-"+levelStates[3], false);
                            GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                        }
                    }))));
                }else{
                    sounds.soundImpossible.play();
                    stageRedirect = new Stage();
                    redWd = new UnavailableWindow("", skin, stageRedirect);
                    ongoingRedirect=true;
                    stageRedirect.addActor(redWd);
                    stageRedirect.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                }
            }
        });
        button4.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!levelStates[3].equals("1")) {
                    levelStates[3] = "3";
                    if (levelStates[0].equals("3"))
                        levelStates[0] = "2";
                    if (levelStates[1].equals("3"))
                        levelStates[1] = "2";
                    if (levelStates[2].equals("3"))
                        levelStates[2] = "2";
                    ManageLevelState();
                    sounds.soundChange.play();
                    stage.addAction(Actions.sequence(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            file.writeString(levelStates[0]+"-"+levelStates[1]+"-"+levelStates[2]+"-"+levelStates[3], false);
                            GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                        }
                    }))));
                }else{
                    sounds.soundImpossible.play();
                    stageRedirect = new Stage();
                    redWd = new UnavailableWindow("", skin, stageRedirect);
                    ongoingRedirect=true;
                    stageRedirect.addActor(redWd);
                    stageRedirect.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                }
            }
        });
    }

    public class ClassSounds
    {
        public Sound soundChange;
        public Sound soundImpossible;

        public ClassSounds()
        {
            soundChange = Gdx.audio.newSound(Gdx.files.internal("sounds/success2.wav"));
            soundImpossible = Gdx.audio.newSound(Gdx.files.internal("sounds/error3.wav"));
        }
    }

    public class UnavailableWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stageR;
        TextButton butRedirect, butOK;
        Label lab;

        public UnavailableWindow(String m_title, Skin m_skin, Stage m_stage) {
            super(m_title, m_skin);
            skin=m_skin;
            stageR = m_stage;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            butRedirect = new TextButton("GO TO STORE", skin);
            butRedirect.setSize(getWidth()*1/3, getHeight()/2);
            butOK = new TextButton("OK", skin);
            butOK.setSize(getWidth()*1/3, getHeight()/2);
            lab = new Label("YOU DON'T HAVE ACCESS TO THAT MODE! BUY IT IN THE STORE!", skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);
            lab.setFontScale(Gdx.graphics.getWidth()/1650f);
            lab.getStyle().fontColor=Color.WHITE;
            butRedirect.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stageR.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingRedirect=false;
                            Gdx.input.setInputProcessor(inputmult);
                            GlobalVariables.game.setScreen(new StoreClass());
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
                            ongoingRedirect=false;
                            Gdx.input.setInputProcessor(inputmult);
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
