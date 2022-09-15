package com.guireadergui.logic;


public class LogicFactory {

    private LogicFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Logic newInstance(LogicType type, LogicEnum logicEnum, ReadableObjManager man, int threshold, Logic logic) {

        return switch (type) {
            case NUMBER -> new NumberLogic(man, threshold, logicEnum, logic);
            case PERCENTAGE -> new PercentageLogic(man, threshold, logicEnum, logic);
            default -> throw new UnsupportedOperationException("Type not Implemented");
        };


    }
}