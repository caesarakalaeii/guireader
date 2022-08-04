package com.guireadergui.execute;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundExecutioner extends Executioner {
    MediaPlayer media;


    public SoundExecutioner(int patience, Executioner ref) {
        super(patience, ref);
    }


    @Override
    public void setFile(File file){
        if (file !=null) {
            Media sound = new Media(file.toURI().toString());
            this.media = new MediaPlayer(sound);
        }
    }

    @Override
    public void trigger(){

        if(!triggered && patience<patienceCount){
            triggered = true;
            media.stop();
            media.play();
            return;
        }
        if(ref!=null){ref.trigger();}
        patienceCount++;

    }

    @Override
    public void reset(){
        triggered = false;
        super.reset();
    }
}
