package com.guireadergui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GUIReaderController {
    @FXML
    Stage stage;
    @FXML
    VBox imageVBox;
    @FXML
    ImageView imv4;
    @FXML
    Label yLabel;
    @FXML
    Label xLabel;
    @FXML
    Label wLabel;
    @FXML
    Label hLabel;
    @FXML
    Label rLabel;
    @FXML
    Label resLabel;
    @FXML
    Label thrLabel;
    @FXML
    Spinner<Integer> threshold;

    @FXML
    Spinner<Integer> heightField;

    @FXML
    Spinner<Integer> widthField;
    @FXML
    Spinner<Integer> resField;
    @FXML
    Spinner<Integer> resuField;
    @FXML
    Spinner<Integer> xField;
    @FXML
    Spinner<Integer> yField;
    @FXML
    Button setButton;
    @FXML
    HBox hBoxAll;
    @FXML
    VBox controlVBox;
    @FXML
    ChoiceBox logicType;
    @FXML
    ChoiceBox logicThreshold;






    GUIReader reader;

    SpinnerValueFactory<Integer> xValueFactory;
    SpinnerValueFactory<Integer> yValueFactory;
    SpinnerValueFactory<Integer> widthValueFactory;
    SpinnerValueFactory<Integer> heightValueFactory;
    SpinnerValueFactory<Integer> resFieldValueFactory;
    SpinnerValueFactory<Integer> resuFieldValueFactory;
    SpinnerValueFactory<Integer> thresFieldValueFactory;
    Image screen;
    ArrayList<BarRectangle> barRectangles;
    ArrayList<ImageView> imv;
    Logic logic;
    LogicType[] logicTypeEnumArr = {LogicType.PERCENTAGE, LogicType.NUMBER, LogicType.POINT};
    LogicEnum[] logicEnumArr = {LogicEnum.EQUAL,LogicEnum.SMALLER,LogicEnum.GREATER};
    LogicType logicTypeEnum ;
    LogicEnum logicEnum ;
    Thread t;


    public GUIReaderController (){
        xValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 100);
        yValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 100);
        widthValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 100);
        heightValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 20);
        resFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 10);
        resuFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 50);
        thresFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 50);

        barRectangles = new ArrayList();
        imv = new ArrayList();

        logic = null;




    }

    @FXML
    private void loadScreen(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            screen = new Image(file.toURI().toString());

            imv4.setImage(screen);
        }
    }

    @FXML
    private void loadSound(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file !=null) {
            Media sound = new Media(file.toURI().toString());
            reader.media = new MediaPlayer(sound);
        }
    }

    @FXML
    private void setValues(){
        if(setButton.getText() == "Set") {
            BarRectangle barRectangle = barRectangles.get(barRectangles.size() - 1);
            barRectangle.heightProperty().unbind();
            barRectangle.widthProperty().unbind();
            barRectangle.yProperty().unbind();
            barRectangle.xProperty().unbind();
            barRectangle.resuProperty().unbind();
            ReadableService serv = new ReadableService(ReadableType.BAR,xValueFactory.getValue(),
                    yValueFactory.getValue(),
                    widthValueFactory.getValue(),
                    heightValueFactory.getValue(),
                    resFieldValueFactory.getValue(),
                    resuFieldValueFactory.getValue());
            ReadableObject bar = serv.createReadableTask();
            ReadableObjManager man = new ReadableObjManager(bar);
            reader.manager.add(man);
            bar.setOnVal(Probe.getRGBArray(SwingFXUtils.fromFXImage(screen, null), bar.getProbes().get(0)));

            imv.add(new ImageView());
            imv.get(imv.size() - 1).setImage(SwingFXUtils.toFXImage(bar.getImg(), null));
            imageVBox.getChildren().add(imv.get(imv.size() - 1));

            logic = LogicFactory.newInstance(logicTypeEnum, logicEnum, man, threshold.getValue(), logic);

            setButton.setText("New");
            reader.startSlideShow();

        }
        xField.setValueFactory(xValueFactory);
        yField.setValueFactory(yValueFactory);
        widthField.setValueFactory(widthValueFactory);
        heightField.setValueFactory(heightValueFactory);
        resField.setValueFactory(resFieldValueFactory);
        resuField.setValueFactory(resuFieldValueFactory);
        threshold.setValueFactory(thresFieldValueFactory);
        createNewRectangle();
        setButton.setText("Set");



    }

    private void createNewRectangle() {
        BarRectangle barRectangle = new BarRectangle(xValueFactory.getValue(), yValueFactory.getValue(), widthValueFactory.getValue(), heightValueFactory.getValue());
        barRectangles.add(barRectangle);
        barRectangle.heightProperty().bind(heightField.valueProperty());
        barRectangle.widthProperty().bind(widthField.valueProperty());
        barRectangle.yProperty().bind(yField.valueProperty());
        barRectangle.xProperty().bind(xField.valueProperty());
        barRectangle.resuProperty().bind(resuField.valueProperty());

        drawRect();

    }

    public void drawRect(){
        BufferedImage tempscreen = SwingFXUtils.fromFXImage(screen, null);
        for(BarRectangle barRectangle : barRectangles) {
            int height = (int) barRectangle.getHeight();
            int width = (int) barRectangle.getWidth();
            int x = (int) barRectangle.getX();
            int y = (int) barRectangle.getY();
            int res = barRectangle.getResolution();



            for(int i = 0; i<width;i++) {
                tempscreen.setRGB(x + i, y, 100);
                tempscreen.setRGB(x + i, y + height, 100);
            }
            for(int i =0; i<height;i++) {

                tempscreen.setRGB(x, y + i, 100);
                tempscreen.setRGB(x + width, y + i, 100);
            }


            if (height >= width) {
                int middle = width / 2;
                int interval = height / res;

                for (int i = 0; i < res; i++) {
                    tempscreen.setRGB(x + middle, y + (interval * i), 100);
                }
            }  //in case of vertical bar
            if (height < width) {
                int middle = height / 2;
                int interval = width / res;
                for (int i = 0; i < res; i++) {
                    tempscreen.setRGB(x + (interval * i), y + middle, 100);
                }

            }



        }
        imv4.setImage(SwingFXUtils.toFXImage(tempscreen, null));

    }

    @FXML
    private void resetSound(){
        reader.resetSound();
    }

    public void setReader(GUIReader reader){
        this.reader = reader;

    }

    @FXML
    private void updateAll(){

        if(barRectangles.size() > 0) {
            drawRect();
        }
    }

}

