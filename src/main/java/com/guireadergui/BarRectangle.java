package com.guireadergui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Rectangle;


public class BarRectangle extends Rectangle {
    IntegerProperty resolution;

    public BarRectangle(int x, int y, int width, int height){
        super(x, y, width, height);
        this.resolution = new SimpleIntegerProperty();


    }

    public int getResolution(){
        return resolution.get();
    }

    public IntegerProperty resuProperty(){
        return resolution;
    }



}

