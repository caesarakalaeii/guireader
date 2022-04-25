package com.guireadergui;

import java.util.Arrays;

public class NumberLogic extends LogicDecorator{

    public NumberLogic(Logic comp, ReadableObjManager man, int threshold, LogicEnum logicEnum) {
        super(comp, man, threshold, logicEnum);
    }


    @Override
    public void update() {

    }

    @Override
    public int getValue(){
        if(!checker(0)){
            if(!checker(1)){
                return 1;
            }
            return 4;
        }
        if(!checker(3)){
            if(!checker(6)){
                return 7;
            }
            return 0;
        }
        if(!checker(1)){
            if(!checker(5)){
                return 2;
            }
            return 3;
        }
        if(!checker(2)){
            if(!checker(4)){
                return 6;
            }
            return 5;
        }
        if(!checker(4)){
            return 9;
        }
        if(checker(5)) {
            return 8; //TODO might still not be an 8
        }
        throw new ArithmeticException("7 segment analysis failure, result not a number");

    }

    public boolean checker(int check){
        return  Arrays.equals(man.getProbes().get(check).getRGBArray(), man.getOnVal());
    }
}
