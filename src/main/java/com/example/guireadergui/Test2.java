/*package com.example.guireadergui;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import static java.lang.Thread.sleep;


public class Test2 {

    public static void main(String[] args) {

        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            JSONParser parser = new JSONParser();
            ReadableBarList bars = new ReadableBarList();
            Rectangle screenRect = new Rectangle(143,
                    808,
                    191,
                    8);
            Rectangle fullscreenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File("smallcapture.png"));
            BufferedImage fullcapture = new Robot().createScreenCapture(fullscreenRect);

            //ArrayList<ReadableNumber> num = new ArrayList(); //TODO get Number readout via 7segment ana, make numList class
            //JSONArray a = (JSONArray) parser.parse(new FileReader("C:/Users/Caesar/sciebo/Studium Malte/GUIReaderApp/src/main/java/layout.json"));
            //for (Object o : a)
            //{
                //JSONObject readable = (JSONObject) o;
                //if(readable.get("type").toString().equals("bar")) {
                    bars.add(new ReadableBar(143,
                            808,
                            191,
                            8,
                            50));
                    int pullrate = 500;
                //}
                //else{
                //    System.out.println("Could not identify type/ Type is not supported yet");
                //}

            //}
            System.out.println(bars.get(0).getClass());
            ReadableBar bar = bars.getBarList().get(0);
            for(Probe p : bar.getProbes()){
                fullcapture.setRGB(p.getX()+143,p.getY()+808,255);
            }
            fullcapture.setRGB(143,808,255);
            ImageIO.write(fullcapture, "png", new File("fullcapture.png"));
            int [][] oldVal = bars.getValues(); //TODO better run statement, this is shit

            while(true){
                try {
                    bars.updateBars();
                    int [][] newVal = bars.getValues();
                    if(!Arrays.equals(newVal, oldVal)){
                        System.out.println("Change Detected");
                        for(ReadableBar r : bars.getBarList()){
                            System.out.println("Percentage:"+ r.getPercentage());
                        }
                        oldVal = newVal;

                    }

                    sleep(pullrate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }









        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }
}

*/
