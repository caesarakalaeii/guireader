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
    ArrayList<ReadableObjManager> manager;
    Scene scene;
    GUIReaderController cont;
    Executioner exec;
    LogicType[] logicTypeEnumArr = {LogicType.PERCENTAGE, LogicType.NUMBER, LogicType.POINT};
    LogicEnum[] logicEnumArr = {LogicEnum.EQUAL,LogicEnum.SMALLER,LogicEnum.GREATER};
    LogicType logicTypeEnum ;
    LogicEnum logicEnum ;
    ExecEnum execEnum;
    ExecEnum[] execEnumArr = {ExecEnum.SOUND};




    @Override
    public void start(Stage stage) throws Exception {
        manager = new ArrayList<>();
        this.stage = stage;
        stop=false;


        FXMLLoader fxmlLoader = new FXMLLoader(GUIReader.class.getResource("guireadergui.fxml"));




        startSlideShow();
        scene = new Scene(fxmlLoader.load());
        cont = fxmlLoader.getController();
        cont.setReader(this);
        stage.minWidthProperty().bind(cont.gethBoxAll().widthProperty());
        stage.minHeightProperty().bind(cont.gethBoxAll().heightProperty());
        cont.getLogicType().setItems(FXCollections.observableArrayList(
                "Percentage", "Number(Not Working)"));
        cont.getLogicType().getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> logicTypeEnum = logicTypeEnumArr[new_value.intValue()]);
        cont.getLogicThreshold().setItems(FXCollections.observableArrayList("Equals", "Smaller", "Greater"));
        cont.getLogicThreshold().getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> logicEnum = logicEnumArr[new_value.intValue()]);
        cont.getExecutionerChoice().setItems(FXCollections.observableArrayList(
                "Sound"
        ));
        cont.getExecutionerChoice().getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> execEnum = execEnumArr[new_value.intValue()]);


        stage.setTitle("GUI-Reader");
        stage.setScene(scene);
        stage.show();






        slideShow.messageProperty().addListener((observable, oldValue, newValue) -> {
                for(Manager man : manager) {
                    BufferedImage img = man.getImage();

                    cont.getImv().get(manager.indexOf(man)).setImage(SwingFXUtils.toFXImage(img, null));

                    if (cont.getLogic().compute()) {
                        exec.trigger();
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
                        Thread.sleep(10);
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



    public static void main(String[] args) {
        launch(args);
    }



    public ExecEnum getExecEnum() {
        return execEnum;
    }

    public Executioner getExec() {
        return exec;
    }

    public void setExec() {

        this.exec=ExecutionerFactory.newInstance(execEnum,exec);
    }

    public LogicType getLogicTypeEnum() {
        return logicTypeEnum;
    }

    public LogicEnum getLogicEnum() {
        return logicEnum;
    }
}



