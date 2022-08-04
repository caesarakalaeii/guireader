package com.guireadergui.show;

import java.awt.image.BufferedImage;

public class NumberRectangle extends ModRectangle {
    public NumberRectangle(int x, int y, int width,int height ) {
        super(x,y,width, height);
    }

    @Override
    public BufferedImage draw(BufferedImage copy, int color) {
        try{
        int x = (int)super.getX();
        int y = (int) super.getY();
        int width = (int)super.getWidth();
        int height = (int) super.getHeight();

        copy.setRGB(x+(width/2)     ,y                ,color);                   // --0--
        copy.setRGB(x                 ,y+(height/5)   ,color);                   // 1   |
        copy.setRGB(x+width         ,y+(height/5)   ,color);                   // |   2
        copy.setRGB(x+(width/2)     ,y+(height/2)   ,color);                   // --3--
        copy.setRGB(x                 ,y+(4*height/5) ,color);                   // 4   |
        copy.setRGB(x+width         ,y+(4*height/5) ,color);                   // |   5
        copy.setRGB(x+(width/2)     ,y+height       , color);                   // --6--

        for(int i = 0; i<width;i++) { //besser über die Probes der ReadableObj
            copy.setRGB(x + i, y-1, color);
            copy.setRGB(x + i, y+1 + height, color);
        }
        for(int i =0; i<height;i++) {

            copy.setRGB(x-1, y + i, color);
            copy.setRGB(x+1 + width, y + i, color);
        }}
        catch(ArrayIndexOutOfBoundsException e){
            //just do nothing
        }

        return copy;
    }
}
