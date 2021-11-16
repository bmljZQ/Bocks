package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rendering implements Runnable{

    public static BufferedImage newView = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_RGB);
    public static int resolutionWidth;
    public static int resolutionHeight;
    public static int fieldOfView;
    private static double[][][] shapeCoordinates;
    private static int lineCount;

    private static void getData() {
        resolutionWidth = Main.resolutionWidth;
        resolutionHeight = Main.resolutionHeight;
        fieldOfView = Main.fieldOfView;
        shapeCoordinates = Main.coordinates;
        lineCount = Main.quadCount;
    }

    @Override
    public void run() {
        getData();
        while(Main.running){
            if(!Main.paused){
                render();
            }
        }
    }

    private static void render() {
        double[][][] pointsOnScreen = new double[lineCount][4][2];
        double[][][] linesOnScreen = new double[lineCount][4][2];
        double DZConstant;
        double distanceToScreen = (resolutionWidth / 2.0) / Math.tan(Math.toRadians(fieldOfView / 2.0));
        Color colourToDraw;
        Color pink = new Color(255, 0 ,255);
        Color yellow = new Color(255, 255, 0);
        Color softBlue = new Color(70, 100, 255);
        int pixelXCentered;
        int pixelYCentered;
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < 4; j++) {
                DZConstant = distanceToScreen / shapeCoordinates[i][j][2];
                for (int k = 0; k < 2; k++) {
                    pointsOnScreen[i][j][k] = shapeCoordinates[i][j][k] * DZConstant;
                }
            }

            for (int j = 0; j < 4; j++) {
                if(j + 1 > 3){
                    linesOnScreen[i][j][0] = (pointsOnScreen[i][j - 3][1] - pointsOnScreen[i][j][1]) / (pointsOnScreen[i][j - 3][0] - pointsOnScreen[i][j][0]);
                }else{
                    linesOnScreen[i][j][0] = (pointsOnScreen[i][j + 1][1] - pointsOnScreen[i][j][1]) / (pointsOnScreen[i][j + 1][0] - pointsOnScreen[i][j][0]);
                }
                linesOnScreen[i][j][1] = pointsOnScreen[i][j][1] - (linesOnScreen[i][j][0] * pointsOnScreen[i][j][0]);
            }
        }
        for (int pixelX = 0; pixelX < resolutionWidth; pixelX++) {
            pixelXCentered = (int) Math.round(pixelX - (resolutionWidth / 2.0));
            for (int pixelY = 0; pixelY < resolutionHeight; pixelY++) {
                colourToDraw = Color.black;
                pixelYCentered = (int) Math.round(pixelY - (resolutionHeight / 2.0));
                for (int i = 0; i < lineCount; i++) {
                    for (int j = 0; j < 4; j++) {
                        if(Main.points){
                            if(pixelXCentered == Math.round(pointsOnScreen[i][j][0]) || pixelYCentered == Math.round(pointsOnScreen[i][j][1])){
                                colourToDraw = pink;
                            }
                            if(Math.round(pixelXCentered / 10.0) == Math.round(pointsOnScreen[i][j][0] / 10.0) && Math.round(pixelYCentered / 10.0) == Math.round(pointsOnScreen[i][j][1] / 10.0)){
                                colourToDraw = yellow;
                            }
                        }
                        if(pixelYCentered == (linesOnScreen[i][j][0] * pixelXCentered) + linesOnScreen[i][j][1]){
                            colourToDraw = softBlue;
                        }

                        newView.setRGB(pixelX, pixelY, colourToDraw.getRGB());
                    }
                }
            }
        }
//        Main.updateView(newView);
    }
}