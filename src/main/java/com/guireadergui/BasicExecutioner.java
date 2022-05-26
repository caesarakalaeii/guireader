package com.guireadergui;

public class BasicExecutioner extends Executioner {
    public BasicExecutioner(){
        super();
    }

    @Override
    public void trigger(){
        if(!triggered) {
            super.trigger();
            System.out.println("Triggered BasicExecutioner");
            return;
        }
        System.out.println("Didn't trigger BasicExecutioner");

    }
}
