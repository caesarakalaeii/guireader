package com.guireadergui;

import java.io.File;

public abstract class ExecutionerDecorator extends Executioner{
    Executioner ref;

    public ExecutionerDecorator(Executioner ref){
        super();
        if (ref == null){
            ref = new BasicExecutioner();
        }
        this.ref = ref;
    }
    @Override
    public void trigger(){
        ref.trigger();
    }

    @Override
    public void reset(){
        super.reset();
        ref.reset();
    }




}
