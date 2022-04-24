package com.example.guireadergui;


import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ReadableBar extends ReadableObject{
    private int onVal[] = new int[3];
    private int ofVal;

    public ReadableBar(int x, int y, int width, int height, int res){
        super(x, y, width, height);

        if(height>width){
            int middle = width/2;
            int interval = height/res;

            for(int i = 0; i<res;i++){
                Probe probe = new Probe(middle,(interval * i));
                getProbes().add(probe);
            }
        }  //in case of vertical bar
        else{
            int middle = height/2;
            int interval = width/res;
            for(int i = 0; i<res;i++){
                Probe probe = new Probe((interval * i),middle);
                getProbes().add(probe);
            }

            }  // in case of horizontal bar
        updateProbes();
        this.onVal[0] = getProbes().get(0).getMono("red");
        this.onVal[1] = getProbes().get(0).getMono("green");
        this.onVal[2] = getProbes().get(0).getMono("blue");

        }

        public int getPercentage(){
            int numProbes = getProbes().size();
            int i=0;
            for(Probe p : getProbes()){
                int currentVal[] = {p.getMono("red"),p.getMono("green"),p.getMono("blue")};
                if(currentVal[0] <= onVal[0]+10 && currentVal[0] >= onVal[0]-10 && currentVal[1] <= onVal[1]+10 && currentVal[1] >= onVal[1]-10 && currentVal[2] <= onVal[2]+10 && currentVal[2] >= onVal[2]-100){
                    i++;
                    //System.out.println("Found Equal");
                }
                //System.out.println("Pixelval:"+ currentVal[0]);
            }


            return (i*100)/numProbes;
        }

        public BufferedImage getUpdate(){
        updateProbes();
        return getImg();
    }
}

