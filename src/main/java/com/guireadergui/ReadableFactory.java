package com.guireadergui;


public class ReadableFactory {



    public static ReadableObject newInstance(ReadableType type, int x, int y, int width, int height, int resilience, int resolution) {
        if (type == ReadableType.BAR) {
            return new ReadableBar(x, y, width, height, resilience, resolution);
        }
        if (type == ReadableType.NUMBER) {
            return new ReadableNumber(x, y, width, height, resilience);
        }
        return null;
    }
}

