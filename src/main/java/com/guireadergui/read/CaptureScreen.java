package com.guireadergui.read;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public abstract class CaptureScreen{
    Robot robot;
    Rectangle area;

    protected CaptureScreen(){
        try {
            robot = new Robot();
        }
        catch (AWTException e) {

            System.err.println("Could not capture screen, Initialization Fail: " + e.getMessage());

        }

    }
    protected CaptureScreen(int x, int y, int width, int height){
        this();
        area = new Rectangle(x, y, width, height);

    }
    protected BufferedImage getImg(){
        if(area == null){
            area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }
        return robot.createScreenCapture(area);
    }


    public int getX() {
        return (int)area.getX();
    }
    public int getY() {
        return (int)area.getY();
    }
    public int getWidth() {
        return (int)area.getWidth();
    }
    public int getHeight() {
        return (int)area.getHeight();
    }
}