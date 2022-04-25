package com.guireadergui;

import java.util.ArrayList;

public class LogicFactory {

    public static Logic newInstance(LogicType type, LogicEnum logicEnum, ReadableObjManager man, int threshold, Logic logic) {
        if(logic == null){logic = new BasicLogic();}
        switch (type){
            case NUMBER: return new NumberLogic(logic, man, threshold, logicEnum);
            case PERCENTAGE: return new PercentageLogic(logic,man, threshold, logicEnum);
        }
        return new BasicLogic();

    }

    public static Logic newInstance(ArrayList<LogicType> types, ArrayList<LogicEnum> logicEnums,ArrayList<ReadableObjManager> managers, ArrayList<Integer> thresholds){
         Logic logic = null;
        for(ReadableObjManager man : managers){
            logic = newInstance(types.get(managers.indexOf(man)), logicEnums.get(managers.indexOf(man)), managers.get(managers.indexOf(man)), thresholds.get(managers.indexOf(man)), logic);
        }

        return logic;
    }
}
