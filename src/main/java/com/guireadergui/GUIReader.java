package com.guireadergui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUIReader  extends Application {
    Stage stage;

    Task<Void> slideShow;
    boolean stop;
    MediaPlayer media;
    boolean playedMedia;
    ArrayList<ReadableObjManager> manager;
    Scene scene;
    GUIReaderController cont;




    @Override
    public void start(Stage stage) throws Exception {
        manager = new ArrayList<>();
        this.stage = stage;
        stop=false;


        FXMLLoader fxmlLoader = new FXMLLoader(GUIReader.class.getResource("guireadergui.fxml"));




        startSlideShow();
        scene = new Scene(fxmlLoader.load(), 320, 240);
        cont = fxmlLoader.getController();
        cont.setReader(this);
        stage.minWidthProperty().bind(cont.hBoxAll.widthProperty());
        stage.minHeightProperty().bind(cont.hBoxAll.heightProperty());
        cont.logicType.setItems(FXCollections.observableArrayList(
                "Percentage", "Number(Not Working)"));
        cont.logicType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue ov, Number value, Number new_value){
                cont.logicTypeEnum = cont.logicTypeEnumArr[new_value.intValue()];
            }
        });
        cont.logicThreshold.setItems(FXCollections.observableArrayList("Equals", "Smaller", "Greater"));
        cont.logicType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue ov, Number value, Number new_value){
                cont.logicEnum = cont.logicEnumArr[new_value.intValue()];
            }
        });


        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();






        slideShow.messageProperty().addListener((observable, oldValue, newValue) -> {
                for(Manager man : manager) {
                    BufferedImage img = man.getImage();

                    cont.imv.get(manager.indexOf(man)).setImage(SwingFXUtils.toFXImage(img, null));

                    if (cont.logic.compute()) {
                        playsound();
                    }
                }
            });

        new Thread(slideShow).start();
        }


    public void startSlideShow() {
        slideShow = new Task<>() {
            @Override
            protected Void call() {
                int i = 1;
                while (!stop) {
                    try {
                        Thread.sleep(100);
                        updateMessage(i + ".png");
                    } catch (Exception e) {
                    }
                    i++;
                }
                return null;
            }
        };


    }

    @Override
    public void stop(){
        stop=true;
        for(Manager man : manager) {
            man.stop();
        }

    }

    public void playsound(){

        if(!playedMedia && media != null){
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

    public void resetSound(){
        playedMedia = false;
    }
}



