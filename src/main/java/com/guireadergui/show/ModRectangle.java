package com.guireadergui.show;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;

public abstract class  ModRectangle extends Rectangle {
    IntegerProperty resolution;

    protected ModRectangle(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.resolution = new SimpleIntegerProperty();
    }

    /**
     *
     * @param color will be used at a later point
     */
    public BufferedImage draw(BufferedImage copy, int color){

        return copy;
    }

    public IntegerProperty resuProperty() {
        return resolution;
    }
}
