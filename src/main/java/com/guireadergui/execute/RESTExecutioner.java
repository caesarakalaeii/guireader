package com.guireadergui.execute;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;


public class RESTExecutioner extends Executioner{
    int time = 200; //time in ms, to be added via config

    protected RESTExecutioner(int patience, Executioner ref) {
        super(patience, ref);
    }

    @Override
    public void trigger(){

        if(!triggered && patience<patienceCount){
            post();
        }
        if(ref!=null){ref.trigger();}
        patienceCount++;

    }

    @Override
    public void reset(){
        triggered = false;
        super.reset();
    }
    
    public void post(){
        try {
            triggered = true;
            URL url = new URL("http://localhost:38080/arduino/tens/onfor?t=" + time); //http://localhost:28080/arduino/tens/onfor?t=500 -Method POST
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            http.connect();
            System.out.println("HTTP-Response: " + http.getResponseCode());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        RESTExecutioner p = new RESTExecutioner(0, null);
        p.post();
    }

}
