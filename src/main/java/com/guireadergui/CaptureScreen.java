package com.guireadergui;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class CaptureScreen extends Task {
    Robot robot;
    Rectangle area;

    public CaptureScreen(){
        try {
            robot = new Robot();
        }
        catch (AWTException e) {

            System.out.println("Could not capture screen, Initialization Fail: " + e.getMessage());

        }

    }
    public CaptureScreen(int x, int y, int width, int height){
        this();
        area = new Rectangle(x, y, width, height);

    }
    public BufferedImage getImg(){
        if(area == null){
            area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }
        return robot.createScreenCapture(area);
    }

    @Override
    protected Object call() throws Exception{
        return SwingFXUtils.toFXImage(getImg(),null);
    }



}