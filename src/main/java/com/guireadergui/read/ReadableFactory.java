package com.guireadergui.read;


public class ReadableFactory {

    private ReadableFactory(){}



    public static ReadableObject newInstance(ReadableEnum type, int x, int y, int width, int height, int resilience, int resolution) {
        if (type == ReadableEnum.BAR) {
            return new ReadableBar(x, y, width, height, resilience, resolution);
        }
        if (type == ReadableEnum.NUMBER) {
            return new ReadableNumber(x, y, width, height, resilience);
        }
        return null;
    }
}

