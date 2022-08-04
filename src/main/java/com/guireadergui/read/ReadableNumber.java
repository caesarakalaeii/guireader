package com.guireadergui.read;

import org.jetbrains.annotations.NotNull;


public class ReadableNumber extends ReadableObject{

    public ReadableNumber(int x, int y, int width, int height, int resilience){
        super(x, y, width, height, resilience);



        getProbes().add(new Probe((width/2)     ,1));                            // --0--
        getProbes().add(new Probe(1           ,(height/5)));                               // 1   |
        getProbes().add(new Probe(width-1     ,(height/5)));                       // |   2
        getProbes().add(new Probe((width/2)     ,(height/2)));              // --3--
        getProbes().add(new Probe(1           ,(4*height/5)));                             // 4   |
        getProbes().add(new Probe(width-1     ,(4*height/5)));                     // |   5
        getProbes().add(new Probe((width/2)     ,height-1));                            // --6--
        updateProbes();
    }
    public ReadableNumber(int @NotNull [] args){
        this(args[0],args[1],args[2],args[3],args[4]);
    }




    public void update() {
        updateProbes();
    }


}
