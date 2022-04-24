package com.example.guireadergui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

import java.io.File;
import java.io.IOException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import static java.lang.Thread.sleep;

public class HelloApplication extends Application {
    UpdateThread tr;
    @Override
    public void start(Stage stage) throws IOException {
        ReadableBar bar = new ReadableBar(236,1344,317,16,30);
        Image png = SwingFXUtils.toFXImage(bar.getUpdate(), null);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        ImageView image = new ImageView();
        image.setImage(png);
        Button button = new Button();

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        String musicFile = "C:\\Users\\Caesar\\Downloads\\sugoi-sugoi.mp3";     // For example


        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        tr = new UpdateThread(bar, image, stage, mediaPlayer);

        stage.show();

        tr.start();

    }
    public void stop(){
        tr.savestop();
    }

    public static void main(String[] args) {
        launch();
    }
}
class UpdateThread extends Thread{
    ReadableBar bar;
    ImageView image;
    Stage stage;
    boolean stop;
    MediaPlayer media;
    boolean playedMedia;


    public UpdateThread(ReadableBar bar, ImageView img, Stage stage, MediaPlayer media) {
        this.bar = bar;
        this.image = img;
        this.stage = stage;
        this.stop = false;
        this.playedMedia = false;
        this.media = media;
    }

    @Override
    public void run(){
        while (!stop) {
            Image png = SwingFXUtils.toFXImage(bar.getUpdate(), null);
            image.setImage(png);
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(bar.getPercentage() == 0){
                playsound();
            }
        }
    }
    public void savestop(){
        stop=true;
    }
    public void playsound(){
        if(!playedMedia){
            playedMedia = true;
            media.play();
        }
    }
    public void resetMedia(){
        playedMedia = false;
    }
}