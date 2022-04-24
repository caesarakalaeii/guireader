package com.example.guireadergui;
import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CaptureScreen {
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
    public BufferedImage getImage(){
        if(area == null){
            area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }
        return robot.createScreenCapture(area);
    }



}