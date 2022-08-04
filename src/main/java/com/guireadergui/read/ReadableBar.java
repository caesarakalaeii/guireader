package com.guireadergui.read;


import java.awt.image.BufferedImage;

public class ReadableBar extends ReadableObject{


    public ReadableBar(int x, int y, int width, int height,int resilience,  int res){
        super(x, y, width, height, resilience);

        if(height>width){
            int middle = width/2;
            int interval = height/res;

            for(int i = 0; i<res;i++){
                Probe probe = new Probe(middle,(interval * i));
                getProbes().add(probe);
            }
        }  //in case of vertical bar
        if(height<width){
            int middle = height/2;
            int interval = width/res;
            for(int i = 0; i<res;i++){
                Probe probe = new Probe((interval * i),middle);
                getProbes().add(probe);
            }

        }  // in case of horizontal bar
        updateProbes();
        }

    public ReadableBar(int[] args) {
        this(args[0], args[1], args[2], args[3], args[4], args[5]);
    }


    public BufferedImage getUpdate(){
        updateProbes();
        return getImg();
    }

    public void update() {
        updateProbes();
    }


}

