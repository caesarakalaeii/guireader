package com.guireadergui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
        cont.getLogicType().getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> logicTypeEnum = logicTypeEnumArr[newValue.intValue()]);
        cont.getLogicThreshold().setItems(FXCollections.observableArrayList("Equals", "Smaller", "Greater"));
        cont.getLogicThreshold().getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> logicEnum = logicEnumArr[newValue.intValue()]);
        cont.getExecutionerChoice().setItems(FXCollections.observableArrayList(
                "Sound"
        ));
        cont.getExecutionerChoice().getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> execEnum = execEnumArr[newValue.intValue()]);


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
            protected Void call() throws InterruptedException {
                int i = 1;
                while (!stop) {
                    try {
                        Thread.sleep(10);
                        updateMessage(i + ".png");
                    } catch (Exception e) {
                        throw e;
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



