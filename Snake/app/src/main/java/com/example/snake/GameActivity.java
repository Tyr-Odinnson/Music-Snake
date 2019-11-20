package com.example.snake;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

public class GameActivity extends Activity {
    static GameActivity instance;

    Bitmap headBitmap;
    Bitmap bodyBitmap;
    Bitmap tailBitmap;
    Bitmap appleBitmap;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;
    int topGap;
    int blockSize;
    Point screenSize = new Point();
    Point blockDimensions = new Point();

    private SnakeView snakeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        initializeAudio();
        configureDisplay();
        initializeBitmaps();

        snakeView = new SnakeView(this);
        setContentView(snakeView);
    }

    @Override
    protected void onStop() {
        super.onStop();

        while (true) {
            snakeView.pause();
            break;
        }

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            snakeView.pause();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();

            return true;
        }

        return false;
    }

    private void initializeAudio() {
        sample1 = AudioManager.instance.getSample("sample1.ogg");
        sample2 = AudioManager.instance.getSample("sample2.ogg");
        sample3 = AudioManager.instance.getSample("sample3.ogg");
        sample4 = AudioManager.instance.getSample("sample4.ogg");
    }

    void configureDisplay(){
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);
        topGap = screenSize.y / 14;

        int blocksHorizontal = 15;
        blockSize = screenSize.x / blocksHorizontal;

        blockDimensions.x = blocksHorizontal;
        blockDimensions.y = (screenSize.y - topGap) / blockSize;
    }

    private void initializeBitmaps() {
        // Load and scale bitmaps.
        headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snake_head);
        bodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body);
        tailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snake_tail);
        appleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.note);

        // Scale the bitmaps to match the block size.
        headBitmap = Bitmap.createScaledBitmap(headBitmap, blockSize, blockSize, false);
        bodyBitmap = Bitmap.createScaledBitmap(bodyBitmap, blockSize, blockSize, true);
        tailBitmap = Bitmap.createScaledBitmap(tailBitmap, blockSize, blockSize, false);
        appleBitmap = Bitmap.createScaledBitmap(appleBitmap, blockSize, blockSize, false);
    }
}
