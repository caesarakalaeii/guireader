package com.guireadergui;

import java.awt.image.BufferedImage;

public class PercentageLogic extends LogicDecorator {


    public PercentageLogic(Logic comp, ReadableObjManager man, int threshold, LogicEnum logicType) {
        super(comp, man, threshold, logicType);
    }


    @Override
    public void update() {

    }

    @Override
    public int getValue() {

        int numProbes = man.getProbes().size();
        int i=0;
        int res = man.getRef().getResiliance();
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
                //System.out.println("Found Equal");
            }
            //System.out.println("Pixelval:"+ currentVal[0]);
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
