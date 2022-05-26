package com.guireadergui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundExecutioner extends ExecutionerDecorator {
    MediaPlayer media;


    public SoundExecutioner(Executioner ref) {
        super(ref);
    }


    @Override
    public void setFile(File file){
        if (file !=null) {
            Media sound = new Media(file.toURI().toString());
            this.media = new MediaPlayer(sound);
        }
    }

    public void trigger(){
        ref.trigger();
        if(!triggered){
            triggered = true;
            media.play();
        }

    }
}
