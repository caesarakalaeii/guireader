package com.guireadergui;

import java.awt.image.BufferedImage;
import java.lang.invoke.WrongMethodTypeException;
import java.util.ArrayList;
import java.util.List;

public abstract class Manager {
    List<LogicListener> l = new ArrayList<>();
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
    public List<LogicListener> getList(){
        return l;
    }

    public void setList(List<LogicListener> l) {
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

    public List<Probe> getProbes(){
        return ref.getProbes();
    }

    public BufferedImage getImage() {
        update();
        return ref.getImg();
    }

}
