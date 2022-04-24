package com.example.guireadergui;
import java.util.ArrayList;

public class ReadableBarList {
    ArrayList<ReadableBar> bars;

    public ReadableBarList(){
        bars=new ArrayList();
    }
    public void updateBars(){
        for(ReadableBar b : bars){
            b.updateProbes();
        }
    }
    public void add(ReadableBar b){
        bars.add(b);
    }
    public ReadableBar get(int i){
        return bars.get(i);
    }

    public ArrayList<ReadableBar> getBarList() {
        return bars;
    }

    public int[][] getValues(){
        int[][] values = new int[bars.size()][3];
        for(ReadableBar b : bars){
            values[bars.indexOf(b)] = b.getProbeValues();
        }
        return values;
    }
    public int[] getValuesMono(String color){

        int[] values = new int[bars.size()];
        for(ReadableBar b : bars){
            for(Probe p : b.getProbes()) {
                values[bars.indexOf(b)] = p.getMono(color);
            }
        }
        return values;
    }
}
