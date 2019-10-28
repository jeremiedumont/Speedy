package com.speedy.mainproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.util.Random;

/**
 * Created by test on 5/22/2018.
 */
public abstract class GameSuperClass {

    SpriteBatch batch;
    ShapeRenderer shape;
    ParticleEffect pe;//Utilise pour l'animation type feu d'artifce lors d'un succes
    Label lab1, labScore, labLife, labNewHighScore;
    Sprite imTop, imBottom;
    ImageButton backButton;
    Sprite backSpriteUp, backSpriteDown;
    BitmapFont font,font2, font3;
    InputMultiplexer inputmult;
    RateAppWindow rateWd;
    UnavailableWindow redWd;
    OfflineWindow offwd;
    int temp, score = 1, numW=8, numH=10, bestScore=0;
    int rc[][];
    Button bt[][];
    Bonus bonus;
    long timeAct, timePrec, charToRedTime;
    Stage stage, stageGameOver, stageRedirect, stageRateWd, stageNewHighScore;
    boolean charToRed=false, ongoingRedirect=false, ongoingNotConnected=false,alreadyReachedHighScore=false;
    Skin skin, skin2;
    float tScore=45000, timeRefresh=300;
    long timeNewHighScore=0;
    GameSounds sounds;
    Random rd;
    String charD1="A", charA1="D", strScore, strLife;

