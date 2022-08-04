package com.guireadergui.logic;


import com.guireadergui.execute.Executioner;
import com.guireadergui.read.Probe;
import com.guireadergui.read.ReadableObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Manager extends Thread {
    List<LogicListener> l = new ArrayList<>();
    ReadableObject ref;
    Executioner e;
    boolean stop;

    public void attach(LogicListener listner){
        l.add(listner);
    }

    public void deattach(LogicListener listner){
        l.remove(listner);
    }
    public void update(){
        ref.updateProbes();
        for(LogicListener list : l){
            if(list.compute()){
                e.trigger();
            }
        }
    }
    public List<LogicListener> getList(){
        return l;
    }


    public ReadableObject getRef() {
        return ref;
    }

    public void setRef(ReadableObject ref) {
        this.ref = ref;
    }


    public List<Probe> getProbes(){
        return ref.getProbes();
    }

    public BufferedImage getImage() {
        update();
        return ref.getImg();
    }

    public void setE(Executioner e) {
        this.e = e;
    }

    @Override
    public void run(){
        while(!stop){
            update();
        }
    }

    public void stopThread(){
        stop = true;
    }
}
