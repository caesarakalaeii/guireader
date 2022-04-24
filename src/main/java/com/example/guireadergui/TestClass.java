/*import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.Arrays;

import static java.lang.Thread.sleep;


public class TestClass {

    public static void main(String[] args) {

        //sleep(1000);
        int pullrate = 100;

        try {
            JSONParser parser = new JSONParser();
            ReadableBarList bars = new ReadableBarList();
            //ArrayList<ReadableNumber> num = new ArrayList(); //TODO get Number readout via 7segment ana, make numList class
            JSONArray a = (JSONArray) parser.parse(new FileReader("C:/Users/Caesar/sciebo/Studium Malte/GUIReaderApp/src/main/java/layout.json"));
            for (Object o : a)
            {
                JSONObject readable = (JSONObject) o;
                if(readable.get("type").toString().equals("bar")) {
                    bars.add(new ReadableBar(((Number) readable.get("x")).intValue(), //type conversion is shit, but works
                            ((Number) readable.get("y")).intValue(),
                            ((Number) readable.get("width")).intValue(),
                            ((Number) readable.get("height")).intValue(),
                            ((Number) readable.get("res")).intValue()));
                    pullrate = ((Number) readable.get("pullrate")).intValue();
                }
                else{
                    System.out.println("Could not identify type/ Type is not supported yet");
                }

            }
            System.out.println(bars.get(0).getClass());
            int [] oldVal = bars.getValuesMono("red"); //TODO better run statement, this is shit

            while(true){
                try {
                    bars.updateBars();
                    int [] newVal = bars.getValuesMono("red");
                    if(!Arrays.equals(newVal, oldVal)){
                        System.out.println("Change Detected");
                        for(ReadableBar r : bars.getBarList()){
                            System.out.println("Percentage:"+ r.getPercentage());
                        }

                    }
                    oldVal = newVal;
                    sleep(pullrate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }









        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}


*/