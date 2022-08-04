package com.guireadergui.show;
import java.awt.image.BufferedImage;


public class BarRectangle extends ModRectangle {

    public BarRectangle(int x, int y, int width, int height){
        super(x,y,width, height);
    }


   @Override
    public BufferedImage draw(BufferedImage copy, int color){
        try {
            int width = (int) super.getWidth();
            int height = (int) super.getHeight();
            int x = (int) super.getX();
            int y = (int) super.getY();
            int res = resolution.get();


            for (int i = 0; i < width; i++) { //besser über die Probes der ReadableObj
                copy.setRGB(x + i, y, color);
                copy.setRGB(x + i, y + height, color);
            }
            for (int i = 0; i < height; i++) {

                copy.setRGB(x, y + i, color);
                copy.setRGB(x + width, y + i, color);
            }


            if (height >= width) {
                int middle = width / 2;
                int interval = height / res;

                for (int i = 0; i < res; i++) {
                    copy.setRGB(x + middle, y + (interval * i), color);
                }
            }  //in case of vertical bar
            if (height < width) {
                int middle = height / 2;
                int interval = width / res;
                for (int i = 0; i < res; i++) {
                    copy.setRGB(x + (interval * i), y + middle, color);
                }

            }
        }
        catch(ArrayIndexOutOfBoundsException e){
            //just do nothing
        }
        return copy;
    }
}

