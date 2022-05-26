package com.guireadergui;

import java.io.File;

public abstract class Executioner {
    boolean triggered;

    public Executioner() {
        this.triggered = false;

    }

    public void trigger(){

    }

    public void reset(){
        triggered = false;
    }
    public void setFile(File file){

    }
}
