package com.guireadergui;

import java.util.ArrayList;

public class ReadableNumber extends ReadableObject{
    ArrayList<Probe> probes;

    public ReadableNumber(int x, int y, int width, int height, int resilience){
        super(x, y, width, height, resilience);
        //TODO 7-Segment-Nummern über GUI festlegen
        probes.add(new Probe(x,width/2));                            // --0--
        probes.add(new Probe(x/3, y));                               // 1   |
        probes.add(new Probe(x/3, y+width));                       // |   2
        probes.add(new Probe(x+width/2, y+height/2));              // --3--
        probes.add(new Probe(2*x/3, y));                             // 4   |
        probes.add(new Probe(2*x/3, y+width));                     // |   5
        probes.add(new Probe(x,width/2));                            // --6--
        super.setOnVal(probes.get(0).getPixelValueArr());

    }
    public ReadableNumber(int[] args){
        this(args[0],args[1],args[2],args[3],args[4]);
    }




    public void update() {
        updateProbes();
    }


}
