package com.example.guireadergui;

import java.awt.image.BufferedImage;

public class Probe {
    private int x;
    private int y;
    private int pixelValue;

    public Probe(int x, int y){
        this.x = x;
        this.y = y;
        this.pixelValue = 0;
    }
    public int getProbeRGB(BufferedImage img){
        return img.getRGB(this.x, this.y);
    }
    public void updatePixelValue(BufferedImage img){
        pixelValue = getProbeRGB(img);
    }

    public int getPixelValue() {
        return pixelValue;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int[] getRGBArray(){
        int array[] = new int[3];
        array[0] = pixelValue & 0xff;
        array[1] = (pixelValue & 0xff00) >> 8;
        array[2] = (pixelValue & 0xff0000) >> 16;
        return array;

    }
    public int getMono(String color){

        if(color.compareTo("red") == 0){
            return pixelValue & 0xff;
        } else if (color.compareTo("green") == 0){
            return (pixelValue & 0xff00) >> 8;
        }
        else {
            return (pixelValue & 0xff0000) >> 16;
        }
    }

}
