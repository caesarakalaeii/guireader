package com.guireadergui;

public class BasicLogic extends Logic{

    public BasicLogic(int threshold, LogicEnum logicType) {
        super(threshold, logicType);
    }
    public BasicLogic(){
        super(0, LogicEnum.EQUAL);
    }

    @Override
    public boolean compute() {
        return true;
    }
}
