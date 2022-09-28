package com.guireadergui.logic;


import com.guireadergui.execute.Executioner;
import com.guireadergui.read.Probe;
import com.guireadergui.read.ReadableObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Manager extends Thread {
    List<LogicListener> l = new ArrayList<>();
    List<ReadableObject> objects = new ArrayList<>();
    Executioner e;
    boolean stop;
    ReadableObject temp;
    ReadableObject tempR;
    LogicListener tempList;
    LogicListener tempListR;

    public void attach(LogicListener listener){
        tempList =listener;
    }

    public void deattach(LogicListener listener){
        tempListR =listener;
        tempR = objects.get(l.indexOf(listener));
    }

    public void deattach(int index){
        tempListR = l.get(index);
        tempR = objects.get(index);
    }
    public void update(){ //if to avoid ConcurrentModificationException
        if(temp != null){
            objects.add(temp);
            temp = null;
        }
        if(tempList != null){
            l.add(tempList);
            tempList = null;
        }
        if(tempR!=null){
            objects.remove(tempR);
            tempR = null;
        }
        if(tempListR != null){
            l.remove(tempListR);
            tempListR = null;

        }
        for(ReadableObject ref: objects) {
            ref.updateProbes();
            if (computeAll() && e != null) {
                    e.trigger();
                }

        }
    }

    public boolean computeAll(){
        boolean prev = true;
        for(LogicListener listener : l){
            prev = listener.compute() && prev;
        }
        return prev;
    }
    public List<LogicListener> getList(){
        return l;
    }


    public ReadableObject getRef(int index) {
        return objects.get(index);
    }




    public List<Probe> getProbes(int index){
        return objects.get(index).getProbes();
    }

    public BufferedImage getImage(int index) {
        update();
        return objects.get(index).getImg();
    }

    public void setE(Executioner e) {
        this.e = e;
    }

    @Override
    public void run(){
        while(!stop){
            update();
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stopThread(){
        stop = true;
    }

    public List<ReadableObject> getObjects(){
        return objects;
    }

    public void newObject(ReadableObject bar) {
        this.temp = bar;
    }
}
