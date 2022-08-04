package com.guireadergui.logic;


import com.guireadergui.read.Probe;

public class PercentageLogic extends Logic {


    public PercentageLogic(ReadableObjManager man, int threshold, LogicEnum logicType, Logic comp) {
        super( man, threshold, logicType, comp);
    }




    @Override
    public int getValue() {

        int numProbes = man.getProbes().size();
        int i=0;
        int res = man.getRef().getResilience();
        int[] onVal = man.getOnVal();
        for(Probe p : man.getProbes()){
            int[] currentVal = p.getRGBArray();
            if(currentVal[0] <= onVal[0]+res &&
                    currentVal[0] >= onVal[0]-res &&
                    currentVal[1] <= onVal[1]+res &&
                    currentVal[1] >= onVal[1]-res &&
                    currentVal[2] <= onVal[2]+res &&
                    currentVal[2] >= onVal[2]-res){
                i++;
            }
        }

        return (i*100)/numProbes;
    }


    @Override
    public boolean computeSmaller(){
        return threshold > getValue();
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
