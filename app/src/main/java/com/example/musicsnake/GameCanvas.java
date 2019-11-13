package com.example.musicsnake;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;
import java.util.Timer;

public class GameCanvas extends View implements GestureDetector.OnGestureListener {

    public static final int swipeThreshold = 100;
    public static final int swipeVelocityThreshold = 100;
    private int score = 0;
    private SoundPlayer sound;
    float canvasWidth, canvasHeight;
    float noteX = 600, noteY = 200, bodyX, bodyY;
    float restX = 300, restY = 100;
    int snakeX = 200, snakeY = 600;
    int getSnakeXSpeed = 10, getSnakeYSpeed;
    float snakeWidth, snakeHeight, noteWidth, noteHeight, restWidth, restHeight;
    Bitmap restimg, snakeImage, snakeImage2, noteImage, snakeBitmap, snakeBitmap2, noteBitmap, restBitmap, background;
    GestureDetector gestureDetector;
    float GestureStartX, GestureEndX, GestureStartY, GestureEndY, XMovement, YMovement;
    int gestureType;
    Random r;

    private Handler handler = new Handler();
    private Timer timer = new Timer();

    public GameCanvas (Context context) {
        super(context);
        sound = new SoundPlayer(context);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bk);

        r = new Random();
        gestureDetector = new GestureDetector(this);

        restimg = BitmapFactory.decodeResource(getResources(), R.drawable.rest);
        snakeImage = BitmapFactory.decodeResource(getResources(), R.drawable.snake_head);
        snakeImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.snakehead);
        noteImage = BitmapFactory.decodeResource(getResources(), R.drawable.note);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        canvas.drawBitmap(background, 0, 0, null);

        background = Bitmap.createScaledBitmap(background, (int)canvasWidth, (int)canvasHeight, true);
        noteBitmap = Bitmap.createScaledBitmap(noteImage, 40, 50, true);
        restBitmap = Bitmap.createScaledBitmap(restimg, 40, 50, true);

        canvas.drawBitmap(restBitmap, restX, restY, null);
        canvas.drawBitmap(noteBitmap, noteX, noteY, null);

        Paint paintText = new Paint();
        paintText.setTextSize(70);
        paintText.setColor(Color.BLACK);
        canvas.drawText("Score: ", 10, 60, paintText);
        canvas.drawText(Integer.toString(score), 240, 65, paintText);

        snakeBitmap = Bitmap.createScaledBitmap(snakeImage, 150, 100, true);

        snakeSpeed();

        canvas.drawBitmap(snakeBitmap, snakeX, snakeY, null);

        score = collision();
        snakeWidth = snakeBitmap.getWidth();
        snakeHeight = snakeBitmap.getHeight();
        noteHeight = noteBitmap.getHeight();
        noteWidth = noteBitmap.getWidth();
        restHeight = restBitmap.getHeight();
        restWidth = restBitmap.getWidth();
        invalidate();
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
