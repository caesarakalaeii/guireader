package com.guireadergui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class ReadableService extends Service<Image> {
    ReadableType type;
    int x;
    int y;
    int width;
    int height;
    int resilience;
    int resolution;


    public ReadableService(ReadableType type, int x, int y, int width, int height, int resilience, int resolution){
        if(type != ReadableType.BAR){
            throw new IllegalArgumentException("Can't create Service with these Arguments, wrong type or argument provided");
        }
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.resilience = resilience;
        this.resolution = resolution;


    }
    public ReadableService(ReadableType type, int x, int y, int width, int height, int resilience){
        if(type != ReadableType.NUMBER){
            throw new IllegalArgumentException("Can't create Service with these Arguments, wrong type or argument provided");
        }
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.resilience = resilience;

    }


    @Override
    protected Task createTask() {
        if (type == ReadableType.BAR){
            return new ReadableBar(x,y,width,height,resilience,resolution);
        }
        if (type == ReadableType.NUMBER){
            return new ReadableNumber(x,y,width,height,resilience);
        }
        return null;

    }

    public ReadableObject createReadableTask() {
        if (type == ReadableType.BAR){
            return new ReadableBar(x,y,width,height,resilience,resolution);
        }
        if (type == ReadableType.NUMBER){
            return new ReadableNumber(x,y,width,height,resilience);
        }
        return null;
    }

}
