package com.guireadergui;

import java.io.File;

public abstract class Executioner {
    boolean triggered;

    protected Executioner() {
        this.triggered = false;

    }

    public void trigger(){
        this.triggered = true;
    }

    public void reset(){
        this.triggered = false;
    }
    public void setFile(File file){

    }
}
