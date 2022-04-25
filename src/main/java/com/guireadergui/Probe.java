package com.guireadergui;

import java.awt.image.BufferedImage;

public class Probe {
    private final int x;
    private final int y;
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
        int[] array = new int[3];
        array[0] = pixelValue & 0xff;
        array[1] = (pixelValue & 0xff00) >> 8;
        array[2] = (pixelValue & 0xff0000) >> 16;
        return array;

    }
    public static int[] getRGBArray(BufferedImage img, int x, int y){
        int val = img.getRGB(x,y);
        int[] array = new int[3];
        array[0] = val & 0xff;
        array[1] = (val & 0xff00) >> 8;
        array[2] = (val & 0xff0000) >> 16;
        return array;
    }

    public static int[] getRGBArray(BufferedImage img, Probe probe){
        return getRGBArray(img, probe.getX(), probe.getY());
    }

    public int getMono(ColorEnum color){
        switch (color){
        case RED: return pixelValue & 0xff;
        case GREEN: return (pixelValue & 0xff00) >> 8;
        case BLUE: return (pixelValue & 0xff0000) >> 16;
        }
        return 0; // can't be reached, but java likes it
    }

}
