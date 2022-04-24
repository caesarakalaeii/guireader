package com.example.guireadergui;
import java.util.ArrayList;

public class ReadableNumber extends ReadableObject{
    ArrayList<Probe> probes;
    private int onVal;
    private int ofVal;

    public ReadableNumber(int x, int y, int width, int height){
        super(x, y, width, height);
        //TODO 7-Segment-Nummern über GUI festlegen
        probes.add(new Probe(x+5,width/2));                         // --0--
        probes.add(new Probe(x/3, y+5));                            // 1   |
        probes.add(new Probe(x/3, y+width-5));                      // |   2
        probes.add(new Probe(x+width/2, y+height/2));               // --3--
        probes.add(new Probe(2*x/3, y+5));                          // 4   |
        probes.add(new Probe(2*x/3, y+width-5));                    // |   5
        probes.add(new Probe(x+5,width/2));                         // --6--
        onVal = probes.get(0).getPixelValue();

    }

    public int getNumber(){
        if(eins()){
            return 1;
        }
        else if(zwei()){
            return 2;
        }
        else if(drei()){
            return 3;
        }
        else if(vier()){
            return 4;
        }
        else if(funf()){
            return 5;
        }
        else if(sechs()){
            return 6;
        }
        else if(sieben()){
            return 7;
        }
        else if(acht()){
            return 8;
        }
        else if(neun()){
            return 9;
        }
        else return 0;
    }

    public boolean eins(){
        if(probes.get(0).getPixelValue() != onVal &&
                probes.get(1).getPixelValue() != onVal &&
                probes.get(2).getPixelValue() == onVal &&
                probes.get(3).getPixelValue() != onVal &&
                probes.get(4).getPixelValue() != onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() != onVal){
            return true;
        }
        else return false;
    }

    public boolean zwei(){
        if(probes.get(0).getPixelValue() == onVal &&
                probes.get(1).getPixelValue() != onVal &&
                probes.get(2).getPixelValue() == onVal &&
                probes.get(3).getPixelValue() == onVal &&
                probes.get(4).getPixelValue() == onVal &&
                probes.get(5).getPixelValue() != onVal &&
                probes.get(6).getPixelValue() == onVal){
            return true;
        }
        else return false;
    }
    public boolean drei(){
        if(probes.get(0).getPixelValue() == onVal &&
                probes.get(1).getPixelValue() != onVal &&
                probes.get(2).getPixelValue() == onVal &&
                probes.get(3).getPixelValue() == onVal &&
                probes.get(4).getPixelValue() != onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() == onVal){
            return true;
        }
        else return false;
    }
    public boolean vier(){
        if(probes.get(0).getPixelValue() != onVal &&
                probes.get(1).getPixelValue() == onVal &&
                probes.get(2).getPixelValue() == onVal &&
                probes.get(3).getPixelValue() == onVal &&
                probes.get(4).getPixelValue() != onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() != onVal){
            return true;
        }
        else return false;
    }
    public boolean funf(){
        if(probes.get(0).getPixelValue() == onVal &&
                probes.get(1).getPixelValue() == onVal &&
                probes.get(2).getPixelValue() != onVal &&
                probes.get(3).getPixelValue() == onVal &&
                probes.get(4).getPixelValue() != onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() == onVal){
            return true;
        }
        else return false;
    }
    public boolean sechs(){
        if(probes.get(0).getPixelValue() == onVal &&
                probes.get(1).getPixelValue() == onVal &&
                probes.get(2).getPixelValue() != onVal &&
                probes.get(3).getPixelValue() == onVal &&
                probes.get(4).getPixelValue() == onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() == onVal){
            return true;
        }
        else return false;
    }
    public boolean sieben(){
        if(probes.get(0).getPixelValue() == onVal &&
                probes.get(1).getPixelValue() != onVal &&
                probes.get(2).getPixelValue() == onVal &&
                probes.get(3).getPixelValue() != onVal &&
                probes.get(4).getPixelValue() != onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() != onVal){
            return true;
        }
        else return false;
    }
    public boolean acht(){
        if(probes.get(0).getPixelValue() == onVal &&
                probes.get(1).getPixelValue() == onVal &&
                probes.get(2).getPixelValue() == onVal &&
                probes.get(3).getPixelValue() == onVal &&
                probes.get(4).getPixelValue() == onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() == onVal){
            return true;
        }
        else return false;
    }
    public boolean neun(){
        if(probes.get(0).getPixelValue() == onVal &&
                probes.get(1).getPixelValue() == onVal &&
                probes.get(2).getPixelValue() == onVal &&
                probes.get(3).getPixelValue() == onVal &&
                probes.get(4).getPixelValue() != onVal &&
                probes.get(5).getPixelValue() == onVal &&
                probes.get(6).getPixelValue() == onVal){
            return true;
        }
        else return false;
    }




}
