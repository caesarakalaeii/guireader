package com.example.guireadergui;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class ReadableObject extends CaptureScreen {
    private ArrayList<Probe> probes;
    private BufferedImage img;
    private ReadableEventHandler h;

    public ReadableObject(int x, int y, int width, int height){
        super(x,y,width,height);
        probes = new ArrayList<>();
        img = getImage();
        h = new ReadableEventHandler();

    }


    public void updateProbes(){
        img = getImage();
        for(Probe probe : probes){
            probe.updatePixelValue(img);
        }
    }
    public void updateSingleProbe(int index){
        img = getImage();
        probes.get(index).updatePixelValue(img);
    }
    public int[] getProbeValues(){
        updateProbes();
        int values[] = new int[probes.size()];
        for(Probe p :probes){
            values[probes.indexOf(p)] = p.getPixelValue();
        }
        return values;
    }

    public ArrayList<Probe> getProbes() {
        return probes;
    }


    public BufferedImage getImg() {
        return img;
    }

    public ReadableEventHandler getHandler(){
        return h;
    }
}
