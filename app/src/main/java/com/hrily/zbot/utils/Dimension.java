package com.hrily.zbot.utils;

import java.util.Objects;

/**
 * Created by hrishi on 16/6/17.
 */

public class Dimension {

    public int height, width;

    public Dimension() {
        this.width = this.height = 0;
    }

    public Dimension(int width, int height) {
        setSize(width, height);
    }

    public Dimension(Dimension dimension){
        setSize(dimension);
    }

    @Override
    public boolean equals(Object o){
        if(o.getClass()!=this.getClass())
            return false;
        Dimension d = (Dimension) o;
        return  this.height == d.height &&
                this.width  == d.width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setSize(Dimension dimension){
        this.height = dimension.height;
        this.width  = dimension.width;
    }

    public void setSize(int width, int height){
        this.height = height;
        this.width  = width;
    }

    @Override
    public String toString() {
        return "Dimension(" + String.valueOf(width)
                + ", " + String.valueOf(height) + ")";
    }
}
