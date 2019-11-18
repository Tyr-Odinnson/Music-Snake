package com.example.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class SnakeAnimView extends SurfaceView implements Runnable {
    private final int numFrames = 6;

    private Rect rectToBeDrawn;
    private Point frameSize;
    private int frameNumber;
    private long lastFrameTime;
    private int fps;

    SnakeAnimView(Context context) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        frameSize = new Point(
            MainActivity.instance.headAnimBitmap.getWidth()/numFrames
        ,   MainActivity.instance.headAnimBitmap.getHeight()
        );
    }

    Thread ourThread = null;
    SurfaceHolder ourHolder;
    volatile boolean playingSnake;
    Paint paint;

    @Override
    public void run() {
        while (playingSnake) {
            update();
            draw();
            controlFPS();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        MainActivity.instance.startActivity(MainActivity.instance.intent);
        return true;
    }

    void update() {
        //which frame should we draw
        rectToBeDrawn = new Rect((frameNumber * frameSize.x)-1, 0,(frameNumber * frameSize.x + frameSize.x) - 1, frameSize.x);

        //now the next frame
        frameNumber++;

        //don't try and draw frames that don't exist
        if(frameNumber == numFrames){
            frameNumber = 0;//back to the first frame
        }
    }

    void draw() {
        if (ourHolder.getSurface().isValid()) {
            Canvas canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.argb(255, 255, 255, 255));

            drawSnakeText(canvas);

            Point screenSize = MainActivity.instance.screenSize;

            drawHighScoreText(canvas, screenSize);
            drawSnakeHead(canvas, screenSize);

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawHighScoreText(Canvas canvas, Point screenSize) {
        paint.setTextSize(25);
        canvas.drawText(
            "Hi Score:" + SnakeView.highScore
        ,   10
        ,   screenSize.y - 50
        ,   paint
        );
    }

    private void drawSnakeText(Canvas canvas) {
        paint.setTextSize(150);
        canvas.drawText(
            "Snake"
        ,   10
        ,   150
        ,   paint
        );
    }

    private void drawSnakeHead(Canvas canvas, Point screenSize) {
        Rect destRect = new Rect(
            screenSize.x / 2 - 100
        ,   screenSize.y / 2 - 100
        ,   screenSize.x / 2 + 100
        ,   screenSize.y / 2 + 100
        );

        canvas.drawBitmap(MainActivity.instance.headAnimBitmap, rectToBeDrawn, destRect, paint);
    }

    void controlFPS() {
        long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
        System.out.println("TESTTTTT: " + timeThisFrame);
        long timeToSleep = (1000 / 8) - timeThisFrame;

        if (timeThisFrame > 0) {
            fps = (int) (1000 / timeThisFrame);
        }

        if (timeToSleep > 0) {
            try {
                ourThread.sleep(timeToSleep);
            } catch (InterruptedException e) {
            }
        }

        lastFrameTime = System.currentTimeMillis();
    }

    void pause() {
        playingSnake = false;
        try {
            ourThread.join();
        } catch (InterruptedException e) {
        }
    }

    void resume() {
        playingSnake = true;
        ourThread = new Thread(this);
        ourThread.start();
    }
}
