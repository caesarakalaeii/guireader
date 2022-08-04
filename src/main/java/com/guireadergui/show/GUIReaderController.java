package com.guireadergui.show;

import com.guireadergui.execute.ExecEnum;
import com.guireadergui.execute.Executioner;
import com.guireadergui.execute.ExecutionerFactory;
import com.guireadergui.logic.Logic;
import com.guireadergui.logic.LogicFactory;
import com.guireadergui.logic.Manager;
import com.guireadergui.logic.ReadableObjManager;
import com.guireadergui.read.Probe;
import com.guireadergui.read.ReadableFactory;
import com.guireadergui.read.ReadableObject;
import com.guireadergui.read.ReadableEnum;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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
    @FXML
    private Spinner<Integer> patienceSpinner;







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
    private SpinnerValueFactory<Integer> patienceValueFactory;
    private Image screen;
    private List<ModRectangle> rectangles;
    private List<ImageView> imv;
    private Logic logic;
    private Executioner e;

    private File file;
    private int color;
    private ExecEnum execEnum;


    public GUIReaderController (){
        xValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 70);
        yValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 205);
        widthValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 70);
        heightValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9000, 133);
        resFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 10);
        resuFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 50);
        thresFieldValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 5);
        rValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 255);
        gValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0);
        bValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0);
        patienceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10);
        color = 0;

        rectangles = new ArrayList<>();
        imv = new ArrayList<>();






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
        if(e!=null) {
            e.setFile(file);
        }

    }

    @FXML
    private void setValues(){
        if(setButton.getText().equals("Set")) {
            setButton.setText("New");
            ModRectangle modRectangle = rectangles.get(rectangles.size() - 1);
            modRectangle.heightProperty().unbind();
            modRectangle.widthProperty().unbind();
            modRectangle.yProperty().unbind();
            modRectangle.xProperty().unbind();
            modRectangle.resuProperty().unbind();
            ReadableObject bar = ReadableFactory.newInstance(getReadableType(),xValueFactory.getValue(),
                    yValueFactory.getValue(),
                    widthValueFactory.getValue(),
                    heightValueFactory.getValue(),
                    resFieldValueFactory.getValue(),
                    resuFieldValueFactory.getValue());
            ReadableObjManager man = new ReadableObjManager(bar);
            if(e!=null){
                man.setE(e);
            }
            reader.newMan(man);
            bar.setOnVal(Probe.getRGBArray(SwingFXUtils.fromFXImage(screen, null).getSubimage(bar.getX(),
                    bar.getY(),
                    bar.getWidth(),
                    bar.getHeight()), bar.getProbes().get(0)));
            ImageView im = new ImageView();
            im.setImage(SwingFXUtils.toFXImage(bar.getImg(), null));
            imv.add(im);
            imageVBox.getChildren().add(imv.get(imv.size() - 1));

            logic = LogicFactory.newInstance(reader.getLogicTypeEnum(), reader.getLogicEnum(), man, threshold.getValue(), logic);
            man.start();
            return;

        }
        switch(getReadableType()){
            case NUMBER -> createNewNumber();
            case BAR -> createNewRectangle();
            case POINT -> createNewPoint();
        }

        setButton.setText("Set");



    }



    @FXML
    private void deleteLast(){
        if(!rectangles.isEmpty() && reader.getManager().size()==rectangles.size()) {
            reader.deleteLast();
            ImageView toRemove = imv.get(imv.size()-1);
            imageVBox.getChildren().remove(toRemove);
            imv.remove(toRemove);
        }
        if (!rectangles.isEmpty()) {
            rectangles.remove(rectangles.size()-1);
        }
        setButton.setText("New");
        imv4.setImage(screen);
    }

    @FXML
    private void openGitHub() throws IOException, URISyntaxException {
        URL gitHub = new URL("https://github.com/caesarakalaeii/guireader");
        Desktop d = Desktop.isDesktopSupported() ? Desktop.getDesktop(): null;
        if (d != null && d.isSupported(Desktop.Action.BROWSE)){
            d.browse(gitHub.toURI());
        }


    }


    private void createNewNumber() {
        ModRectangle numberRectangle = new NumberRectangle(xValueFactory.getValue(), yValueFactory.getValue(), widthValueFactory.getValue(), heightValueFactory.getValue());
        rectangles.add(numberRectangle);
        numberRectangle.heightProperty().bind(heightField.valueProperty());
        numberRectangle.widthProperty().bind(widthField.valueProperty());
        numberRectangle.yProperty().bind(yField.valueProperty());
        numberRectangle.xProperty().bind(xField.valueProperty());

        drawRect();

    }

    private void createNewRectangle() {
        ModRectangle barRectangle = new BarRectangle(xValueFactory.getValue(), yValueFactory.getValue(), widthValueFactory.getValue(), heightValueFactory.getValue());
        rectangles.add(barRectangle);
        barRectangle.heightProperty().bind(heightField.valueProperty());
        barRectangle.widthProperty().bind(widthField.valueProperty());
        barRectangle.yProperty().bind(yField.valueProperty());
        barRectangle.xProperty().bind(xField.valueProperty());
        barRectangle.resuProperty().bind(resuField.valueProperty());

        drawRect();

    }


    public void drawRect(){
        BufferedImage tempscreen = SwingFXUtils.fromFXImage(screen, null);
        BufferedImage copy = new BufferedImage(tempscreen.getWidth(),tempscreen.getHeight(),1); //Remove alpha channel
        Graphics2D g2d = copy.createGraphics();
        g2d.setColor(Color.WHITE); // Or what ever fill color you want...
        g2d.fillRect(0, 0, copy.getWidth(), copy.getHeight());
        g2d.drawImage(tempscreen, 0, 0, null);
        g2d.dispose();
        color = Probe.toRGBint(rValueFactory.getValue(),gValueFactory.getValue(),bValueFactory.getValue());

        for(ModRectangle rectangle : rectangles) {
            rectangle.draw(copy, color);
        }
        imv4.setImage(SwingFXUtils.toFXImage(copy, null));

    }

    @FXML
    private void resetSound(){
            e.reset();
    }

    public void setReader(GUIReader reader){
        this.reader = reader;

    }

    @FXML
    public void updateAll(){

        if(!rectangles.isEmpty()) {
            drawRect();
        }
    }

    @FXML
    private void setExecutioner(){
        this.e= ExecutionerFactory.newInstance(execEnum,patienceSpinner.getValue(),e);
        e.setFile(file);
        List<Manager> manager = reader.getManager();
        if(!manager.isEmpty()){
            manager.get(manager.size()-1).setE(e);
        }



    }

    public ReadableEnum getReadableType(){
        if(logicType.getValue().equals("Percentage")){
            return ReadableEnum.BAR;
        }
        else{
            return ReadableEnum.NUMBER;
        }
    }
    private void createNewPoint() {
        throw new UnsupportedOperationException("Point not yet Supported");
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

    public List<ModRectangle> getRectangles() {
        return rectangles;
    }

    public void setRectangles(List<ModRectangle> rectangles) {
        this.rectangles = rectangles;
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

    public Spinner<Integer> getrSpinner() {
        return rSpinner;
    }

    public void setrSpinner(Spinner<Integer> rSpinner) {
        this.rSpinner = rSpinner;
    }

    public Spinner<Integer> getgSpinner() {
        return gSpinner;
    }

    public void setgSpinner(Spinner<Integer> gSpinner) {
        this.gSpinner = gSpinner;
    }

    public Spinner<Integer> getbSpinner() {
        return bSpinner;
    }

    public void setbSpinner(Spinner<Integer> bSpinner) {
        this.bSpinner = bSpinner;
    }

    public Spinner<Integer> getPatienceSpinner() {
        return patienceSpinner;
    }

    public void setPatienceSpinner(Spinner<Integer> patienceSpinner) {
        this.patienceSpinner = patienceSpinner;
    }

    public SpinnerValueFactory<Integer> getrValueFactory() {
        return rValueFactory;
    }

    public void setrValueFactory(SpinnerValueFactory<Integer> rValueFactory) {
        this.rValueFactory = rValueFactory;
    }

    public SpinnerValueFactory<Integer> getgValueFactory() {
        return gValueFactory;
    }

    public void setgValueFactory(SpinnerValueFactory<Integer> gValueFactory) {
        this.gValueFactory = gValueFactory;
    }

    public SpinnerValueFactory<Integer> getbValueFactory() {
        return bValueFactory;
    }

    public void setbValueFactory(SpinnerValueFactory<Integer> bValueFactory) {
        this.bValueFactory = bValueFactory;
    }

    public SpinnerValueFactory<Integer> getPatienceValueFactory() {
        return patienceValueFactory;
    }

    public void setPatienceValueFactory(SpinnerValueFactory<Integer> patienceValueFactory) {
        this.patienceValueFactory = patienceValueFactory;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setE(Executioner e){
        this.e =  e;
    }


    public void setExecEnum(ExecEnum execEnum) {
        this.execEnum=execEnum;
    }
}

