package com.example.guireadergui;
public class ReadableEventHandler {
    private String path;
    private String[] type;
    private int threshold;
    private ReadableBar bar;
    private ReadableNumber num;

    public ReadableEventHandler(){

    }

    public ReadableEventHandler(String path, String[] type, int threshold ){
        this.path = path;
        this.type = type;
        this.threshold = threshold;
    }


    public ReadableEventHandler(ReadableBar bar,String path, String[] type, int threshold ){
        this();
        this.bar = bar;

    }
    public ReadableEventHandler(ReadableNumber num,String path, String[] type, int threshold ){
        this();
        this.num = num;
    }

    public void onEvent(){
        //TODO execute file in Path, so meanwhile print
        System.out.println("Event, wohoooo");
    }

    public void checkEvent(){
        if(type[0].equalsIgnoreCase("value_threshold")){ //TODO might be a better way using enums
            if(type[1].equalsIgnoreCase("below")){
                if(bar.getPercentage()<threshold){
                    onEvent();
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public ReadableBar getBar() {
        return bar;
    }

    public void setBar(ReadableBar bar) {
        this.bar = bar;
    }

    public ReadableNumber getNum() {
        return num;
    }

    public void setNum(ReadableNumber num) {
        this.num = num;
    }
}
