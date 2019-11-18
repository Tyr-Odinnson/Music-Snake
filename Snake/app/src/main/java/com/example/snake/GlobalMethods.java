package com.example.snake;

import android.graphics.Point;

public final class GlobalMethods {
    public static Point subtract(Point p1, Point p2) {
        return new Point(p1.x - p2.x, p1.y - p2.y);
    }
}
