package com.hrily.zbot.utils;

/**
 * Created by hrishi on 16/6/17.
 */

public class Point {

    public int x, y;

    public Point() {
        this.x = this.y = 0;
    }

    public Point(int x, int y) {
        this.setLocation(x, y);
    }

    public Point(Point p){
        this.setLocation(p);
    }

    public void setLocation(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setLocation(double x, double y){
        this.x = (int) x;
        this.y = (int) y;
    }

    public void setLocation(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    public void move(int x, int y){
        setLocation(x, y);
    }

    public void translate(int dx, int dy){
        x += dx;
        y += dy;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
