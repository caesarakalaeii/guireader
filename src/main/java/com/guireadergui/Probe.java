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

    public int[] getPixelValueArr() {
        return getRGBArray(pixelValue);
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
        return getRGBArray(img.getRGB(x,y));
    }

    public static int[] getRGBArray(int rgb){
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        int[] array = new int[3];
        array[0] = red;
        array[1] = green;
        array[2] = blue;
        return array;
    }

    public static int[] getRGBArray(BufferedImage img, Probe probe){
        return getRGBArray(img, probe.getX(), probe.getY());
    }

    public static int toRGBint(int[] rgbArray){
        int rgb = ((rgbArray[0]&0x0ff)<<16)|((rgbArray[1])<<8)|(rgbArray[2]&0x0ff);
        System.out.println("RGB soll: R"+ rgbArray[0]+ ", G"+ rgbArray[1] + ", B"+ rgbArray[2] );
        System.out.println("RGB ist: R"+ getRGBArray(rgb)[0]+ ", G"+ getRGBArray(rgb)[1] + ", B"+ getRGBArray(rgb)[2] );
        return rgb;
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
