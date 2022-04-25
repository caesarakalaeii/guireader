package com.guireadergui;

public abstract class Logic {
    int threshold;
    LogicEnum logicType;

    public Logic(int threshold, LogicEnum logicType){
        this.threshold = threshold;
        this.logicType = logicType;
    }

    public LogicEnum getLogicType() {
        return logicType;
    }

    public void setLogicType(LogicEnum logicType) {
        this.logicType = logicType;
    }

    public boolean compute(){
        switch(logicType){
            case EQUAL: return computeEquals();
            case GREATER: return computeGreater();
            case SMALLER: return computeSmaller();
        }
        return false;
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

    public void setThreshold(int threshold){
        this.threshold=threshold;
    }
}
