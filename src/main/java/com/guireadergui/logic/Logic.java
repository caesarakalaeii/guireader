package com.guireadergui.logic;


public abstract class Logic implements LogicListener{
    Logic comp;
    ReadableObjManager man;
    int threshold;
    LogicEnum logicType;


    protected Logic(ReadableObjManager man, int threshold, LogicEnum logicType, Logic comp){
        this.threshold = threshold;
        this.logicType = logicType;
        this.comp = comp;
        this.man = man;
        man.attach(this);
    }



    public boolean compute(){
        switch(logicType){
            case EQUAL: return computeEquals();
            case GREATER: return computeGreater();
            case SMALLER: return computeSmaller();
        }
        return false;
    }
    public LogicEnum getLogicType() {
        return logicType;
    }

    public void setLogicType(LogicEnum logicType) {
        this.logicType = logicType;
    }
    public void setThreshold(int threshold){
        this.threshold=threshold;
    }


    public boolean computeSmaller(){
        return true;
    }

    public boolean computeGreater(){
        return true;
    }

    public boolean computeEquals(){
        return true;
    }


}
