package com.guireadergui;

public class ReadableObjManager extends Manager{


    public ReadableObjManager(ReadableObject ref){
        super();
        this.ref = ref;

    }

    @Override
    public void update() {
        super.update();
    }


    public int[] getOnVal() {
        return ref.getOnVal();
    }


}
