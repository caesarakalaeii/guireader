package com.guireadergui.logic;

import java.util.Arrays;

public class NumberLogic extends Logic{

    public NumberLogic(ReadableObjManager man, int threshold, LogicEnum logicEnum, Logic comp) {
        super( man, threshold, logicEnum, comp);
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
                return 5;
            }
            return 6;
        }
        if(!checker(4)){
            return 9;
        }
        if(checker(5)) {
            return 8; //might still not be an 8
        }

        throw new ArithmeticException("7 segment analysis failure, result not a number");

    }

    public boolean checker(int check){
        return  Arrays.equals(man.getProbes().get(check).getRGBArray(), man.getOnVal());
    }

    @Override
    public boolean computeSmaller(){return threshold > getValue();
    }

    @Override
    public boolean computeGreater(){
        return threshold < getValue();
    }

    @Override
    public boolean computeEquals(){
        return threshold == getValue();
    }

}
