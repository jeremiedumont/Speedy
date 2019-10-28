package com.speedy.mainproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Vector;

/**
 * Created by test on 6/2/2017.
 */
public class StoreClass implements InputProcessor, Screen {
    Stage stage, stageOngoingPurchase;
    OngletBottomTable bottomTable;
    OngletAssetsTable assetsTable;
    OngletMusicTable musicTable;
    OngletModeTable modeTable;
    OngletBonusTable bonusTable;
    InputMultiplexer inputMult;
    Image background;
    ShapeRenderer shape;
    int coinsStock;
    boolean mainMusicChanged=false, ongoingPurchase=false;
    LakeCoinsWindow wdCoins;
    ValidatePurchaseWindow wdConfirm;
    LockedModeWindow wdLockedMode;
    UnavailableBonusWindow wdBonus;
    ClassSounds sounds;
    ImageButton backButton;
    Sprite backSpriteUp, backSpriteDown;


    public StoreClass() {
    }

    @Override
    public void show() {
        stage = new Stage();

        shape = new ShapeRenderer();

        background = new Image(new Texture("blockTexture2.jpg"));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        coinsStock=getCoinsStock();

        sounds = new ClassSounds();

        bottomTable = new OngletBottomTable();
        assetsTable = new OngletAssetsTable();
        musicTable = new OngletMusicTable();
        modeTable = new OngletModeTable();
        bonusTable = new OngletBonusTable();

        stage.addActor(background);
        stage.addActor(bottomTable);
        stage.addActor(assetsTable);
        stage.addActor(musicTable);
        stage.addActor(modeTable);
        stage.addActor(bonusTable);

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
                stage.addAction(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i < musicTable.music.size(); i++)
                            if(musicTable.music.get(i).soundAmbiance!=null)
                                musicTable.music.get(i).soundAmbiance.dispose();
                        GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, mainMusicChanged));
                    }
                })));
            }
        });

        inputMult = new InputMultiplexer();
        inputMult.addProcessor(this);
        inputMult.addProcessor(stage);

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(!ongoingPurchase)
            Gdx.input.setInputProcessor(inputMult);
        else
            Gdx.input.setInputProcessor(stageOngoingPurchase);

        if (bottomTable.idButtonChecked == 1) {
            assetsTable.setVisible(true);
            musicTable.setVisible(false);
            modeTable.setVisible(false);
            bonusTable.setVisible(false);
        } else if (bottomTable.idButtonChecked == 2) {
            assetsTable.setVisible(false);
            musicTable.setVisible(true);
            modeTable.setVisible(false);
            bonusTable.setVisible(false);
        } else if (bottomTable.idButtonChecked == 3) {
            assetsTable.setVisible(false);
            musicTable.setVisible(false);
            modeTable.setVisible(true);
            bonusTable.setVisible(false);
        } else if (bottomTable.idButtonChecked == 4) {
            assetsTable.setVisible(false);
            musicTable.setVisible(false);
            modeTable.setVisible(false);
            bonusTable.setVisible(true);
        }

        stage.act();
        stage.draw();
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        shape.rectLine(0, Gdx.graphics.getHeight() / 40 * 7, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 40 * 7, Gdx.graphics.getHeight() / 60);
        shape.end();

        if(ongoingPurchase){
            stageOngoingPurchase.act();
            stageOngoingPurchase.draw();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            stage.addAction(Actions.sequence(Actions.fadeOut(0.8f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < musicTable.music.size(); i++)
                        if(musicTable.music.get(i).soundAmbiance!=null)
                            musicTable.music.get(i).soundAmbiance.dispose();
                    GlobalVariables.game.setScreen(new MenuScreen(GlobalVariables.game, mainMusicChanged));
                }
            })));
        }
        return false;
    }

    public class OngletBottomTable extends Table {
        TextButton buttonAssets;
        TextButton buttonMusic;
        TextButton buttonMode;
        TextButton buttonBonus;
        Skin skin;
        int idButtonChecked = 1;

        public OngletBottomTable() {
            skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
            skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1200.0f);
            buttonAssets = new TextButton("ASSETS", skin);
            buttonMusic = new TextButton("MUSIC", skin);
            buttonMode = new TextButton("MODE", skin);
            buttonBonus = new TextButton("BONUS", skin);
            buttonAssets.setStyle(skin.get("toggle", TextButton.TextButtonStyle.class));
            buttonMusic.setStyle(skin.get("toggle", TextButton.TextButtonStyle.class));
            buttonMode.setStyle(skin.get("toggle", TextButton.TextButtonStyle.class));
            buttonBonus.setStyle(skin.get("toggle", TextButton.TextButtonStyle.class));

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 8);
            setPosition(-Gdx.graphics.getWidth() / 50,Gdx.graphics.getHeight() / 50);

            buttonAssets.setChecked(true);

            add(buttonAssets).width(Value.percentWidth(.25F, this)).height(Value.percentHeight(1F, this));
            add(buttonMusic).width(Value.percentWidth(.25F, this)).height(Value.percentHeight(1F, this));
            add(buttonMode).width(Value.percentWidth(.25F, this)).height(Value.percentHeight(1F, this));
            add(buttonBonus).width(Value.percentWidth(.25F, this)).height(Value.percentHeight(1F, this));

            buttonAssets.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buttonAssets.isChecked()) {
                        buttonMusic.setChecked(false);
                        buttonMode.setChecked(false);
                        buttonBonus.setChecked(false);

                        idButtonChecked = 1;
                    }
                    buttonAssets.setChecked(true);
                }
            });
            buttonMusic.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buttonMusic.isChecked()) {
                        buttonAssets.setChecked(false);
                        buttonMode.setChecked(false);
                        buttonBonus.setChecked(false);

                        idButtonChecked = 2;
                    }
                    buttonMusic.setChecked(true);
                    musicTable.coinsLab.setText("Your coins : "+coinsStock);
                }
            });
            buttonMode.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buttonMode.isChecked()) {
                        buttonMusic.setChecked(false);
                        buttonAssets.setChecked(false);
                        buttonBonus.setChecked(false);

                        idButtonChecked = 3;
                    }
                    buttonMode.setChecked(true);
                    modeTable.coinsLab.setText("Your coins : "+coinsStock);
                }
            });
            buttonBonus.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buttonBonus.isChecked()) {
                        buttonMusic.setChecked(false);
                        buttonMode.setChecked(false);
                        buttonAssets.setChecked(false);

                        idButtonChecked = 4;
                    }
                    buttonBonus.setChecked(true);
                }
            });
        }
    }

    public class OngletAssetsTable extends Table {
        ImageButton p2Image;
        ImageButton x2Image;
        ImageButton snailImage;
        ImageButton secImage;
        Sprite secSpriteUp;
        Sprite secSpriteDown;
        BuyAsset p2Button;
        BuyAsset x2Button;
        BuyAsset snailButton;
        BuyAsset secButton;
        TextButton tokensButton;
        Label labTokens;
        Skin skin;
        int stockTokens;
        long timeLastClickedTokensButton;

        public OngletAssetsTable() {
            skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
            skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1200.0f);
            p2Button = new BuyAsset(5);
            x2Button = new BuyAsset(4);
            snailButton = new BuyAsset(3);
            secButton = new BuyAsset(1);
            initStock();
            secSpriteUp = new Sprite(new Texture("bonus/2nd.png"));
            secSpriteDown = new Sprite(new Texture("bonus/2ndBis.png"));
            secSpriteUp.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.25f);
            secSpriteDown.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.25f);
            p2Image = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("bonus/plus2.png"))), new TextureRegionDrawable(new TextureRegion(new Texture("bonus/plus2Bis.png"))));
            x2Image = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("bonus/fois2.png"))), new TextureRegionDrawable(new TextureRegion(new Texture("bonus/fois2Bis.png"))));
            snailImage = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("bonus/snailBut.png"))), new TextureRegionDrawable(new TextureRegion(new Texture("bonus/snailButBis.png"))));
            secImage = new ImageButton(new SpriteDrawable(secSpriteUp), new SpriteDrawable(secSpriteDown));
            p2Image.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.25f);
            p2Image.getImageCell().expand().fill();
            x2Image.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.25f);
            x2Image.getImageCell().expand().fill();
            snailImage.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.25f);
            snailImage.getImageCell().expand().fill();
            secImage.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.25f);
            secImage.getImageCell().expand().fill();

            tokensButton = new TextButton("GET TOKENS", skin);
            getStockTokens();
            labTokens = new Label("Your tokens : " + stockTokens, skin);
            labTokens.getStyle().fontColor=Color.WHITE;
            timeLastClickedTokensButton=0;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 20 * 16);
            setPosition(0, Gdx.graphics.getHeight() / 20 * 4);

            add(labTokens).colspan(2).right().height(Value.percentHeight(0.1f, this));
            row();
            add(tokensButton).width(Value.percentWidth(0.6f, this)).height(Value.percentHeight(0.15f, this)).colspan(2).padBottom(Value.percentHeight(0.02f, this));
            row();
            add(p2Image).width(Value.percentWidth(0.45f, this)).height(Value.percentHeight(0.25f, this)).padRight(Value.percentWidth(0.02f, this));
            add(x2Image).width(Value.percentWidth(0.45f, this)).height(Value.percentHeight(0.25f, this));
            row();
            add(p2Button).width(Value.percentWidth(0.25f, this)).height(Value.percentHeight(0.10f, this)).padBottom(Value.percentHeight(0.02f, this)).padRight(Value.percentWidth(0.02f, this));
            add(x2Button).width(Value.percentWidth(0.25f, this)).height(Value.percentHeight(0.10f, this)).padBottom(Value.percentHeight(0.02f, this));
            row();
            add(snailImage).width(Value.percentWidth(0.45f, this)).height(Value.percentHeight(0.25f, this)).padRight(Value.percentWidth(0.02f, this));
            add(secImage).width(Value.percentWidth(0.45f, this)).height(Value.percentHeight(0.25f, this));
            row();
            add(snailButton).width(Value.percentWidth(0.25f, this)).height(Value.percentHeight(0.10f, this)).padRight(Value.percentWidth(0.02f, this));
            add(secButton).width(Value.percentWidth(0.25f, this)).height(Value.percentHeight(0.10f, this));



            tokensButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(GlobalVariables.chartboostInterface.isOnline() && System.currentTimeMillis() - timeLastClickedTokensButton >=15000){
                        timeLastClickedTokensButton=System.currentTimeMillis();
                        stockTokens+=25;
                        setStockTokens();
                        labTokens.setText("Your tokens : " + stockTokens);
                        GlobalVariables.chartboostInterface.launchAds();
                    }
                }
            });

            p2Image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(stockTokens>=p2Button.cost) {
                        p2Button.stock++;
                        p2Button.stockField.setText("Stock:" + p2Button.stock);
                        stockTokens -= p2Button.cost;
                        setStockTokens();
                        labTokens.setText("Your tokens : " + stockTokens);
                        updateStock();
                        sounds.soundChange.play();
                    }else{
                        sounds.soundImpossible.play();
                    }
                }
            });
            x2Image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(stockTokens>=x2Button.cost) {
                        x2Button.stock++;
                        x2Button.stockField.setText("Stock:" + x2Button.stock);
                        stockTokens -= x2Button.cost;
                        setStockTokens();
                        labTokens.setText("Your tokens : " + stockTokens);
                        updateStock();
                        sounds.soundChange.play();
                    }else{
                        sounds.soundImpossible.play();
                    }
                }
            });
            snailImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(stockTokens>=snailButton.cost) {
                        snailButton.stock++;
                        snailButton.stockField.setText("Stock:" + snailButton.stock);
                        stockTokens -= snailButton.cost;
                        setStockTokens();
                        labTokens.setText("Your tokens : " + stockTokens);
                        updateStock();
                        sounds.soundChange.play();
                    }else{
                        sounds.soundImpossible.play();
                    }
                }
            });
            secImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(stockTokens>=secButton.cost) {
                        secButton.stock++;
                        secButton.stockField.setText("Stock:" + secButton.stock);
                        stockTokens -= secButton.cost;
                        setStockTokens();
                        labTokens.setText("Your tokens : " + stockTokens);
                        updateStock();
                        sounds.soundChange.play();
                    }else{
                        sounds.soundImpossible.play();
                    }
                }
            });
        }

        public void getStockTokens(){
            FileHandle file = Gdx.files.local("data/playerSpec.txt");
            if(file.exists()){
                stockTokens=Integer.parseInt(file.readString().split("-")[3]);
            }else{
                file.writeString("1-0-0-0", false);
                stockTokens=0;
            }
        }

        public void setStockTokens(){
            FileHandle file = Gdx.files.local("data/playerSpec.txt");
            if(file.exists()){
                file.writeString(file.readString().split("-")[0]+"-"+file.readString().split("-")[1]+"-"+file.readString().split("-")[2]+"-"+stockTokens, false);
            }else{
                file.writeString("1-0-0-0", false);
            }
        }

        public void initStock(){
            FileHandle file = Gdx.files.local("data/assetsStock.txt");
            if(file.exists()){
                p2Button.stock=Integer.parseInt(file.readString().split("-")[0]);
                snailButton.stock=Integer.parseInt(file.readString().split("-")[1]);
                x2Button.stock=Integer.parseInt(file.readString().split("-")[2]);
                secButton.stock=Integer.parseInt(file.readString().split("-")[3]);

                p2Button.stockField.setText("Stock:" + p2Button.stock);
                snailButton.stockField.setText("Stock:" + snailButton.stock);
                x2Button.stockField.setText("Stock:" + x2Button.stock);
                secButton.stockField.setText("Stock:" + secButton.stock);
            }else{
                file.writeString("0-0-0-0", false);
            }
        }

        public void updateStock(){
            FileHandle file = Gdx.files.local("data/assetsStock.txt");
            if(file.exists()){
                file.writeString(p2Button.stock+"-"+snailButton.stock+"-"+x2Button.stock+"-"+secButton.stock, false);
            }else{
                file.writeString("0-0-0-0", false);
            }
        }

    public class BuyAsset extends Table {
        Label stockField;
        Label costLab;
        Skin skin;
        int stock = 0;
        int cost;

        public BuyAsset(int costAsset) {
            cost=costAsset;
            skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
            skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1200.0f);
            costLab = new Label("Cost:" + costAsset, skin);
            costLab.getStyle().fontColor=Color.WHITE;
            stockField = new Label("Stock:" + stock, skin);
            stockField.getStyle().fontColor=Color.WHITE;

            add(costLab).padRight(Value.percentWidth(0.04f, this));
            add(stockField);
        }
        }
    }

    public class OngletMusicTable extends Table {
        Skin skin;
        ScrollPane scroll;
        Table subTable;
        Label coinsLab;
        Vector<MusicElement> music;
        FileHandle file;

        public OngletMusicTable(){
            skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
            skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1200.0f);


            file = Gdx.files.local("data/musicStates.txt");
            if(!file.exists()){
                file.writeString("3-1-1-1-1-1-1-1-1-1-1-1-1-1", false);
            }

            music = new Vector<MusicElement>();
            music.add(new MusicElement("AMBIANCE 1", 0));
            music.add(new MusicElement("AMBIANCE 2", 1));
            music.add(new MusicElement("AMBIANCE 3", 2));
            music.add(new MusicElement("AMBIANCE 4", 3));
            music.add(new MusicElement("AMBIANCE 5", 4));
            music.add(new MusicElement("AMBIANCE 6", 5));
            music.add(new MusicElement("AMBIANCE 7", 6));
            music.add(new MusicElement("AMBIANCE 8", 7));
            music.add(new MusicElement("AMBIANCE 9", 8));
            music.add(new MusicElement("AMBIANCE 10", 9));
            music.add(new MusicElement("AMBIANCE 11", 10));
            music.add(new MusicElement("AMBIANCE 12", 11));
            music.add(new MusicElement("AMBIANCE 13", 12));
            music.add(new MusicElement("AMBIANCE 14", 13));

            subTable = new Table();

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 20 * 16);
            setPosition(0, Gdx.graphics.getHeight() / 20 * 4);

            getCoinsStock();
            coinsLab = new Label("Your coins : "+coinsStock, skin);
            coinsLab.getStyle().fontColor=Color.WHITE;
            add(coinsLab).right().top().height(Value.percentHeight(0.1f, this));
            row();

            for(int i =0; i < music.size(); i++)
            {
                subTable.add(music.get(i)).height(Value.percentHeight(0.2f, this));
                subTable.row();
            }
            scroll = new ScrollPane(subTable);
            add(scroll);
        }

        public class MusicElement extends Table{
            Label lab;
            TextButton but;
            ImageButton play;
            boolean bought = false;
            boolean selected = false;
            Music soundAmbiance;
            int id;
            String nameMusic;

            public MusicElement(final String name, int idItem ){
                id = idItem;
                nameMusic = name;
                if(file.exists()){
                    if(file.readString().split("-")[id].equals("2"))
                        bought=true;
                    else if(file.readString().split("-")[id].equals("3")) {
                        selected = true;
                        bought=true;
                    }
                }
                lab = new Label(nameMusic, skin);
                lab.getStyle().fontColor=Color.WHITE;
                but = new TextButton("BUY", skin);
                play = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("play.png")))), new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("playBis.png")))));
                if(bought){
                    but.setText("BOUGHT");
                    if(selected) {
                        but.setText("SELECTED");
                    }
                }
                setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 20 * 16);

                add(lab).width(Value.percentWidth(0.6f, this));
                add(play).width(Value.percentWidth(0.15f, this));
                add(but).width(Value.percentWidth(0.25f, this));


                if(skin.getFont("button").getSpaceWidth()*(but.getText().length()+1)>but.getWidth())
                    but.getLabel().setFontScale(0.5f);
                but.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(coinsStock>=100 && !bought){
                            scroll.setSmoothScrolling(true);
                            stageOngoingPurchase = new Stage();
                            wdConfirm = new ValidatePurchaseWindow("", skin, stageOngoingPurchase, 100, id, TypeOnglet.MUSIC);
                            stageOngoingPurchase.addActor(wdConfirm);
                            ongoingPurchase=true;
                            stageOngoingPurchase.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                        }
                        else if(bought && !selected) {
                            changeToSelected();
                        }else if (coinsStock<100){
                            stageOngoingPurchase = new Stage();
                            wdCoins = new LakeCoinsWindow("", skin, stageOngoingPurchase, 100);
                            stageOngoingPurchase.addActor(wdCoins);
                            ongoingPurchase=true;
                            stageOngoingPurchase.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                        }
                        if(skin.getFont("button").getSpaceWidth()*(but.getText().length()+1)>but.getWidth())
                            but.getLabel().setFontScale(0.5f);
                    }
                });
                play.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        for(int i = 0; i < music.size(); i++)
                            if(music.get(i).soundAmbiance!=null)
                                music.get(i).soundAmbiance.dispose();
                        GlobalVariables.mainSound.pause();
                        soundAmbiance = Gdx.audio.newMusic(Gdx.files.internal("sounds/"+nameMusic+".mp3"));
                        soundAmbiance.play();

                    }
                });
            }

            public void changeToSelected(){

                for(int i = 0; i < music.size(); i++)
                {
                    if(music.get(i).selected) {
                        music.get(i).selected = false;
                        music.get(i).but.setText("BOUGHT");
                        if(skin.getFont("button").getSpaceWidth()*(music.get(i).but.getText().length()+1)>music.get(i).but.getWidth())
                            music.get(i).but.getLabel().setFontScale(0.5f);
                        else
                            music.get(i).but.getLabel().setFontScale(1f);

                        String str=file.readString();
                        file.writeString("", false);
                        for(int j = 0; j < i; j++)
                            file.writeString(str.split("-")[j]+"-", true);
                        file.writeString("2-", true);
                        for(int j = i+1; j < music.size()-1; j++)
                            file.writeString(str.split("-")[j]+"-", true);
                        file.writeString(str.split("-")[music.size()-1], true);
                    }
                }
                selected=true;
                but.setText("SELECTED");
                String str=file.readString();
                file.writeString("", false);
                for(int i = 0; i < id; i++)
                    file.writeString(str.split("-")[i]+"-", true);
                file.writeString("3-", true);
                for(int i = id+1; i < music.size()-1; i++)
                    file.writeString(str.split("-")[i]+"-", true);
                file.writeString(str.split("-")[music.size()-1], true);

                for(int i = 0; i < music.size(); i++)
                    if(music.get(i).soundAmbiance!=null)
                        music.get(i).soundAmbiance.dispose();
                GlobalVariables.mainSound.pause();
                soundAmbiance = Gdx.audio.newMusic(Gdx.files.internal("sounds/"+nameMusic+".mp3"));
                soundAmbiance.play();
                mainMusicChanged=true;
            }

            public void purchaseConfirmed(){
                coinsStock-=100;
                setCoinsStock();
                bought=true;
                coinsLab.setText("Your coins : "+coinsStock);

                String str=file.readString();
                file.writeString("", false);
                for(int i = 0; i < id; i++)
                    file.writeString(str.split("-")[i]+"-", true);
                file.writeString("2-", true);
                for(int i = id+1; i < music.size()-1; i++)
                    file.writeString(str.split("-")[i]+"-", true);
                file.writeString(str.split("-")[music.size()-1], true);

                changeToSelected();
            }
        }
    }

    public class OngletModeTable extends Table {
        Skin skin;
        ScrollPane scroll;
        Table subTable;
        Label coinsLab;
        Vector<ModeElement> mode;

        public OngletModeTable(){
            skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
            skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1200.0f);
            mode = new Vector<ModeElement>();

            FileHandle file;
            String[] levelStates;
            String textFile;
            file = Gdx.files.local("data/levelstate.txt");
            boolean bought1=false, bought2=false;
            if(file.exists()){
                textFile = file.readString();
                levelStates = textFile.split("-");
                if(!levelStates[2].equals("1"))
                    bought1=true;
                if(!levelStates[3].equals("1"))
                    bought2=true;

            }else{
                file.writeString("3-2-1-1", false);
            }

            mode.add(new ModeElement("HARDCORE", bought1, 5, 0));
            mode.add(new ModeElement("INTENSE", bought2, 10, 1));


            subTable = new Table();

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 20 * 16);
            setPosition(0, Gdx.graphics.getHeight() / 20 * 4);

            coinsLab = new Label("Your coins : "+coinsStock, skin);
            coinsLab.getStyle().fontColor=Color.WHITE;
            add(coinsLab).height(Value.percentHeight(0.1f, this)).padBottom(Value.percentHeight(0.2f, this)).right();
            row();

            for(int i =0; i < mode.size(); i++)
            {
                subTable.add(mode.get(i)).padBottom(Value.percentHeight(0.2f, this));
                subTable.row();
            }
            scroll = new ScrollPane(subTable);

            add(scroll).height(Value.percentHeight(0.7f, this));
        }

        public class ModeElement extends Table{
            Label lab;
            TextButton but;
            int requiredLevel;
            int id;

            public ModeElement(final String nameMode, boolean alreadyBought, int minLevel, int idElement){

                lab = new Label(nameMode, skin);
                lab.getStyle().fontColor=Color.WHITE;
                but = new TextButton("UNLOCK", skin);
                requiredLevel = minLevel;
                id=idElement;

                if(alreadyBought){
                    but.setText("GOT IT");
                    but.setTouchable(Touchable.disabled);
                }
                setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 20 * 16);

                add(lab).width(Value.percentWidth(0.75f, this));
                add(but).width(Value.percentWidth(0.25f, this));


                if(skin.getFont("button").getSpaceWidth()*(but.getText().length()+1)>but.getWidth())
                    but.getLabel().setFontScale(0.5f);

                but.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(coinsStock>=300 && GlobalVariables.levelPlayer>=requiredLevel){
                            stageOngoingPurchase = new Stage();
                            wdConfirm = new ValidatePurchaseWindow("", skin, stageOngoingPurchase, 300, id, TypeOnglet.MODE);
                            stageOngoingPurchase.addActor(wdConfirm);
                            ongoingPurchase=true;
                            stageOngoingPurchase.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                        }else{
                            stageOngoingPurchase = new Stage();
                            wdLockedMode = new LockedModeWindow("", skin, stageOngoingPurchase, 300, requiredLevel);
                            stageOngoingPurchase.addActor(wdLockedMode);
                            ongoingPurchase=true;
                            stageOngoingPurchase.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                        }
                    }
                });
            }
            public void purchaseConfirmed(){
                coinsStock-=300;
                but.setText("GOT IT");
                coinsLab.setText("Your coins : "+coinsStock);
                but.setTouchable(Touchable.disabled);
                but.getLabel().setFontScale(0.5f);

                FileHandle file;
                String[] levelStates;
                String textFile;
                file = Gdx.files.local("data/levelstate.txt");
                levelStates= new String[4];
                if(file.exists()){
                    textFile = file.readString();
                    levelStates = textFile.split("-");
                    if(requiredLevel==5)
                        levelStates[2]="2";
                    else
                        levelStates[3]="2";
                    file.writeString(levelStates[0]+"-"+levelStates[1]+"-"+levelStates[2]+"-"+levelStates[3], false);

                }else{
                    file.writeString("3-2-1-1", false);
                }
            }
        }
    }

    public class OngletBonusTable extends Table {
        Skin skin;
        ScrollPane scroll;
        Table subTable;
        Label coinsLab;
        Vector<BonusElement> bonus;

        public OngletBonusTable(){
            skin = new Skin(Gdx.files.internal("skin_rainbow/rainbow-ui.json"));
            skin.getFont("button").getData().setScale(Gdx.graphics.getWidth()/1200.0f);
            bonus = new Vector<BonusElement>();

            FileHandle file;
            String[] levelStates;
            String textFile;
            file = Gdx.files.local("data/bonusstate.txt");
            boolean bought1=false, bought2=false, bought3 =false;
            if(file.exists()){
                textFile = file.readString();
                levelStates = textFile.split("-");
                if(levelStates[0].equals("1"))
                    bought1=true;
                if(levelStates[1].equals("1"))
                    bought1=true;
                if(levelStates[2].equals("1"))
                    bought2=true;

            }else{
                file.writeString("0-0-0", false);
            }

            bonus.add(new BonusElement("ONE MORE LIFE", bought1, 0));
            bonus.add(new BonusElement("SCORE 10", bought2, 1));
            bonus.add(new BonusElement("GET 300 coins", bought3, 2));


            subTable = new Table();

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 20 * 16);
            setPosition(0, Gdx.graphics.getHeight() / 20 * 4);

            coinsLab = new Label("Your coins : "+coinsStock, skin);
            coinsLab.getStyle().fontColor=Color.WHITE;
            add(coinsLab).height(Value.percentHeight(0.1f, this)).padBottom(Value.percentHeight(0.2f, this)).right();
            row();

            for(int i =0; i < bonus.size(); i++)
            {
                subTable.add(bonus.get(i)).padBottom(Value.percentHeight(0.2f, this));
                subTable.row();
            }
            scroll = new ScrollPane(subTable);

            add(scroll).height(Value.percentHeight(0.7f, this));
        }

        public class BonusElement extends Table{
            Label lab;
            TextButton but;
            int id;

            public BonusElement(final String nameBonus, boolean alreadyBought, int idElement){

                lab = new Label(nameBonus, skin);
                lab.getStyle().fontColor=Color.WHITE;
                but = new TextButton("GET", skin);
                id=idElement;

                if(alreadyBought){
                    but.setText("GOT IT");
                    but.setTouchable(Touchable.disabled);
                }
                setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 20 * 16);

                add(lab).width(Value.percentWidth(0.75f, this));
                add(but).width(Value.percentWidth(0.25f, this));


                if(skin.getFont("button").getSpaceWidth()*(but.getText().length()+1)>but.getWidth())
                    but.getLabel().setFontScale(0.5f);

                but.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        long lastBought=0;
                        FileHandle file = Gdx.files.local("data/lastbonusbought.txt");
                        if(file.exists())
                            lastBought = Long.valueOf(file.readString());
                        if(TimeUtils.millis()-lastBought>=172800000) {
                            GlobalVariables.currentBonusPurchase=id;
                            if (id == 0) {
                                GlobalVariables.fbLeaderboardInt.publishOnFacebook();
                            }
                            if (id == 1) {
                                GlobalVariables.fbLeaderboardInt.publishOnFacebook();
                            }
                            if (id == 2) {
                                GlobalVariables.fbLeaderboardInt.publishOnFacebook();
                                coinsStock+=300;
                                setCoinsStock();
                            }
                            purchaseConfirmed();
                        }else{
                            long daysToWait = (172800000-TimeUtils.millis()+lastBought)/1000/3600;
                            stageOngoingPurchase = new Stage();
                            wdBonus = new UnavailableBonusWindow("", skin, stageOngoingPurchase,daysToWait);
                            stageOngoingPurchase.addActor(wdBonus);
                            ongoingPurchase=true;
                            stageOngoingPurchase.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
                        }
                    }
                });
            }
            public void purchaseConfirmed(){
                but.setText("GOT IT");
                coinsLab.setText("Your coins : "+coinsStock);
                but.setTouchable(Touchable.disabled);
                but.getLabel().setFontScale(0.5f);
            }
        }
    }

    public int getCoinsStock(){
        FileHandle file = Gdx.files.local("data/playerSpec.txt");
        if(file.exists()){
            return Integer.parseInt(file.readString().split("-")[2]);
        }else{
            file.writeString("1-0-0-0", false);
            return 0;
        }
    }

    public void setCoinsStock(){
        FileHandle file = Gdx.files.local("data/playerSpec.txt");
        if(file.exists()){
            file.writeString(file.readString().split("-")[0]+"-"+file.readString().split("-")[1]+"-"+coinsStock+"-"+file.readString().split("-")[3], false);
        }else{
            file.writeString("1-0-0-0", false);
        }
    }

    public enum TypeOnglet{
        MUSIC,
        MODE;
    }

    public class ValidatePurchaseWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stage;
        TextButton butYes;
        TextButton butNo;
        Label lab;
        int idPurchase;
        TypeOnglet typePurchase;

        public ValidatePurchaseWindow(String m_title, Skin m_skin, Stage m_stage, int necessaryCoins, int id, TypeOnglet type) {
            super(m_title, m_skin);
            skin=m_skin;
            stage = m_stage;
            idPurchase=id;
            typePurchase = type;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            butYes = new TextButton("Yes", skin);
            butYes.setSize(getWidth()*1/3, getHeight()/2);
            butNo = new TextButton("No", skin);
            butNo.setSize(getWidth()*1/3, getHeight()/2);
            lab = new Label("Cost : " + necessaryCoins + " coins\nConfirm?", skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);


            butYes.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            if(typePurchase == TypeOnglet.MUSIC)
                                musicTable.music.get(idPurchase).purchaseConfirmed();
                            else if(typePurchase == TypeOnglet.MODE)
                                modeTable.mode.get(idPurchase).purchaseConfirmed();
                            ongoingPurchase=false;
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
                            ongoingPurchase=false;
                        }
                    })));
                }
            });
            this.setColor(Color.WHITE);
            this.setBackground(new SpriteDrawable(back));
            add(lab).row();
            add(butYes).width(Value.percentWidth(0.5f, this));
            add(butNo).width(Value.percentWidth(0.5f, this));
        }
    }

    public class LakeCoinsWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stage;
        TextButton but;
        Label lab;

        public LakeCoinsWindow(String m_title, Skin m_skin, Stage m_stage, int necessaryCoins) {
            super(m_title, m_skin);
            skin=m_skin;
            stage = m_stage;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            but = new TextButton("BACK", skin);
            but.setSize(getWidth()*2/3, getHeight()/2);
            lab = new Label("You need " + necessaryCoins + " coins", skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);

            but.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingPurchase=false;
                        }
                    })));
                }
            });

            this.setBackground(new SpriteDrawable(back));
            add(lab).row();
            add(but);
        }
    }

    public class LockedModeWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stage;
        TextButton but;
        Label lab;

        public LockedModeWindow(String m_title, Skin m_skin, Stage m_stage, int necessaryCoins, int necessaryLevel) {
            super(m_title, m_skin);
            skin=m_skin;
            stage = m_stage;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            but = new TextButton("BACK", skin);
            but.setSize(getWidth()*2/3, getHeight()/2);
            lab = new Label("Cost : "+necessaryCoins+"\nRequired level : "+necessaryLevel, skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);

            but.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingPurchase=false;
                        }
                    })));
                }
            });

            this.setBackground(new SpriteDrawable(back));
            add(lab).row();
            add(but);
        }
    }

    public class UnavailableBonusWindow extends Window
    {
        Sprite back;
        Skin skin;
        Stage stage;
        TextButton but;
        Label lab;

        public UnavailableBonusWindow(String m_title, Skin m_skin, Stage m_stage, long necessaryTime) {
            super(m_title, m_skin);
            skin=m_skin;
            stage = m_stage;

            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/5);
            setPosition(0, Gdx.graphics.getHeight()/2-getHeight()/2);

            back = new Sprite(new Texture(Gdx.files.internal("paperTexture2.jpg")));
            but = new TextButton("BACK", skin);
            but.setSize(getWidth()*2/3, getHeight()/2);
            lab = new Label("You need to wait " + necessaryTime + "h", skin);
            lab.setSize(getWidth()*2/3, getHeight()/2);

            but.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.sequence(Actions.fadeOut(0.7f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ongoingPurchase=false;
                        }
                    })));
                }
            });

            this.setBackground(new SpriteDrawable(back));
            add(lab).row();
            add(but);
        }
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
