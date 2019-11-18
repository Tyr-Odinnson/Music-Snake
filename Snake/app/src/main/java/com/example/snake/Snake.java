package com.example.snake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

public class Snake {
    Snake() {
        populateAllPotentialSnakePieces();
        initialize();
    }

    Point[] positions;
    int length;

    private SnakeView.Directions directionOfTravel = SnakeView.Directions.RIGHT;

    private void populateAllPotentialSnakePieces() {
        positions = new Point[200];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Point();
        }
    }

    void initialize(){
        length = 3;

        // Head.
        Point dimensions = GameActivity.instance.blockDimensions;
        positions[0].x = dimensions.x / 2;
        positions[0].y = dimensions.y / 2;

        // Body.
        positions[1].x = positions[0].x - 1;
        positions[1].y = positions[0].y;

        // Tail.
        positions[2].x = positions[1].x - 1;
        positions[2].y = positions[0].y;
    }

    void draw(GameActivity ga, Canvas canvas, int topGap, int blockSize, Paint paint) {
        Bitmap head = ga.headBitmap;
        Bitmap body = ga.bodyBitmap;
        Bitmap tail = ga.tailBitmap;

        canvas.drawBitmap(
            rotateHead(head, blockSize)
        ,   positions[0].x * blockSize
        ,   (positions[0].y * blockSize) + topGap
        ,   paint
        );

        for(int i = 1; i < length -1; i++){
            canvas.drawBitmap(
                rotateBody(body, blockSize, i)
            ,   positions[i].x * blockSize
            ,   (positions[i].y * blockSize) + topGap
            ,   paint
            );
        }

        canvas.drawBitmap(
            rotateBody(tail, blockSize, length - 1)
        ,   positions[length - 1].x * blockSize
        ,   (positions[length - 1].y * blockSize) + topGap
        ,   paint
        );
    }

    void move() {
        // Move the body, starting at the back.
        for(int i = length; i >0 ; i--){
            positions[i].x = positions[i - 1].x;
            positions[i].y = positions[i - 1].y;
        }

        switch (directionOfTravel){
            case UP:
                positions[0].y--;
                break;
            case RIGHT:
                positions[0].x++;
                break;
            case DOWN:
                positions[0].y++;
                break;
            case LEFT:
                positions[0].x--;
                break;
        }
    }

    private float getBodyAngle(int _i) {
        Point direction = GlobalMethods.subtract(positions[_i], positions[_i - 1]);

        if (direction.x == 0 && Math.abs(direction.y) == 1) {
            return 90;
        }

        return 0;
    }

    private Point getBodyScale(int _i) {
        Point direction = GlobalMethods.subtract(positions[_i], positions[_i - 1]);

        if (direction.x == 0 && direction.y == 1) {
            return new Point(1, -1);
        } else if (direction.x == 1 && direction.y == 0) {
            return new Point(-1, 1);
        }

        return new Point(1, 1);
    }

    private Bitmap rotateBody(Bitmap _bitmap, int _size, int _i) {
        Matrix matrix = new Matrix();
        Point scale = getBodyScale(_i);
        matrix.preScale(scale.x, scale.y);
        matrix.preRotate(getBodyAngle(_i));

        return Bitmap.createBitmap(_bitmap, 0, 0, _size, _size, matrix, true);
    }

    private float getHeadAngle() {
        switch (directionOfTravel) {
            case UP:
            case DOWN:
                return -90;
        }

        return 0;
    }

    private Point getHeadScale() {
        switch (directionOfTravel) {
            case DOWN:
                return new Point(1, -1);
            case LEFT:
                return new Point(-1, 1);
        }

        return new Point(1, 1);
    }

    private Bitmap rotateHead(Bitmap _bitmap, int _size) {
        Matrix matrix = new Matrix();
        Point scale = getHeadScale();
        matrix.preScale(scale.x, scale.y);
        matrix.preRotate(getHeadAngle());
        return Bitmap.createBitmap(_bitmap, 0, 0, _size, _size, matrix, true);
    }

    void setNewDirection(MotionEvent motionEvent) {
        int newDirection = directionOfTravel.value + (
            (motionEvent.getX() >= GameActivity.instance.screenSize.x / 2)
        ?   1
        :   -1
        );

        if(newDirection == 4) {
            newDirection = 0;
        }

        if(newDirection == -1) {
            newDirection = 3;
        }

        directionOfTravel = SnakeView.Directions.fromInt(newDirection);
    }

    boolean onCollisionSelf() {
        boolean isDead = false;
        for (int i = length - 1; i > 0; i--) {
            if ((i > 4)
            &&  (positions[0].x == positions[i].x)
            &&  (positions[0].y == positions[i].y)) {
                isDead = true;
                break;
            }
        }

        return isDead;
    }
}
