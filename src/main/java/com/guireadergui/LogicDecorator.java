package com.guireadergui;

public abstract class LogicDecorator extends Logic implements LogicListener{
    Logic comp;
    ReadableObjManager man;

    public LogicDecorator(Logic comp, ReadableObjManager man, int threshold, LogicEnum logicType){
        super(threshold, logicType);
        this.comp = comp;
        this.man = man;
        man.attach(this);
    }

    @Override

    public boolean compute(){
        switch(logicType){
            case EQUAL: return computeEquals()&&comp.compute();
            case GREATER: return computeGreater()&&comp.compute();
            case SMALLER: return computeSmaller()&&comp.compute();
        }
        return false;
    }





}
