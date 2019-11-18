package com.example.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

class SnakeView extends SurfaceView implements Runnable {
    SnakeView(Context context) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();

        snake = new Snake();
        initializeApple();
    }

    enum Directions {
        UP(0),
        RIGHT(1),
        DOWN(2),
        LEFT(3);

        Directions(int Value) {
            this.value = Value;
        }


        public int value;
        public static Directions fromInt(int i) {
            for (Directions d : Directions .values()) {
                if (d.value == i) { return d; }

            }
            return null;
        }

    }

    private final int FRAME_RATE = 4;

    public static int highScore;

    // Stats.
    private long lastFrameTime;
    private int fps;
    private int score;

    //Game objects.
    private Snake snake;
    private Point apple = new Point();

    private Thread ourThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean isPlaying;
    private Paint paint;

    @Override
    public void run() {
        while (isPlaying) {
            updateGame();
            drawGame();
            controlFPS();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                snake.setNewDirection(motionEvent);
        }

        return true;
    }

    void initializeApple(){
        Random random = new Random();
        Point dimensions = GameActivity.instance.blockDimensions;
        apple = new Point(
            random.nextInt(dimensions.x - 1) + 1
        ,   random.nextInt(dimensions.y - 1) + 1
        );
    }

    void updateGame() {
        snake.move();
        onCollisionApple();
        onCollisionObstacle();

    }

    private void restart(GameActivity ga) {
        AudioManager.play(ga.sample4);
        highScore = score > highScore ? score : highScore;
        score = 0;
        snake.initialize();
    }

    private void onCollisionApple() {
        if(snake.positions[0].x == apple.x && snake.positions[0].y == apple.y){
            snake.length++;
            initializeApple();
            score += snake.length;

            AudioManager.play(GameActivity.instance.sample1);
        }
    }

    private void onCollisionObstacle() {
        boolean isDead;
        GameActivity ga = GameActivity.instance;
        isDead = onCollisionWall(ga) || snake.onCollisionSelf();

        if(isDead){
            restart(ga);
        }
    }

    private boolean onCollisionWall(GameActivity ga) {
        return
            snake.positions[0].x == -1
        ||  snake.positions[0].x >= ga.blockDimensions.x
        ||  snake.positions[0].y == -1
        ||  snake.positions[0].y == ga.blockDimensions.y;
    }

    void drawGame() {
        GameActivity ga = GameActivity.instance;

        if (ourHolder.getSurface().isValid()) {
            Canvas canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(ga.topGap / 2);
            canvas.drawText("Score:" + score + "  Hi:" + highScore, 10, ga.topGap - 6, paint);

            int topGap = ga.topGap;
            int blockSize = ga.blockSize;

            drawBorder(ga, canvas, topGap, blockSize);
            snake.draw(ga, canvas, topGap, blockSize, paint);
            drawApple(ga, canvas, topGap, blockSize);

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawApple(GameActivity ga, Canvas canvas, int topGap, int blockSize) {
        Bitmap appleBitmap = ga.appleBitmap;
        canvas.drawBitmap(appleBitmap, apple.x * blockSize, (apple.y * blockSize) + topGap, paint);
    }

    private void drawBorder(GameActivity ga, Canvas canvas, int topGap, int blockSize) {
        Point screenSize = ga.screenSize;
        Point blockDimensions = ga.blockDimensions;

        paint.setStrokeWidth(3);//3 pixel border
        canvas.drawLine(1, topGap, screenSize.x - 1, topGap, paint);
        canvas.drawLine(screenSize.x - 1, topGap, screenSize.x - 1, topGap + (blockDimensions.y * blockSize), paint);
        canvas.drawLine(screenSize.x - 1, topGap + (blockDimensions.y * blockSize), 1, topGap + (blockDimensions.y * blockSize), paint);
        canvas.drawLine(1, topGap, 1, topGap + (blockDimensions.y * blockSize), paint);
    }

    void controlFPS() {
        long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
        long timeToSleep = (1000/ FRAME_RATE) - timeThisFrame;

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
        isPlaying = false;
        try {
            ourThread.join();
        } catch (InterruptedException e) {
        }

    }

    void resume() {
        isPlaying = true;
        ourThread = new Thread(this);
        ourThread.start();
    }
}
