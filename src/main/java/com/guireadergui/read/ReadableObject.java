package com.guireadergui.read;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class ReadableObject extends CaptureScreen {
    private final ArrayList<Probe> probes;
    private BufferedImage img;
    private int[] onVal = new int[3];
    private int resilience;


    protected ReadableObject(int x, int y, int width, int height, int resilience){
        super(x,y,width,height);
        probes = new ArrayList<>();
        img = super.getImg();
        this.resilience = resilience;


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

    public List<Probe> getProbes() {
        return probes;
    }

    @Override
    public BufferedImage getImg() {
        return img;
    }

    public int getResilience() {
        return resilience;
    }

    public void setResilience(int resilience) {
        this.resilience = resilience;
    }

    public void initOnVal(){
        updateProbes();
        this.onVal = getProbes().get(0).getRGBArray();
    }

    public List<BufferedImage> getImages() {
        ArrayList<BufferedImage> arr = new ArrayList<>();
        arr.add(img);
        return arr;
    }



}
