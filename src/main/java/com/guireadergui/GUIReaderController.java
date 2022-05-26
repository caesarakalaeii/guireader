package com.guireadergui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GUIReaderController {
    @FXML
    private Stage stage;
    @FXML
    private VBox imageVBox;
    @FXML
    private ImageView imv4;
    @FXML
    private Label yLabel;
    @FXML
    private Label xLabel;
    @FXML
    private Label wLabel;
    @FXML
    private Label hLabel;
    @FXML
    private Label rLabel;
    @FXML
    private Label resLabel;
    @FXML
    private Label thrLabel;
    @FXML
    private Spinner<Integer> threshold;

    @FXML
    private Spinner<Integer> heightField;

    @FXML
    private Spinner<Integer> widthField;
    @FXML
    private Spinner<Integer> resField;
    @FXML
    private Spinner<Integer> resuField;
    @FXML
    private Spinner<Integer> xField;
    @FXML
    private Spinner<Integer> yField;
    @FXML
    private Button setButton;
    @FXML
    private HBox hBoxAll;
    @FXML
    private VBox controlVBox;
    @FXML
    private ChoiceBox<String> logicType;
    @FXML
    private ChoiceBox<String> logicThreshold;
    @FXML
    private Button resetButton;
    @FXML
    private ChoiceBox<String> executionerChoice;
    @FXML
    private Spinner<Integer> rSpinner;
    @FXML
    private Spinner<Integer> gSpinner;
    @FXML
    private Spinner<Integer> bSpinner;







    private GUIReader reader;

    private SpinnerValueFactory<Integer> xValueFactory;
    private SpinnerValueFactory<Integer> yValueFactory;
    private SpinnerValueFactory<Integer> widthValueFactory;
    private SpinnerValueFactory<Integer> heightValueFactory;
    private SpinnerValueFactory<Integer> resFieldValueFactory;
    private SpinnerValueFactory<Integer> resuFieldValueFactory;
    private SpinnerValueFactory<Integer> thresFieldValueFactory;
    private SpinnerValueFactory<Integer> rValueFactory;
    private SpinnerValueFactory<Integer> gValueFactory;
    private SpinnerValueFactory<Integer> bValueFactory;
    private Image screen;
    private List<BarRectangle> barRectangles;
    private List<ImageView> imv;
    private Logic logic;

    private File file;



    public GUIReaderController (){
        xValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 100);
        yValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 100);
        widthValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 100);
        heightValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 20);
        resFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 10);
        resuFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 50);
        thresFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 50);
        rValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 255);
        gValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0);
        bValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0);

        barRectangles = new ArrayList<>();
        imv = new ArrayList<>();

        logic = null;




    }

    @FXML
    private void loadScreen(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File screenfile = fileChooser.showOpenDialog(stage);
        if(screenfile != null) {
            screen = new Image(screenfile.toURI().toString());
            imv4.setImage(screen);
        }
    }

    @FXML
    private void loadSound(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extFilter);
        file = fileChooser.showOpenDialog(stage);

    }

    @FXML
    private void setValues(){
        if(setButton.getText().equals("Set")) {
            setButton.setText("New");
            BarRectangle barRectangle = barRectangles.get(barRectangles.size() - 1);
            barRectangle.heightProperty().unbind();
            barRectangle.widthProperty().unbind();
            barRectangle.yProperty().unbind();
            barRectangle.xProperty().unbind();
            barRectangle.resuProperty().unbind();
            ReadableObject bar = ReadableFactory.newInstance(ReadableType.BAR,xValueFactory.getValue(),
                    yValueFactory.getValue(),
                    widthValueFactory.getValue(),
                    heightValueFactory.getValue(),
                    resFieldValueFactory.getValue(),
                    resuFieldValueFactory.getValue());
            ReadableObjManager man = new ReadableObjManager(bar);
            reader.manager.add(man);
            bar.setOnVal(Probe.getRGBArray(SwingFXUtils.fromFXImage(screen, null), bar.getProbes().get(0)));

            imv.add(new ImageView());
            imv.get(imv.size() - 1).setImage(SwingFXUtils.toFXImage(bar.getImg(), null));
            imageVBox.getChildren().add(imv.get(imv.size() - 1));

            logic = LogicFactory.newInstance(reader.getLogicTypeEnum(), reader.getLogicEnum(), man, threshold.getValue(), logic);
            return;

        }
        xField.setValueFactory(xValueFactory);
        yField.setValueFactory(yValueFactory);
        widthField.setValueFactory(widthValueFactory);
        heightField.setValueFactory(heightValueFactory);
        resField.setValueFactory(resFieldValueFactory);
        resuField.setValueFactory(resuFieldValueFactory);
        threshold.setValueFactory(thresFieldValueFactory);
        rSpinner.setValueFactory(rValueFactory);
        gSpinner.setValueFactory(gValueFactory);
        bSpinner.setValueFactory(bValueFactory);
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
            int[] rgbArr ={rSpinner.getValue(),gSpinner.getValue(),bSpinner.getValue()};
            int color = Probe.toRGBint(rgbArr);



            for(int i = 0; i<width;i++) {
                tempscreen.setRGB(x + i, y, color);
                tempscreen.setRGB(x + i, y + height, color);
            }
            for(int i =0; i<height;i++) {

                tempscreen.setRGB(x, y + i, color);
                tempscreen.setRGB(x + width, y + i, color);
            }


            if (height >= width) {
                int middle = width / 2;
                int interval = height / res;

                for (int i = 0; i < res; i++) {
                    tempscreen.setRGB(x + middle, y + (interval * i), color);
                }
            }  //in case of vertical bar
            if (height < width) {
                int middle = height / 2;
                int interval = width / res;
                for (int i = 0; i < res; i++) {
                    tempscreen.setRGB(x + (interval * i), y + middle, color);
                }

            }



        }
        imv4.setImage(SwingFXUtils.toFXImage(tempscreen, null));

    }

    @FXML
    private void resetSound(){
            reader.getExec().reset();
    }

    public void setReader(GUIReader reader){
        this.reader = reader;

    }

    @FXML
    private void updateAll(){

        if(!barRectangles.isEmpty()) {
            drawRect();
        }
    }

    @FXML
    private void setExecutioner(){
        reader.setExec();
        reader.getExec().setFile(file);

    }


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public VBox getImageVBox() {
        return imageVBox;
    }

    public void setImageVBox(VBox imageVBox) {
        this.imageVBox = imageVBox;
    }

    public ImageView getImv4() {
        return imv4;
    }

    public void setImv4(ImageView imv4) {
        this.imv4 = imv4;
    }

    public Label getyLabel() {
        return yLabel;
    }

    public void setyLabel(Label yLabel) {
        this.yLabel = yLabel;
    }

    public Label getxLabel() {
        return xLabel;
    }

    public void setxLabel(Label xLabel) {
        this.xLabel = xLabel;
    }

    public Label getwLabel() {
        return wLabel;
    }

    public void setwLabel(Label wLabel) {
        this.wLabel = wLabel;
    }

    public Label gethLabel() {
        return hLabel;
    }

    public void sethLabel(Label hLabel) {
        this.hLabel = hLabel;
    }

    public Label getrLabel() {
        return rLabel;
    }

    public void setrLabel(Label rLabel) {
        this.rLabel = rLabel;
    }

    public Label getResLabel() {
        return resLabel;
    }

    public void setResLabel(Label resLabel) {
        this.resLabel = resLabel;
    }

    public Label getThrLabel() {
        return thrLabel;
    }

    public void setThrLabel(Label thrLabel) {
        this.thrLabel = thrLabel;
    }

    public Spinner<Integer> getThreshold() {
        return threshold;
    }

    public void setThreshold(Spinner<Integer> threshold) {
        this.threshold = threshold;
    }

    public Spinner<Integer> getHeightField() {
        return heightField;
    }

    public void setHeightField(Spinner<Integer> heightField) {
        this.heightField = heightField;
    }

    public Spinner<Integer> getWidthField() {
        return widthField;
    }

    public void setWidthField(Spinner<Integer> widthField) {
        this.widthField = widthField;
    }

    public Spinner<Integer> getResField() {
        return resField;
    }

    public void setResField(Spinner<Integer> resField) {
        this.resField = resField;
    }

    public Spinner<Integer> getResuField() {
        return resuField;
    }

    public void setResuField(Spinner<Integer> resuField) {
        this.resuField = resuField;
    }

    public Spinner<Integer> getxField() {
        return xField;
    }

    public void setxField(Spinner<Integer> xField) {
        this.xField = xField;
    }

    public Spinner<Integer> getyField() {
        return yField;
    }

    public void setyField(Spinner<Integer> yField) {
        this.yField = yField;
    }

    public Button getSetButton() {
        return setButton;
    }

    public void setSetButton(Button setButton) {
        this.setButton = setButton;
    }

    public HBox gethBoxAll() {
        return hBoxAll;
    }

    public void sethBoxAll(HBox hBoxAll) {
        this.hBoxAll = hBoxAll;
    }

    public VBox getControlVBox() {
        return controlVBox;
    }

    public void setControlVBox(VBox controlVBox) {
        this.controlVBox = controlVBox;
    }

    public ChoiceBox<String> getLogicType() {
        return logicType;
    }

    public void setLogicType(ChoiceBox<String> logicType) {
        this.logicType = logicType;
    }

    public ChoiceBox<String> getLogicThreshold() {
        return logicThreshold;
    }

    public void setLogicThreshold(ChoiceBox<String> logicThreshold) {
        this.logicThreshold = logicThreshold;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public void setResetButton(Button resetButton) {
        this.resetButton = resetButton;
    }

    public ChoiceBox<String> getExecutionerChoice() {
        return executionerChoice;
    }

    public void setExecutionerChoice(ChoiceBox<String> executionerChoice) {
        this.executionerChoice = executionerChoice;
    }

    public GUIReader getReader() {
        return reader;
    }

    public SpinnerValueFactory<Integer> getxValueFactory() {
        return xValueFactory;
    }

    public void setxValueFactory(SpinnerValueFactory<Integer> xValueFactory) {
        this.xValueFactory = xValueFactory;
    }

    public SpinnerValueFactory<Integer> getyValueFactory() {
        return yValueFactory;
    }

    public void setyValueFactory(SpinnerValueFactory<Integer> yValueFactory) {
        this.yValueFactory = yValueFactory;
    }

    public SpinnerValueFactory<Integer> getWidthValueFactory() {
        return widthValueFactory;
    }

    public void setWidthValueFactory(SpinnerValueFactory<Integer> widthValueFactory) {
        this.widthValueFactory = widthValueFactory;
    }

    public SpinnerValueFactory<Integer> getHeightValueFactory() {
        return heightValueFactory;
    }

    public void setHeightValueFactory(SpinnerValueFactory<Integer> heightValueFactory) {
        this.heightValueFactory = heightValueFactory;
    }

    public SpinnerValueFactory<Integer> getResFieldValueFactory() {
        return resFieldValueFactory;
    }

    public void setResFieldValueFactory(SpinnerValueFactory<Integer> resFieldValueFactory) {
        this.resFieldValueFactory = resFieldValueFactory;
    }

    public SpinnerValueFactory<Integer> getResuFieldValueFactory() {
        return resuFieldValueFactory;
    }

    public void setResuFieldValueFactory(SpinnerValueFactory<Integer> resuFieldValueFactory) {
        this.resuFieldValueFactory = resuFieldValueFactory;
    }

    public SpinnerValueFactory<Integer> getThresFieldValueFactory() {
        return thresFieldValueFactory;
    }

    public void setThresFieldValueFactory(SpinnerValueFactory<Integer> thresFieldValueFactory) {
        this.thresFieldValueFactory = thresFieldValueFactory;
    }

    public Image getScreen() {
        return screen;
    }

    public void setScreen(Image screen) {
        this.screen = screen;
    }

    public List<BarRectangle> getBarRectangles() {
        return barRectangles;
    }

    public void setBarRectangles(List<BarRectangle> barRectangles) {
        this.barRectangles = barRectangles;
    }

    public List<ImageView> getImv() {
        return imv;
    }

    public void setImv(List<ImageView> imv) {
        this.imv = imv;
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

