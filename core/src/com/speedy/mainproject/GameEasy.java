package com.speedy.mainproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameEasy extends GameSuperClass implements InputProcessor, Screen {

    Label labChar[];
    float coeffSpeedMove=0.45f;
    int angle1=100;

    public GameEasy()
    {

    }

    public void show() {
        init(GlobalVariables.gameEnum.gameEasy);

        inputmult.addProcessor(this);
        inputmult.addProcessor(stage);

        font2 = new BitmapFont(Gdx.files.internal("Font/FontStatic1/font1.fnt"), false);
        font2.setColor(Color.WHITE);
        font2.getData().setScale(Gdx.graphics.getWidth()/1000.0f);

        font3 = new BitmapFont(Gdx.files.internal("Font/FontStatic1/font1.fnt"), false);
        font3.setColor(Color.BLACK);
        font3.getData().setScale(Gdx.graphics.getWidth()/3650.0f);

        generateAngle();

        labChar = new Label[4];
        for(int i = 0; i < 4; i++){
            labChar[i] = new Label(charA1, skin2);
            labChar[i].setFontScale(Gdx.graphics.getWidth()/300.0f);
            labChar[i].setPosition(Gdx.graphics.getWidth()/5.5f*(i+1)-labChar[i].getStyle().font.getSpaceWidth()/2, Gdx.graphics.getHeight()/numH*8.8f);
        }

        temp=(rd.nextInt(9)+49);
        charA1=Character.toString((char)temp);
        temp=(rd.nextInt(9)+49);

        bt = new Button[numH-4][numW];
        for (int i = 0; i < numH-4; i++) {
            for (int j = 0; j < numW; j++) {
                bt[i][j]= new Button(skin);
                bt[i][j].setBounds(j * Gdx.graphics.getWidth() / numW, (i+2) * Gdx.graphics.getHeight() / numH, Gdx.graphics.getWidth() / numW, Gdx.graphics.getHeight() / numH);
                stage.addActor(bt[i][j]);
            }
        }


        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.7f)));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        timeRefresh=Gdx.graphics.getDeltaTime()*tScore;
        timeAct=System.currentTimeMillis();
        strScore="CURRENT SCORE : "+String.valueOf(score);
        strLife="LIFE : "+String.valueOf(GlobalVariables.actualLife);


        if(GlobalVariables.actualLife != 0)
            Gdx.input.setInputProcessor(inputmult);
        else if(ongoingNotConnected)
            Gdx.input.setInputProcessor(stageRateWd);
        else if (ongoingRedirect)
            Gdx.input.setInputProcessor(stageRedirect);
        else
            Gdx.input.setInputProcessor(stageGameOver);
        batch.begin();
        if(GlobalVariables.actualLife!=0 && timeAct-timePrec>timeRefresh) {
            for (int i = 0; i < numH - 4; i++) {
                for (int j = 0; j < numW; j++) {
                    rc[i][j] = rd.nextInt();
                    if(j>0 && rc[i][j]%10==rc[i][j-1]%10)
                        rc[i][j]++;
                }
            }
            newCharacter();
            timePrec=timeAct;
        }

        if(charToRed && timeAct-charToRedTime>=500){
            for(int i = 0; i < 4; i++)
                labChar[i].getStyle().fontColor=Color.WHITE;
            charToRed=false;
        }

        for(int i = 0; i < numH-4; i++) {
            for (int j = 0; j < numW; j++) {
                if ((rc[i][j]) % 10 == 0)
                    bt[i][j].setColor(Color.ROYAL);
                if ((rc[i][j]) % 10 == 1)
                    bt[i][j].setColor(Color.MAROON);
                if ((rc[i][j]) % 10 == 2)
                    bt[i][j].setColor(Color.FIREBRICK);
                if ((rc[i][j]) % 10 == 3)
                    bt[i][j].setColor(Color.GREEN);
                if ((rc[i][j]) % 10 == 4)
                    bt[i][j].setColor(Color.RED);
                if ((rc[i][j]) % 10 == 5)
                    bt[i][j].setColor(Color.BROWN);
                if ((rc[i][j]) % 10 == 6)
                    bt[i][j].setColor(Color.YELLOW);
                if ((rc[i][j]) % 10 == 7)
                    bt[i][j].setColor(Color.CHARTREUSE);
                if ((rc[i][j]) % 10 == 8)
                    bt[i][j].setColor(Color.GOLD);
                if ((rc[i][j]) % 10 == 9)
                    bt[i][j].setColor(Color.ORANGE);

            }
        }


        stage.act();
        stage.draw();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.end();
        batch.end();
        batch.begin();
        imTop.draw(batch);
        imBottom.draw(batch);
        reboundsLabel();
        lab1.setPosition((float)(lab1.getX()+Gdx.graphics.getDeltaTime()*Gdx.graphics.getHeight()*coeffSpeedMove*Math.cos(Math.toRadians(angle1))), (float)(lab1.getY()+Gdx.graphics.getDeltaTime()*Gdx.graphics.getHeight()*coeffSpeedMove*Math.sin(Math.toRadians(angle1))));
        lab1.draw(batch, 1f);
        labScore.setText("SCORE : "+score);
        labScore.draw(batch, 1f);
        labLife.setText("LIFE : "+ GlobalVariables.actualLife);
        labLife.draw(batch, 1f);
        for(int i = 0; i < 4; i++) {
            labChar[i].setText(charA1);
            labChar[i].draw(batch, 1f);
        }
        backButton.draw(batch, 0.6f);
        batch.end();
        if(pe!=null) {
            pe.update(Gdx.graphics.getDeltaTime());
            batch.begin();
            pe.draw(batch);
            batch.end();
        }
        batch.begin();
        bonus.draw(batch);
        batch.end();

        if(GlobalVariables.actualLife<1) {
            stageGameOver.act();
            stageGameOver.draw();
        }

        if(ongoingNotConnected){
            stageRateWd.act();
            stageRateWd.draw();
        }

        if(timeAct-timeNewHighScore<2000){
            stageNewHighScore.act();
            stageNewHighScore.draw();
        }

        if(ongoingRedirect){
            stageRedirect.act();
            stageRedirect.draw();
        }
    }

    /* Méthode appelé lors qu'un clique sur l'écran, permettant le traitement de l'évènement*/

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(screenY<Gdx.graphics.getHeight()/numH*8 && screenY>Gdx.graphics.getHeight()/numH*2) { //Si on se situe dans le cadre de jeu


            if ((charA1.equals(charD1))) { //Si le caractère appuyé est le bon
                do {
                    temp = (rd.nextInt(9) + 49);
                } while (charA1.equals(Character.toString((char) temp)));
                charA1 = Character.toString((char) temp); //On change le caractère recherché
                newCharacter();
                timePrec=timeAct;
                generateAngle();
                coeffSpeedMove+=0.009f;
                if(bonus.x2Activated) { //Si on est en bonus x2
                    score += 2;
                    bonus.x2Left--;
                    if(bonus.x2Left==0)
                        bonus.x2Activated=false;
                }
                else
                    score++;
                //On change la rapidité de la partie suivant le niveau
                if (score < 10)
                    tScore -= 2000;
                else
                    tScore -= 500;
                bonus.secondActivated=false;

                sounds.soundSuccess.play(0.7f);
                pe = new ParticleEffect();
                pe.load(Gdx.files.internal("explosion.particle"),Gdx.files.internal(""));
                pe.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
                pe.start();

                if(score>bestScore && bestScore!=0 &&!alreadyReachedHighScore){
                    timeNewHighScore = System.currentTimeMillis();
                    stageNewHighScore.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));
                    alreadyReachedHighScore=true;
                }
            }
            else { //Si on s'est trompé et qu'on est sur le point de relancer le défilement
                //On vérifie si on a pas activé le bonus seconde chance, et on agit en conséquence
                if (!bonus.secondActivated)
                    GlobalVariables.actualLife--;
                else
                    bonus.secondActivated=false;
                sounds.soundFail.play();
                for(int i = 0; i < 4; i++)
                    labChar[i].getStyle().fontColor=Color.RED;
                charToRed = true;
                charToRedTime=System.currentTimeMillis();
            }

            if(GlobalVariables.actualLife==0) {
                sounds.soundOver.play(0.7f);
                GameOverWindow wd = new GameOverWindow("", new Skin(Gdx.files.internal("skin_glassy/glassy-ui.json")), score, stageGameOver, this, GlobalVariables.gameEnum.gameEasy);
                stageGameOver.addActor(backButton);

                launchRateWindow();
            }
        }

        return false;
    }

    public void newCharacter()
    {
        String charTemp = charD1;
        do{
            if (rd.nextInt(4) == 1) {
                if (GlobalVariables.actualLife > 0) {
                    charD1 = charA1;
                }
            } else {
                temp = (rd.nextInt(9) + 49);
                charD1 = Character.toString((char) temp);
            }
        }while (charTemp.equals(charD1));
        lab1.setText(charD1);
    }

    public void reboundsLabel(){
        if(lab1.getX()<=0){
            angle1=180-angle1;
            lab1.setX(lab1.getX()+Gdx.graphics.getDeltaTime()*800);//Avoid to be blocked in a corner
        }
        else if(lab1.getX()+lab1.getWidth()>=Gdx.graphics.getWidth()){
            angle1=180-angle1;
            lab1.setX(lab1.getX()-Gdx.graphics.getDeltaTime()*800);
        }
        else if(lab1.getY()<=Gdx.graphics.getHeight()/numH*2){
            angle1=360-angle1;
            lab1.setY(lab1.getY()+Gdx.graphics.getDeltaTime()*800);
        }
        else if(lab1.getY()+lab1.getHeight()>=Gdx.graphics.getHeight()/numH*8){
            angle1=360-angle1;
            lab1.setY(lab1.getY()-Gdx.graphics.getDeltaTime()*800);
        }
    }

    public void generateAngle(){
        int tempAngle=angle1;
        do{
            angle1=rd.nextInt(359);
        }while(Math.abs(angle1-tempAngle)<=45);
    }

    @Override
    public void resize(int width, int height) {
        font.getData().setScale(Gdx.graphics.getWidth()/550.0f);
        lab1.setOrigin(lab1.getWidth()/2, lab1.getHeight()/2);
        font2.getData().setScale(Gdx.graphics.getWidth()/1000.0f);
        font3.getData().setScale(Gdx.graphics.getWidth()/3650.0f);
        imTop.setBounds(0, Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/numH*2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/numH*2);
        imBottom.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/numH);
        for (int i = 0; i < numH-4; i++) {
            for (int j = 0; j < numW; j++) {

                bt[i][j].setBounds(j * Gdx.graphics.getWidth() / numW, (i+2) * Gdx.graphics.getHeight() / numH, Gdx.graphics.getWidth() / numW, Gdx.graphics.getHeight() / numH);

            }
        }
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
    public void dispose () {
        batch.dispose();
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
    public boolean keyUp(int keycode) {
        return false;}

    @Override
    public boolean keyTyped(char character) {
        return false;}

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;}

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;}

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;}

    @Override
    public boolean scrolled(int amount) {
        return false;}
}
