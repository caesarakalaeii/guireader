package com.guireadergui.execute;

import java.io.File;

public abstract class Executioner {
    boolean triggered;
    int patienceCount;
    Executioner ref;
    int patience;

    protected Executioner(int patience, Executioner ref) {
        this.ref = ref;
        this.triggered = false;
        this.patienceCount =0;
        this.patience = patience;

    }


    public void trigger(){
        this.triggered = true;
    }

    public void reset(){
        this.patienceCount =0;
        this.triggered = false;
    }
    public void setFile(File file){

    }

    protected int getPatienceCount() {
        return patienceCount;
    }

    public void setPatience(int pat){
        patience=pat;
    }

}
