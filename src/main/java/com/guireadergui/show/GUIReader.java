package com.guireadergui.show;

import com.guireadergui.execute.ExecEnum;
import com.guireadergui.execute.Executioner;
import com.guireadergui.logic.LogicEnum;
import com.guireadergui.logic.LogicType;
import com.guireadergui.logic.Manager;
import com.guireadergui.logic.ReadableObjManager;
import javafx.application.Application;
import javafx.collections.FXCollections;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GUIReader extends Application {
    Stage stage;

    boolean stop;
    List<Manager> manager;
    Scene scene;
    GUIReaderController cont;
    Executioner exec;
    LogicType[] logicTypeEnumArr = {LogicType.PERCENTAGE, LogicType.NUMBER, LogicType.POINT};
    LogicEnum[] logicEnumArr = {LogicEnum.EQUAL, LogicEnum.SMALLER, LogicEnum.GREATER};
    LogicType logicTypeEnum;
    LogicEnum logicEnum;
    ExecEnum execEnum;
    ExecEnum[] execEnumArr = {ExecEnum.SOUND};
    Manager newman;
    boolean del;


    @Override
    public void start(Stage stage) throws Exception {
        manager = new ArrayList<>();
        this.stage = stage;
        stop = false;
        del = false;


        FXMLLoader fxmlLoader = new FXMLLoader(GUIReader.class.getResource("guireadergui.fxml"));


        scene = new Scene(fxmlLoader.load());
        cont = fxmlLoader.getController();
        cont.setReader(this);

        stage.minWidthProperty().bind(cont.gethBoxAll().widthProperty());
        stage.minHeightProperty().bind(cont.gethBoxAll().heightProperty());
        cont.getLogicType().setItems(FXCollections.observableArrayList(
                "Percentage", "Number"));
        cont.getLogicType().getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> logicTypeEnum = logicTypeEnumArr[newValue.intValue()]);
        cont.getLogicThreshold().setItems(FXCollections.observableArrayList("Equals", "Smaller", "Greater"));
        cont.getLogicThreshold().getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> logicEnum = logicEnumArr[newValue.intValue()]);
        cont.getExecutionerChoice().setItems(FXCollections.observableArrayList(
                "Sound"
        ));
        cont.getExecutionerChoice().getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> cont.setExecEnum(execEnumArr[newValue.intValue()]));
        cont.getxField().setValueFactory(cont.getxValueFactory());
        cont.getyField().setValueFactory(cont.getyValueFactory());
        cont.getHeightField().setValueFactory(cont.getHeightValueFactory());
        cont.getWidthField().setValueFactory(cont.getWidthValueFactory());
        cont.getResField().setValueFactory(cont.getResFieldValueFactory());
        cont.getResuField().setValueFactory(cont.getResuFieldValueFactory());
        cont.getThreshold().setValueFactory(cont.getThresFieldValueFactory());
        cont.getPatienceSpinner().setValueFactory(cont.getPatienceValueFactory());
        cont.getrSpinner().setValueFactory(cont.getrValueFactory());
        cont.getgSpinner().setValueFactory(cont.getgValueFactory());
        cont.getbSpinner().setValueFactory(cont.getbValueFactory());

        Thread slideshow = new Thread(() -> {
            while (!stop) {
                if (newman != null) {
                    manager.add(newman);
                    newman = null;
                }
                if (del && !manager.isEmpty()) {
                    Manager toRemove = manager.get(manager.size() - 1);
                    toRemove.stopThread();
                    manager.remove(toRemove);
                    del = false;
                }
                try{
                for (Manager man : manager) {
                    BufferedImage img = man.getImage();

                    cont.getImv().get(manager.indexOf(man)).setImage(SwingFXUtils.toFXImage(img, null));

                }
                    Thread.sleep(100);
                } catch (Exception e){
                    System.err.println("Something for a logger");
                }


            }
        });


        stage.setTitle("GUI-Reader");
        stage.setScene(scene);
        stage.show();
        slideshow.start();

    }


    @Override
    public void stop() {
        stop = true;
        for (Manager man : manager) {
            man.stopThread();
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

    public void setExec(Executioner exec) {
        this.exec = exec;
    }

    public LogicType getLogicTypeEnum() {
        return logicTypeEnum;
    }

    public LogicEnum getLogicEnum() {
        return logicEnum;
    }

    public List<Manager> getManager() {
        return manager;
    }

    public void newMan(ReadableObjManager man) {
        newman = man;
    }

    public void deleteLast() {
        del = true;
    }
}