    public void init(GlobalVariables.gameEnum gameType){
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        stage = new Stage();
        GlobalVariables.actualLife=5;
        GlobalVariables.alreadyWatchedAdDuringGame=false;
        stageGameOver = new Stage();
        inputmult = new InputMultiplexer();
        skin = new Skin(Gdx.files.internal("skin_default/uiskin.json"));
        skin2 = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
        skin2.getFont("button").getData().setScale(Gdx.graphics.getWidth()/800.0f);
        if(gameType.equals(GlobalVariables.gameEnum.gameMedium)) {
            font = new BitmapFont(Gdx.files.internal("Font/FontRotate1/font1.fnt"), false);
            font.getData().setScale(Gdx.graphics.getWidth() / 250.0f);
            font.setColor(Color.BLACK);
        }else{
            font = new BitmapFont(Gdx.files.internal("Font/FontStatic1/font1.fnt"), false);
            font.getData().setScale(Gdx.graphics.getWidth() / 550.0f);
            font.setColor(Color.BLACK);
        }

        rd=new Random();

        lab1= new Label("8", new Label.LabelStyle(font, Color.BLACK));
        lab1.setOrigin(lab1.getWidth()/2, lab1.getHeight()/2);
        lab1.setPosition(Gdx.graphics.getWidth()/2-lab1.getWidth()/2, Gdx.graphics.getHeight()/2-lab1.getHeight()/2);

        labScore= new Label("SCORE : "+score, skin2);
        labScore.getStyle().fontColor=Color.WHITE;
        labScore.setPosition(Gdx.graphics.getWidth()/numW/3, Gdx.graphics.getHeight()/numH/4);

        labLife= new Label("LIFE : "+ GlobalVariables.actualLife, skin2);
        labLife.getStyle().fontColor=Color.WHITE;
        labLife.setPosition(Gdx.graphics.getWidth()/numW*6, Gdx.graphics.getHeight()/numH/4);

        backSpriteUp = new Sprite(new Texture("backImage.png"));
        backSpriteDown = new Sprite(new Texture("backImageDown.png"));
        backSpriteUp.setSize(Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/16);
        backSpriteDown.setSize(Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/16);
        backButton = new ImageButton(new SpriteDrawable(backSpriteUp), new SpriteDrawable(backSpriteDown));
        backButton.setSize(Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/16);
        backButton.setPosition(0, Gdx.graphics.getHeight()-backButton.getHeight());
        backButton.getImageCell().expand().fill();
        stage.addActor(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.sequence(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, false));
                    }
                })));
            }
        });

        imTop = new Sprite(new Texture("blockTexture2.jpg"));
        imBottom = new Sprite(new Texture("blockTexture2.jpg"));
        imTop.setBounds(0, Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/numH*2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/numH*2);
        imBottom.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/numH);
        rc = new int[numH][numW];
        timePrec=System.currentTimeMillis();

        bonus = new Bonus(stage);

        sounds = new GameSounds();

        manageStoreBonus();

        FileHandle file = Gdx.files.local("data/scores.txt");
        if(file.exists())
            bestScore = Integer.parseInt(file.readString().split("-")[gameType.ordinal()]);

        labNewHighScore = new Label("NEW HIGH SCORE", skin2);
        labNewHighScore.setFontScale(Gdx.graphics.getWidth()/440.0f);
        labNewHighScore.setPosition(Gdx.graphics.getWidth()/250.0f,Gdx.graphics.getHeight()/2-labNewHighScore.getHeight()/2);
        stageNewHighScore = new Stage();
        stageNewHighScore.addActor(labNewHighScore);
    }

    public void launchUnavailableWindow(){
        stageRedirect = new Stage();
        redWd = new UnavailableWindow("", skin2, stageRedirect);
        ongoingRedirect=true;
        stageRedirect.addActor(redWd);
        stageRedirect.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
    }

    public void launchOfflineWindow(){
        stageRedirect = new Stage();
        offwd = new OfflineWindow("", skin2, stageRedirect);
        ongoingRedirect=true;
        stageRedirect.addActor(offwd);
        stageRedirect.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
    }

    public void launchRateWindow(){

        Preferences prefs = Gdx.app.getPreferences("RatingPreferences");
        boolean alreadyRated = prefs.getBoolean("alreadyRated", false);
        int countOfGames = prefs.getInteger("countGames",0);
        if(countOfGames%5 == 0 && countOfGames >=5 && !alreadyRated) {
            stageRateWd = new Stage();
            rateWd = new RateAppWindow("", skin2, stageRateWd);
            stageRateWd.addActor(rateWd);
            Gdx.input.setInputProcessor(stageRateWd);
            ongoingNotConnected = true;
            stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
        }
        prefs.putInteger("countGames", countOfGames+1);
        prefs.flush();
    }

    public static class GameOverWindow extends Window
    {
        int score=0;
        Skin skin;
        Stage stage;
        TextButton tryAgainButton;
        ImageButton adButton;
        ImageButton shareButton;
        Label lab, result, best, nextLevel;
        Sprite back, shareSpriteUp, shareSpriteDown;
        FileHandle file;
        int bestScore=0, expToLevel=1000;
        GameSuperClass currentAct;
        String labTitle="GAME OVER";
        GlobalVariables.gameEnum gameType;

        public GameOverWindow(String title, Skin m_skin, int Score, Stage m_stage, GameSuperClass m_currentAct, GlobalVariables.gameEnum m_gameType) {
            super(title, m_skin);
            score=Score;
            skin=m_skin;
            stage = m_stage;
            currentAct = m_currentAct;
            gameType = m_gameType;

            file = Gdx.files.local("data/scores.txt");
            if(file.exists()){
                bestScore=Integer.parseInt(file.readString().split("-")[gameType.ordinal()]);
                if(bestScore<score) {
                    bestScore=score;
                    file.writeString(score+"-"+file.readString().split("-")[1]+"-"+file.readString().split("-")[2]+"-"+file.readString().split("-")[3], false);
                    if(gameType== GlobalVariables.gameEnum.gameEasy)
                        file.writeString(score+"-"+file.readString().split("-")[1]+"-"+file.readString().split("-")[2]+"-"+file.readString().split("-")[3], false);
                    else if(gameType== GlobalVariables.gameEnum.gameMedium)
                        file.writeString(file.readString().split("-")[0]+"-"+score+"-"+file.readString().split("-")[2]+"-"+file.readString().split("-")[3], false);
                    else if(gameType== GlobalVariables.gameEnum.gameHard)
                        file.writeString(file.readString().split("-")[0]+"-"+file.readString().split("-")[1]+"-"+score+"-"+file.readString().split("-")[3], false);
                    else
                        file.writeString(file.readString().split("-")[0]+"-"+file.readString().split("-")[1]+"-"+file.readString().split("-")[2]+"-"+score, false);
                    labTitle = "CONGRATULATION!!!!";
                    if(GlobalVariables.fbLeaderboardInt.alreadySignedOn())
                        GlobalVariables.fbLeaderboardInt.publishMyScore(bestScore);

                }
            }else{
                bestScore=score;
                if(gameType== GlobalVariables.gameEnum.gameEasy)
                    file.writeString(score + "-0-0-0", false);
                else if(gameType== GlobalVariables.gameEnum.gameMedium)
                    file.writeString("0-"+score + "-0-0", false);
                else if(gameType== GlobalVariables.gameEnum.gameHard)
                    file.writeString("0-0-"+score + "-0", false);
                else
                    file.writeString("0-0-0-"+score, false);
                labTitle = "CONGRATULATION!!!!";
            }
            GlobalVariables.lastScore=score;
            GlobalVariables.bestScore=bestScore;

            //setSize(Gdx.graphics.getWidth()*4/5, Gdx.graphics.getHeight()*2/5);
            //setPosition(Gdx.graphics.getWidth()/2-getWidth()/2, Gdx.graphics.getHeight()/2-getHeight()/2);
            setSize(Gdx.graphics.getWidth()*5/5, Gdx.graphics.getHeight()* 3/5);
            setPosition(Gdx.graphics.getWidth()/2-getWidth()/2, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));

            this.setBackground(new SpriteDrawable(back));
            expToLevel=CalculateExperience();

            tryAgainButton = new TextButton("TRY AGAIN", skin);

            Sprite adSpriteUp = new Sprite(new Texture("watchVideoLogo2Bis.png"));
            Sprite adSpriteDown = new Sprite(new Texture("watchVideoLogo2BisDown.png"));
            adSpriteUp.setSize(getWidth()*2/3, getHeight()/10);
            adSpriteDown.setSize(getWidth()*2/3, getHeight()/10);
            adButton = new ImageButton(new SpriteDrawable(adSpriteUp), new SpriteDrawable(adSpriteDown));
            adButton.setSize(getWidth()*2/3, getHeight()/10);
            adButton.getImageCell().expand().fill();

            shareSpriteUp = new Sprite(new Texture("shareImage4.png"));
            shareSpriteDown = new Sprite(new Texture("shareImage4Down.png"));
            shareSpriteUp.setSize(getWidth()/6, getHeight()/6);
            shareSpriteDown.setSize(getWidth()/6, getHeight()/6);
            shareButton = new ImageButton(new SpriteDrawable(shareSpriteUp), new SpriteDrawable(shareSpriteDown));
            shareButton.setSize(getWidth()/6, getHeight()/6);
            shareButton.setPosition(getX()+getWidth()-shareButton.getWidth(), getY());
            shareButton.getImageCell().expand().fill();
            shareButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(GlobalVariables.fbLeaderboardInt.alreadySignedOn() && GlobalVariables.chartboostInterface.isOnline()) {
                        GlobalVariables.messShareInt.shareToMessenger();
                    }else{
                        if(GlobalVariables.chartboostInterface.isOnline())
                            currentAct.launchUnavailableWindow();
                        else{
                            currentAct.launchOfflineWindow();
                        }

                    }

                }
            });

            lab = new Label("Your score is : " + score, skin);
            lab.setStyle(skin.get("big", Label.LabelStyle.class));
            lab.getStyle().fontColor= Color.BLACK;
            lab.setFontScale(getWidth()/850);

            result = new Label(labTitle, skin);
            result.setStyle(skin.get("big", Label.LabelStyle.class));
            result.getStyle().fontColor=Color.BLACK;
            result.setFontScale(getWidth()/650);

            best = new Label("BEST SCORE : " + bestScore, skin);
            best.setStyle(skin.get("big", Label.LabelStyle.class));
            best.getStyle().fontColor=Color.BLACK;
            best.setFontScale(getWidth()/850);

            tryAgainButton.setSize(getWidth()*2/3, getHeight()/10);
            tryAgainButton.getLabel().setStyle(skin.get("big", Label.LabelStyle.class));
            tryAgainButton.getLabel().setFontScale(getWidth()/1000);
            adButton.setSize(getWidth()*2/3, getHeight()/10);

            nextLevel = new Label("Next level in : " + expToLevel, skin);
            nextLevel.setStyle(skin.get("big", Label.LabelStyle.class));
            nextLevel.getStyle().fontColor=Color.BLACK;
            nextLevel.setFontScale(getWidth()/1200);

            setTouchable(Touchable.enabled);
            add(result).padBottom(result.getHeight()*3);
            add().row();
            add(lab).row();
            if(!GlobalVariables.alreadyWatchedAdDuringGame)
                add(adButton).width(Value.percentWidth(0.7f,this)).height(Value.percentHeight(0.15f, this)).row();
            add(tryAgainButton).width(Value.percentWidth(0.7f,this)).height(Value.percentHeight(0.15f, this)).row();
            add(best).row();
            add(nextLevel);

            adButton.setTouchable(Touchable.enabled);
            adButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(GlobalVariables.chartboostInterface.isOnline()) {
                        GlobalVariables.alreadyWatchedAdDuringGame = true;
                        GlobalVariables.actualLife = 3;
                        GlobalVariables.chartboostInterface.launchAds();
                    }
                    else{
                        currentAct.launchOfflineWindow();
                    }
                }
            });
            tryAgainButton.setTouchable(Touchable.enabled);
            tryAgainButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.sequence(Actions.moveTo(-Gdx.graphics.getWidth(), 0,0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            if(gameType== GlobalVariables.gameEnum.gameEasy)
                                GlobalVariables.game.setScreen(new GameEasy());
                            else if(gameType== GlobalVariables.gameEnum.gameMedium)
                                GlobalVariables.game.setScreen(new GameMedium());
                            else if(gameType== GlobalVariables.gameEnum.gameHard)
                                GlobalVariables.game.setScreen(new GameHard());
                            else
                                GlobalVariables.game.setScreen(new GameIntense());
                        }
                    })));
                }
            });

            stage.addActor(this);
            stage.addActor(shareButton);
            stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));
        }

        public int CalculateExperience(){
            int expLeft=0, expTotal=0, newCoins=0;
            for(int i = 0; i < GlobalVariables.levelPlayer+1; i++){
                expLeft+=i*40;
            }
            FileHandle file = Gdx.files.local("data/playerSpec.txt");
            if(file.exists()){
                expTotal=Integer.parseInt(file.readString().split("-")[1]);
                newCoins=Integer.parseInt(file.readString().split("-")[2]);
                expTotal+=score;
                newCoins+=score;
                expLeft-=expTotal;
                if(expLeft<=0){
                    GlobalVariables.levelUp=true;
                    GlobalVariables.levelPlayer++;
                    expLeft+=(GlobalVariables.levelPlayer)*40;
                }
                file.writeString(GlobalVariables.levelPlayer+"-"+expTotal+"-"+newCoins+"-"+file.readString().split("-")[3], false);
            }else{
                file.writeString("1-"+expTotal+"-"+score+"-0", false);
            }
            return expLeft;
        }

    }

    public class RateAppWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stage;
        TextButton butRate, butNo;
        Label lab;

        public RateAppWindow(String m_title, Skin m_skin, Stage m_stage) {
            super(m_title, m_skin);
            skin=m_skin;
            stage = m_stage;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            butRate = new TextButton("RATE", skin);
            butRate.setSize(getWidth()*1/3, getHeight()/2);
            butNo = new TextButton("NOT NOW", skin);
            butNo.setSize(getWidth()*1/3, getHeight()/2);
            lab = new Label("COULD YOU TAKE A MOMENT TO RATE THIS APP ?\n WE WOULD BE GRATEFULL OF THAT", skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);
            lab.setFontScale(Gdx.graphics.getWidth()/1300f);
            butRate.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingNotConnected=false;
                            Preferences prefs = Gdx.app.getPreferences("RatingPreferences");
                            prefs.putBoolean("alreadyRated", true);
                            prefs.flush();
                            Gdx.input.setInputProcessor(inputmult);
                            Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.speedy.mainproject");
                        }
                    })));
                }
            });
            butNo.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingNotConnected=false;
                            Gdx.input.setInputProcessor(inputmult);
                        }
                    })));
                }
            });

            this.setBackground(new SpriteDrawable(back));

            add(lab).colspan(2).row();
            add(butRate).width(Value.percentWidth(0.5f, this));
            add(butNo).width(Value.percentWidth(0.5f, this));
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
                            ongoingRedirect=false;
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
                            ongoingRedirect=false;
                            Gdx.input.setInputProcessor(stageGameOver);
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
                            ongoingRedirect=false;
                            Gdx.input.setInputProcessor(stageGameOver);
                        }
                    })));
                }
            });

            this.setBackground(new SpriteDrawable(back));
            add(lab).row();
            add(but);
        }
    }

    public class Bonus{
        ImageButton plus2, snailBut, fois2, secChance;
        Sprite p2Sprite, x2Sprite, secondSprite, snailSprite;
        int p2Stock;
        int x2Stock, x2Left;
        int secondStock;
        int snailStock;
        boolean p2Used, x2Used, secondUsed, snailUsed;
        boolean x2Activated, secondActivated;


        public Bonus(Stage stage)
        {
            p2Stock=0;
            x2Stock=0;
            secondStock=0;
            snailStock=0;
            p2Used=false;
            x2Used=false;
            snailUsed=false;
            secondUsed=false;
            x2Activated=false;
            secondActivated=false;
            x2Left=0;

            loadingStock();

            if(p2Stock>0)
                p2Sprite = new Sprite(new Texture("bonus/plus2.png"));
            else
                p2Sprite = new Sprite(new Texture("bonus/plus2Bis.png"));
            p2Sprite.setSize(Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            plus2 = new ImageButton(new SpriteDrawable(p2Sprite));
            plus2.setBounds(0, Gdx.graphics.getHeight()/numH, Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            plus2.getImageCell().expand().fill();
            if(snailStock>0)
                snailSprite = new Sprite(new Texture("bonus/snailBut.png"));
            else
                snailSprite = new Sprite(new Texture("bonus/snailButBis.png"));
            snailSprite.setSize(Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            snailBut = new ImageButton(new SpriteDrawable(snailSprite));
            snailBut.setBounds(Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH, Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            snailBut.getImageCell().expand().fill();
            if(x2Stock>0)
                x2Sprite = new Sprite(new Texture("bonus/fois2.png"));
            else
                x2Sprite = new Sprite(new Texture("bonus/fois2Bis.png"));
            x2Sprite.setSize(Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            fois2 = new ImageButton(new SpriteDrawable(x2Sprite));
            fois2.setBounds(Gdx.graphics.getWidth()/numW*4, Gdx.graphics.getHeight()/numH, Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            fois2.getImageCell().expand().fill();
            if(secondStock>0)
                secondSprite = new Sprite(new Texture("bonus/2nd.png"));
            else
                secondSprite = new Sprite(new Texture("bonus/2ndBis.png"));
            secondSprite.setSize(Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            secChance = new ImageButton(new SpriteDrawable(secondSprite));
            secChance.setBounds(Gdx.graphics.getWidth()/numW*6, Gdx.graphics.getHeight()/numH, Gdx.graphics.getWidth()/numW*2, Gdx.graphics.getHeight()/numH);
            secChance.getImageCell().expand().fill();

            initListeners(stage);
        }

        public void draw(Batch batch)
        {
            plus2.draw(batch, 1f);
            snailBut.draw(batch, 1f);
            fois2.draw(batch, 1f);
            secChance.draw(batch, 1f);
        }

        public void loadingStock(){
            FileHandle file = Gdx.files.local("data/assetsStock.txt");
            if(file.exists()){
                p2Stock=Integer.parseInt(file.readString().split("-")[0]);
                snailStock=Integer.parseInt(file.readString().split("-")[1]);
                x2Stock=Integer.parseInt(file.readString().split("-")[2]);
                secondStock=Integer.parseInt(file.readString().split("-")[3]);

            }else{
                file.writeString("2-2-2-2", false);
                p2Stock=2;
                snailStock=2;
                x2Stock=2;
                secondStock=2;
            }
        }

        public void updateStock(){
            FileHandle file = Gdx.files.local("data/assetsStock.txt");
            file.writeString(p2Stock+"-"+snailStock+"-"+x2Stock+"-"+secondStock, false);
        }

        public void initListeners(Stage stage)
        {

            plus2.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    if(!p2Used && p2Stock>0) {
                        GlobalVariables.actualLife+=2;
                        p2Sprite.setTexture(new Texture("bonus/plus2Bis.png"));
                        p2Stock--;
                        p2Used=true;
                        updateStock();
                    }
                }
            });
            fois2.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!x2Used && x2Stock>0) {
                        x2Sprite.setTexture(new Texture("bonus/fois2Bis.png"));
                        x2Stock--;
                        x2Used=true;
                        x2Activated=true;
                        x2Left=3;
                        updateStock();
                    }
                }
            });
            snailBut.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!snailUsed && snailStock>0) {
                        if (score < 10)
                            tScore += 10000;
                        else if(score>=10 &&score<15)
                            tScore += ((score-10)*500+(5-score+10)*2000);
                        else
                            tScore += 2500;
                        snailSprite.setTexture(new Texture("bonus/snailButBis.png"));
                        snailStock--;
                        snailUsed = true;
                        updateStock();
                    }
                }
            });
            secChance.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!secondUsed && secondStock>0) {
                        secondSprite.setTexture(new Texture("bonus/2ndBis.png"));
                        secondStock--;
                        secondUsed = true;
                        secondActivated=true;
                        updateStock();
                    }
                }
            });
            stage.addActor(plus2);
            stage.addActor(fois2);
            stage.addActor(secChance);
            stage.addActor(snailBut);
        }
    }

    public class GameSounds
    {
        public Sound soundSuccess;
        public Sound soundFail;
        public Sound soundOver;
        public Sound soundWin;


        public GameSounds()
        {
            soundSuccess = Gdx.audio.newSound(Gdx.files.internal("sounds/success1.mp3"));
            soundFail = Gdx.audio.newSound(Gdx.files.internal("sounds/error3.wav"));
            soundOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver1.wav"));
            soundWin = Gdx.audio.newSound(Gdx.files.internal("sounds/success1.mp3"));
        }
    }

    public void manageStoreBonus(){
        FileHandle fileBonus;
        String[] bonusStates;
        String textFile;
        fileBonus = Gdx.files.local("data/bonusstate.txt");
        if(fileBonus.exists()){
            textFile = fileBonus.readString();
            bonusStates = textFile.split("-");
            if(bonusStates[0].equals("1"))
                GlobalVariables.actualLife++;
            if(bonusStates[1].equals("1"))
                score+=10;
        }
    }
}
