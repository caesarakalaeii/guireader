package com.guireadergui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class ReadableObject extends CaptureScreen {
    private final ArrayList<Probe> probes;
    private BufferedImage img;
    private int[] onVal = new int[3];
    private int ofVal;
    private int resiliance;


    public ReadableObject(int x, int y, int width, int height, int resilience){
        super(x,y,width,height);
        probes = new ArrayList<>();
        img = super.getImg();
        this.resiliance = resilience;


    }


    public void updateProbes(){
        img = super.getImg();
        for(Probe probe : probes){
            probe.updatePixelValue(img);
        }
    }
    public void updateSingleProbe(int index){
        img = super.getImg();
        probes.get(index).updatePixelValue(img);
    }
    public int[] getProbeValues(){
        updateProbes();
        int[] values = new int[probes.size()];
        for(Probe p :probes){
            values[probes.indexOf(p)] = p.getPixelValue();
        }
        return values;
    }
    public void setOnVal(int[] onVal){
        this.onVal = onVal;
    }

    public int[] getOnVal(){
        return onVal;
    }

    public ArrayList<Probe> getProbes() {
        return probes;
    }

    @Override
    public BufferedImage getImg() {
        return img;
    }

    public int getResiliance() {
        return resiliance;
    }

    public void setResiliance(int resiliance) {
        this.resiliance = resiliance;
    }

    public void initOnVal(){
        updateProbes();
        this.onVal = getProbes().get(0).getRGBArray();
    }

    public ArrayList<BufferedImage> getImages() {
        ArrayList<BufferedImage> arr = new ArrayList();
        arr.add(img);
        return arr;
    }

    @Override
    protected Object call() throws Exception{
        super.call();
        return null;
    }
}
