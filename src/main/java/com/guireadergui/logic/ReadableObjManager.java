package com.guireadergui.logic;

import com.guireadergui.read.ReadableObject;

public class ReadableObjManager extends Manager{


    public ReadableObjManager(ReadableObject ref){
        super();
        this.ref = ref;
        stop = false;
    }





    public int[] getOnVal() {
        return ref.getOnVal();
    }


}
