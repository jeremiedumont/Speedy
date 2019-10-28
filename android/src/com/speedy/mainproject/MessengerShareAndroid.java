package com.speedy.mainproject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;

import java.io.ByteArrayOutputStream;

/**
 * Created by test on 10/8/2017.
 */
public class MessengerShareAndroid implements com.speedy.mainproject.MessengerShareInterface {
    Activity actSup;
    boolean permissionsClear=false;
    public MessengerShareAndroid(Activity actPassed){actSup=actPassed;}
    @Override
    public void shareToMessenger() {
        String mimeType = "image/*";

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            checkIfAlreadyhavePermission();
        }
        BitmapDrawable draw = writeOnDrawable(R.drawable.shareimage);
        if(permissionsClear) {
            Resources resources = actSup.getResources();
            FileHandle file = Gdx.files.local("data/levelstate.txt");
            if(file.exists()){
                String textFile = file.readString();
                String[] levelStates = textFile.split("-");
                if(levelStates[0].equals("3"))
                    draw = writeOnDrawable(R.drawable.shareimage);
                else if(levelStates[1].equals("3"))
                    draw = writeOnDrawable(R.drawable.shareimage);
                else if(levelStates[2].equals("3"))
                    draw = writeOnDrawable(R.drawable.shareimagegreen);
                else if(levelStates[3].equals("3"))
                    draw = writeOnDrawable(R.drawable.shareimagered);
            }else
                draw = writeOnDrawable(R.drawable.shareimage);
            String path = MediaStore.Images.Media.insertImage(actSup.getContentResolver(), draw.getBitmap(), "Image Description", null);
            Uri uri = Uri.parse(path);
            ShareToMessengerParams shareToMessengerParams =
                    ShareToMessengerParams.newBuilder(uri, mimeType)
                            .build();

// Sharing from an Activity
            MessengerUtils.shareToMessenger(
                    actSup,
                    0,
                    shareToMessengerParams);
        }
    }

    private boolean checkIfAlreadyhavePermission() {

        ContextCompat.checkSelfPermission(actSup, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if((ContextCompat.checkSelfPermission(actSup, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            //show dialog to ask permission
            ActivityCompat.requestPermissions(actSup,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

            return true;
        } else
            permissionsClear=true;

        return false;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public BitmapDrawable writeOnDrawable(int drawableId){

        String modeText="EASY";
        FileHandle file = Gdx.files.local("data/levelstate.txt");
        if(file.exists()){
            String textFile = file.readString();
            String[] levelStates = textFile.split("-");
            if(levelStates[1].equals("3"))
                modeText="MEDIUM";
            else if(levelStates[2].equals("3"))
                modeText="HARD";
            else if(levelStates[3].equals("3"))
                modeText="INTENSE";
        }

        Bitmap bm = BitmapFactory.decodeResource(actSup.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(100);
        String text1 = "YOUR SCORE : "+GlobalVariables.lastScore;
        String text2 = "BEST SCORE : "+GlobalVariables.bestScore;
        String text3 = "MODE : " + modeText;

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text1, bm.getWidth()/2-paint.measureText(text1)/2, bm.getHeight()/1.5f, paint);
        paint.setTextSize(60);
        canvas.drawText(text2, bm.getWidth()/2-paint.measureText(text2)/2, bm.getHeight()/1.2f, paint);
        paint.setTextSize(40);
        canvas.drawText(text3, bm.getWidth()/2-paint.measureText(text3)/2, bm.getHeight()*29/30, paint);

        return new BitmapDrawable(actSup.getResources(), bm);
    }
}
