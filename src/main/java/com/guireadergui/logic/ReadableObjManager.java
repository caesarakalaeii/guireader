package com.guireadergui.logic;

import com.guireadergui.read.ReadableObject;

public class ReadableObjManager extends Manager{


    public ReadableObjManager(ReadableObject ref){
        super();
        this.objects.add(ref);
        stop = false;
    }





    public int[] getOnVal(int index) {
        return objects.get(index).getOnVal();
    }


}
