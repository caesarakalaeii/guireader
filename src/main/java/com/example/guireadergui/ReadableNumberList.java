package com.example.guireadergui;
import java.util.ArrayList;

public class ReadableNumberList { //TODO basically exactly as ReadableBar, might be shit
    ArrayList<ReadableNumber> nums;

    public ReadableNumberList(){
        nums=new ArrayList();
    }
    public void updateBars(){
        for(ReadableNumber n : nums){
            n.updateProbes();
        }
    }
    public void add(ReadableNumber n){
        nums.add(n);
    }
    public ReadableNumber get(int i){
        return nums.get(i);
    }

    public ArrayList<ReadableNumber> getBarList() {
        return nums;
    }

    public int[][] getValues(){
        int[][] values = new int[nums.size()][3];
        for(ReadableNumber n : nums){
            values[nums.indexOf(n)] = n.getProbeValues();
        }
        return values;
    }
    public int[] getValuesMono(String color){

        int[] values = new int[nums.size()];
        for(ReadableNumber n : nums){
            for(Probe p : n.getProbes()) {
                values[nums.indexOf(n)] = p.getMono(color);
            }
        }
        return values;
    }
}

