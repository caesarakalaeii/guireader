package com.guireadergui;

import java.awt.image.BufferedImage;
import java.lang.invoke.WrongMethodTypeException;
import java.util.ArrayList;

public abstract class Manager {
    ArrayList<LogicListener> l = new ArrayList();
    ReadableObject ref;

    public void attach(LogicListener listner){
        l.add(listner);
    }

    public void deattach(LogicListener listner){
        l.remove(listner);
    }
    public void update(){
        ref.updateProbes();
        for(LogicListener list : l){
            list.update();
        }
    }
    public ArrayList<LogicListener> getList(){
        return l;
    }

    public void setList(ArrayList<LogicListener> l) {
        this.l = l;
    }

    public ReadableObject getRef() {
        return ref;
    }

    public void setRef(ReadableObject ref) {
        this.ref = ref;
    }

    public int getPercentage() {
        for (LogicListener list: l){
            if(list.getClass() == PercentageLogic.class){
                return list.getValue();
            }
        }
        throw new WrongMethodTypeException("No Logic with this method; getPercentage()");
    }

    public ArrayList<Probe> getProbes(){
        return ref.getProbes();
    }

    public BufferedImage getImage() {
        update();
        return ref.getImg();
    }

    public void stop(){
        ref.cancel();
    }
}
