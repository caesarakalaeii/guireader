package com.example.guireadergui;

import javafx.application.Application;
import javafx.concurrent.Task;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;


public class Main extends Application {

    ImageView imv;
    Label label;
    String file="";

    GridPane gridPane;
    Scene scene;
    Task<Void> slideShow;
    boolean stop;
    MediaPlayer media;
    boolean playedMedia;


    @Override
    public void start(Stage primarystage) {
        gridPane=new GridPane();
        gridPane.setPadding(new Insets(0, 10, 10, 10));

        // business logic for slide show
        startSlideShow();

        imv=new ImageView();

        label = new Label();
        label.setGraphic(imv);
        Button butt = new Button();
        butt.setText("Reset");
        butt.setOnAction((Event) -> {
            playedMedia = false;
            media.stop();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        gridPane.add(label, 5, 0, 1, 1);
        gridPane.add(butt,5,5,5,5);

        scene=new Scene(gridPane, 450, 300, Color.TRANSPARENT);
        String musicFile = "C:\\Users\\Caesar\\Downloads\\sugoi-sugoi.mp3";     // For example


        Media sound = new Media(new File(musicFile).toURI().toString());
        media = new MediaPlayer(sound);

        primarystage.setScene(scene);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        primarystage.show();


        // spawning a new thread for this task
        new Thread(slideShow).start();
    }
    @Override
    public void stop(){
        stop=true;
    }

    public void startSlideShow() {
        slideShow= new Task<Void>() {
            @Override
            protected Void call() {
                int i = 1;
                while (!stop) {
                    try {
                        Thread.sleep(1000);
                        updateMessage(i + ".png");
                    } catch (Exception e) {
                    }
                    i++;
                }
                return null;
            }
        };

        ReadableBar bar = new ReadableBar(237,1344,317,16,50);


        slideShow.messageProperty().addListener((observable, oldValue, newValue) -> {
            BufferedImage img = bar.getUpdate();
            for(Probe p: bar.getProbes()){
                img.setRGB(p.getX(),p.getY(),255);
            }
            imv.setImage(SwingFXUtils.toFXImage(img, null));
            System.out.println("Percantage: " + bar.getPercentage());
            if(bar.getPercentage() == 0){
                playsound();
            }
        });
    }
    public void playsound(){

        if(!playedMedia){
            playedMedia = true;
            media.play();
        }
        else{
            System.out.println("Already played");

        }


        
    }

    public static void main(String[] args) {
        launch(args);
    }


}