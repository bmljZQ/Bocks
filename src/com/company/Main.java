package com.company;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main extends Application {

    //Codes to colour the output text in the console.
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String TITLE = "Bocks";
    public static final String MAIN_MENU = "Main menu";
    public static final String MAP_SELECT = "Map select";
    public static final String OPTIONS = "Options";
    public static final String GAME = "Game";
    public static final String GAME_MENU = "Game menu";
    public static final String R_WORLD = "World";
    public static final String R_OBJECTS = "Objects";
    public static final String R_LIGHTS = "Lights";
    public static final int buttonSpeed = 15;

    public static boolean running = false;
    public static boolean paused = true;
    public static int targetFPS = 60;
    public static int quadCount = 0;
    public static int objectCount = 0;
    public static int lightCount = 0;
    public static ArrayList<String> worldStrings;
    public static ArrayList<String> objectStrings;
    public static ArrayList<String> lightStrings;
    public static ArrayList<Quad> world;
    public static ArrayList<Object> objects;
    public static ArrayList<Light> lights;
    public ArrayList<ListMap> maps;
    public static BitSet keysPressed = new BitSet(256);
    public static double[][][] coordinates;
    public static int resolutionWidth = 1700;
    public static int resolutionHeight = 900;
    public static int fieldOfView = 60;
    public static double moveSpeed = 0.2;
    public static boolean points = true;
    public static double[] cameraDeltaUpDown = {0,0,0};
    public static double[] cameraDeltaLeftRight = {0,0,0};
    public static double[] cameraPos = {0,0,0};
    public static double[] cameraAngle = {0,0};
    public static double cameraAngleXtoY = 0;
    public static double gameFade = 0;
    public String focusedWindow = "Main menu";
    public double lastWidth = 0;
    public double lastHeight;
    public static String screenshotFolder = "screenshots";
    public CustomImage[] buttonList;
    public File mapToLoad = null;
    public static boolean mapLoaded = false;
    public static boolean debugMode = false;
    public static Text debugText;
    public boolean mouseInBounds = false;

    public static void main(String[] args) {
        running = true;
        launch(args);
    }

    private static void loadMap(File mapFile){
        BufferedReader reader;
        worldStrings = new ArrayList<>();
        objectStrings = new ArrayList<>();
        lightStrings = new ArrayList<>();
        world = new ArrayList<>();
        objects = new ArrayList<>();
        lights = new ArrayList<>();
        String readLine;
        int bracketNo = 0;
        boolean endLoop = false;

        String currentlyReading = "";
        try{
            reader = new BufferedReader(new FileReader(mapFile));
            do{
                readLine = reader.readLine().trim();
                if(readLine.contains("<World>") || readLine.contains("<world>")){
                    currentlyReading = R_WORLD;
                }else if(readLine.contains("<Objects>") || readLine.contains("<objects>")){
                    currentlyReading = R_OBJECTS;
                }else if(readLine.contains("<Lights>") || readLine.contains("<lights>")){
                    currentlyReading = R_LIGHTS;
                }
                if(readLine.contains("}")){
                    currentlyReading = "";
                    bracketNo++;
                }
                if(bracketNo == 3){
                    endLoop = true;
                }
                if(readLine.startsWith("[") && readLine.endsWith("]")){
                    switch (currentlyReading) {
                        case R_WORLD:
                            worldStrings.add(readLine.substring(1, (readLine.length() - 1)));
                            break;
                        case R_OBJECTS:
                            objectStrings.add(readLine.substring(1, (readLine.length() - 1)));
                            break;
                        case R_LIGHTS:
                            lightStrings.add(readLine.substring(1, (readLine.length() - 1)));
                            break;
                    }
                }
            }while(!endLoop);
        }catch(FileNotFoundException e){
            reportError("Error initialising the file reader to read the map.", e);
        }catch(IOException e){
            reportError("Error counting the lines in the map file.", e);
        }

        quadCount = worldStrings.size();
        objectCount = objectStrings.size();
        lightCount = lightStrings.size();
        String[][] tempCoordinates;
        String[][][] coordinatesString;

        tempCoordinates = new String[quadCount][4];
        coordinates = new double[quadCount][4][3];
        coordinatesString = new String[quadCount][4][3];
        for (int i = 0; i < quadCount; i++) {
            //pppfpfpfpfpfpfpfpfpffpfpfppfpfpfp
            tempCoordinates[i] = worldStrings.get(i).split("/");
            for (int j = 0; j < 4; j++) {
                coordinatesString[i][j] = tempCoordinates[i][j].split(",");
                for (int k = 0; k < 3; k++) {
                    coordinates[i][j][k] = Double.parseDouble(coordinatesString[i][j][k]);
                }
            }
            world.add(new Quad(i, coordinates[i], null, null, null, 0));
        }

        ArrayList<String>[] tempCoordsDynamic = new ArrayList[objectCount];
        ArrayList<double[]>[] coordsDynamic = new ArrayList[objectCount];
        ArrayList<String[]>[] coordsStringDynamic = new ArrayList[objectCount];
        String[] startPosString;

        for (int i = 0; i < objectCount; i++) {
            startPosString = objectStrings.get(i).split("¦")[1].split(",");
            tempCoordsDynamic[i] = new ArrayList<>(Arrays.asList(objectStrings.get(i).split("¦")[0].split("/")));

            ArrayList<String[]> listOfCoordStringArrays = new ArrayList<>();
            for (int j = 0; j < tempCoordsDynamic[i].size(); j++) {
                listOfCoordStringArrays.add(tempCoordsDynamic[i].get(j).split(","));
            }
            coordsStringDynamic[i] = listOfCoordStringArrays;

            ArrayList<double[]> coordArrays = new ArrayList<>();
            double[] startPos = new double[3];
            for (int j = 0; j < coordsStringDynamic[i].size(); j++) {
                double[] coords = new double[3];
                for (int k = 0; k < 3; k++) {
                    coords[k] = Double.parseDouble(coordsStringDynamic[i].get(j)[k]);
                }
                coordArrays.add(coords);
            }
            for (int j = 0; j < 3; j++) {
                startPos[j] = Double.parseDouble(startPosString[j]);
            }
            coordsDynamic[i] = coordArrays;

            double[][] vertices = new double[][]{};
            vertices = coordsDynamic[i].toArray(vertices);

            objects.add(new Object(i, startPos, vertices));
        }

        double[][] pos = new double[lightCount][3];
        String[][] posString = new String[lightCount][3];
        String[][] lightData = new String[lightCount][3];
        double[] brightnesses = new double[lightCount];
        String[][] colourData = new String[lightCount][3];
        Color[] colours = new Color[lightCount];
        for (int i = 0; i < lightCount; i++) {
            lightData[i] = lightStrings.get(i).split("/");
            posString[i] = lightData[i][0].split(",");
            for (int j = 0; j < 3; j++) {
                pos[i][j] = Double.parseDouble(posString[i][j]);
            }
            brightnesses[i] = Double.parseDouble(lightData[i][1]);
            colourData[i] = lightData[i][2].split(",");
            colours[i] = Color.color(Double.parseDouble(colourData[i][0]), Double.parseDouble(colourData[i][1]), Double.parseDouble(colourData[i][2]));

            lights.add(new Light(i, pos[i], brightnesses[i], colours[i]));
        }

        mapLoaded = true;
    }

    public static void reportError(String error, Exception e){
        //This method simply takes the string it was given and prints it to the console in a visually appealing format.
        System.out.println(ANSI_RED + error + ANSI_RESET);
        e.printStackTrace();
    }

    public static void screenshot(Scene scene, KeyEvent e){
        if(e.getCode() == KeyCode.F7){
            WritableImage snapshot = scene.snapshot(null);
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy¦HH-mm-ss");
            String fileName = screenshotFolder + "/screenshot_" + formatter.format(dateTime) + ".png";
            File file = new File(fileName);
            BufferedImage snapshotBuffered = SwingFXUtils.fromFXImage(snapshot, null);
            try{
                ImageIO.write(snapshotBuffered, "png", file);
                System.out.println(ANSI_GREEN + "Screenshot saved to folder " + ANSI_PURPLE + screenshotFolder + ANSI_GREEN + " as " + ANSI_PURPLE + fileName + ANSI_GREEN + "." + ANSI_RESET);
            }catch(IOException ex){
                reportError("Error saving the screenshot.", ex);
            }
        }
    }

    public void selectMap(ListMap map){
        for (int i = 0; i < maps.size(); i++) {
            if(maps.get(i) == map){
                if(maps.get(i).selected){
                    maps.get(i).selected = false;
                    mapToLoad = null;
                }else{
                    maps.get(i).selected = true;
                    mapToLoad = maps.get(i).mapFile;
                }
            }else{
                maps.get(i).selected = false;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean transitioning = false;

        Canvas canvas = new Canvas(resolutionWidth, resolutionHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
//        System.out.println(javafx.scene.text.Font.getFamilies());
        VBox menuBox = new VBox();
        GridPane mapSelectBox = new GridPane();
        menuBox.setAlignment(Pos.TOP_CENTER);
        mapSelectBox.setAlignment(Pos.CENTER_LEFT);

        File backgroundFile = new File("resources/background1.png");
        primaryStage.getIcons().add(new Image(new FileInputStream("resources/icon.png")));
        Image background = new Image(new FileInputStream(backgroundFile));
        Image mapBase = new Image(new FileInputStream("resources/Map_Base.png"));
        Image mapBaseSelected = new Image(new FileInputStream("resources/Map_Base_Selected.png"));
        Image mainMenu = new Image(new FileInputStream("resources/Main_Menu.png"));
        Image mapSelect = new Image(new FileInputStream("resources/Map_Select.png"));
        Image options = new Image(new FileInputStream("resources/Options.png"));
        Image exit = new Image(new FileInputStream("resources/Exit.png"));
        Image start = new Image(new FileInputStream("resources/Start.png"));
        Image startEmpty = new Image(new FileInputStream("resources/Start_Empty.png"));
        Image leftMapsBracketImage = new Image(new FileInputStream("resources/Left_Bracket.png"));
        Image rightMapsBracketImage = new Image(new FileInputStream("resources/Right_Bracket.png"));
        Image defaultMapIcon = new Image(new FileInputStream("resources/Default_Map_Icon.png"));
        Image mapSelectBackgroundImage = new Image(new FileInputStream("resources/Map_Select_Background.png"));

        CustomText title = new CustomText(TITLE, 300);
        title.text.setFont(javafx.scene.text.Font.font("Monospaced", FontWeight.BOLD,130));
        title.text.setFill(Color.LIMEGREEN);
        Background boxesBackground = new Background(new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize((resolutionHeight / background.getHeight()), (resolutionWidth / background.getWidth()), true, true, false, false)));

        CustomImage mainMenuButton = new CustomImage(mainMenu, resolutionHeight + 200, 0, 1, -200);
        CustomImage mapSelectButton = new CustomImage(mapSelect, resolutionHeight + 200, 0, 1, 0);
        CustomImage optionsButton = new CustomImage(options, resolutionHeight + 200, 0, 1, 0);
        CustomImage exitButton = new CustomImage(exit, resolutionHeight + 200, 0, 1, 0);
        CustomImage startButton = new CustomImage(start, resolutionHeight + 200, 0, 1, 200);
        CustomImage leftMapsBracket = new CustomImage(leftMapsBracketImage, resolutionHeight + 200, 0, 1, 0);
        CustomImage rightMapsBracket = new CustomImage(rightMapsBracketImage, resolutionHeight + 200, 1100, 1, 0);
        CustomImage mapSelectBackground = new CustomImage(mapSelectBackgroundImage, 0, 0, 0, 0);

        ListMap testMap = new ListMap("Test Map", "Just a test map.", "Used for...testing.", mapBase, mapBaseSelected, defaultMapIcon, "maps/testMap.mfb");
        buttonList = new CustomImage[]{mainMenuButton, mapSelectButton, optionsButton, exitButton, startButton};
        maps = new ArrayList<>();
        maps.add(testMap);

        menuBox.setPadding(new Insets(30,0,10,0));
        menuBox.setSpacing(70);
        menuBox.getChildren().add(title.text);
        menuBox.getChildren().add(mapSelectButton.imageView);
        menuBox.getChildren().add(optionsButton.imageView);
        menuBox.getChildren().add(exitButton.imageView);
        menuBox.setBackground(boxesBackground);

        Scene menuScene = new Scene(menuBox, resolutionWidth, resolutionHeight);
        Pane pane = new Pane();
        pane.getChildren().add(canvas);
        Scene gameScene = new Scene(pane, resolutionWidth, resolutionHeight);
        gameScene.setOnKeyPressed(e -> {
            keyPressed(e);
            if(e.getCode() == KeyCode.F3){
                debugMode();
            }
            if(e.getCode() == KeyCode.ESCAPE){
                paused = !paused;
            }
            screenshot(gameScene, e);
        });
        gameScene.setOnKeyReleased(this::keyReleased);
        gameScene.setOnMouseMoved(e -> mouseMoved(e, primaryStage));

        mapSelectBox.setPadding(new Insets(10));
        mapSelectBox.setHgap(10);
        mapSelectBox.setVgap(20);
        mapSelectBox.add(mapSelectBackground.imageView, 2, 0);
        mapSelectBackground.imageView.setTranslateX(-517);
        mapSelectBox.add(leftMapsBracket.imageView,0,0);
        mapSelectBox.add(rightMapsBracket.imageView, 1, 0);
        mapSelectBox.add(mainMenuButton.imageView, 2, 0);
        mapSelectBox.add(startButton.imageView, 2, 0);
        mapSelectBox.add(testMap.map, 1, 0);
        mapSelectBox.setBackground(boxesBackground);
        debugText = new Text(0, 15, "No information available yet");
        pane.getChildren().add(debugText);
        pane.setStyle("-fx-background-color: #000000;");
        Scene mapSelectScene = new Scene(mapSelectBox, resolutionWidth, resolutionHeight);
        mapSelectButton.imageView.setOnMouseClicked(e -> focusedWindow = MAP_SELECT);
        optionsButton.imageView.setOnMouseClicked(e -> focusedWindow = OPTIONS);
        mainMenuButton.imageView.setOnMouseClicked(e -> focusedWindow = MAIN_MENU);
        startButton.imageView.setOnMouseClicked(e -> {
            if(mapToLoad != null){
                focusedWindow = GAME;
                paused = false;
                if(!mapLoaded){
                    loadMap(mapToLoad);
                }
            }
        });
        testMap.map.setOnMouseClicked(e -> selectMap(testMap));
        Alert alert = new Alert(Alert.AlertType.NONE, "REALLY exit?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit");
        exitButton.imageView.setOnMouseClicked(e -> {
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                exit();
            }
        });
        title.text.setTranslateY(-title.distance);
        mapSelectButton.imageView.setTranslateY(mapSelectButton.distance);
        optionsButton.imageView.setTranslateY(optionsButton.distance);
        exitButton.imageView.setTranslateY(exitButton.distance);
        leftMapsBracket.imageView.setTranslateY(leftMapsBracket.distance);
        rightMapsBracket.imageView.setTranslateY(rightMapsBracket.distance);
        mainMenuButton.imageView.setTranslateX(870);
        startButton.imageView.setTranslateX(870);
        Timeline renderTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        event -> {
                            if(!paused && mapLoaded){
                                render(gc, canvas.getWidth(), canvas.getHeight());
                                checkKeys();
                                cycleMovement();
                            }

                            if(focusedWindow.equals(MAIN_MENU) && !rightMapsBracket.transitioning){
                                title.enterAnim();
                                mapSelectButton.enterAnim();
                                optionsButton.enterAnim();
                                exitButton.enterAnim();
                            }else{
                                title.exitAnim();
                                mapSelectButton.exitAnim();
                                optionsButton.exitAnim();
                                exitButton.exitAnim();
                            }
                            if(focusedWindow.equals(MAP_SELECT) && !mapSelectButton.transitioning){
                                if(mapToLoad == null){
                                    startButton.changeImage(startEmpty);
                                }else{
                                    startButton.changeImage(start);
                                }
                                leftMapsBracket.enterAnim();
                                rightMapsBracket.enterAnim();
                                mainMenuButton.enterAnim();
                                startButton.enterAnim();
                                if(!rightMapsBracket.transitioning){
                                    rightMapsBracket.moveRight();
                                    if(!rightMapsBracket.moving){
                                        testMap.enterAnim();
                                        mapSelectBackground.fadeIn();
                                    }
                                }
                            }else{
                                testMap.exitAnim();
                                mapSelectBackground.fadeOut();
                                if(!testMap.transitioning){
                                    rightMapsBracket.moveBackRight();
                                    if(!rightMapsBracket.moving){
                                        leftMapsBracket.exitAnim();
                                        rightMapsBracket.exitAnim();
                                        mainMenuButton.exitAnim();
                                        startButton.exitAnim();
                                    }
                                }
                            }
                            if(focusedWindow.equals(MAIN_MENU) && !(rightMapsBracket.transitioning || rightMapsBracket.moving || testMap.transitioning)){
                                if(primaryStage.getScene() != menuScene){
                                    lastWidth = primaryStage.getWidth();
                                    lastHeight = primaryStage.getHeight();
                                    primaryStage.setScene(menuScene);
                                    primaryStage.setWidth(lastWidth);
                                    primaryStage.setHeight(lastHeight);
                                }
                            }
                            if(focusedWindow.equals(MAP_SELECT) && !mapSelectButton.transitioning){
                                if(primaryStage.getScene() != mapSelectScene){
                                    lastWidth = primaryStage.getWidth();
                                    lastHeight = primaryStage.getHeight();
                                    primaryStage.setScene(mapSelectScene);
                                    primaryStage.setWidth(lastWidth);
                                    primaryStage.setHeight(lastHeight);
                                }
                            }
                            if(focusedWindow.equals(GAME) && !(rightMapsBracket.transitioning || rightMapsBracket.moving || testMap.transitioning)){
                                if(primaryStage.getScene() != gameScene){
                                    lastWidth = primaryStage.getWidth();
                                    lastHeight = primaryStage.getHeight();
                                    primaryStage.setScene(gameScene);
                                    primaryStage.setWidth(lastWidth);
                                    primaryStage.setHeight(lastHeight);
                                }
                                if(mouseInBounds && !paused){
                                    if(gameScene.getCursor() != Cursor.NONE){
                                        gameScene.setCursor(Cursor.NONE);
                                    }
                                }
                                if(paused){
                                    if(gameScene.getCursor() == Cursor.NONE){
                                        gameScene.setCursor(Cursor.DEFAULT);
                                    }
                                }
                                if(paused && gameFade < 90){
                                    gameFade += 5;
                                }else if(!paused && gameFade > 0){
                                    gameFade -= 5;
                                }
                                canvas.setOpacity(1 - (Math.sin(Math.toRadians(gameFade) * 0.5)));
                            }else{
                                if(gameScene.getCursor() == Cursor.NONE){
                                    gameScene.setCursor(Cursor.DEFAULT);
                                }
                            }
                            for (int i = 0; i < buttonList.length; i++) {
                                if(!(buttonList[i] == startButton && mapToLoad == null)){
                                    if(buttonList[i].imageView.isHover()){
                                        buttonList[i].hoverAnimUp();
                                    }else{
                                        buttonList[i].hoverAnimDown();
                                    }
                                }
                            }
                            if(testMap.map.isHover()){
                                testMap.hoverAnimUp();
                            }else{
                                testMap.hoverAnimDown();
                            }
                            testMap.select();
                            if(paused){
                                debugText.setText("|PAUSED|");
                                debugText.setFill(Color.SILVER);
                            }else{
                                debugText.setFill(Color.BLACK);
                            }
                        }
                ),
                new KeyFrame(Duration.seconds(1.0 / targetFPS))
        );
        renderTimeline.setCycleCount(Timeline.INDEFINITE);
        renderTimeline.play();
        menuScene.setOnKeyPressed(e -> screenshot(menuScene, e));
        mapSelectScene.setOnKeyPressed(e -> screenshot(mapSelectScene, e));
        primaryStage.setScene(menuScene);
        primaryStage.setTitle(TITLE + " - A-Level Project");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> exit());
    }

    public void debugMode(){
        debugMode = !debugMode;
    }

    public static void exit(){
        running = false;
        System.exit(0);
    }

    private static void draw(GraphicsContext gc, double canvasWidth, double canvasHeight, double DZ, double[][] coordsT, double distanceToScreen, double[][] distancesToPoints) {
        double[][] pointsToUse = new double[2][4];
        double blueBrightness;
        double[] blueRGB = new double[3];
        Color variableBlue;
        gc.clearRect(0,0,canvasWidth,canvasHeight);
        gc.setFill(Color.GRAY);
        gc.fillRect(0,0,canvasWidth,canvasHeight);
        boolean inFrontOfCamera;
        Quad currentQuad;
        if(debugMode){
            for (int i = 0; i < quadCount; i++) {
                inFrontOfCamera = false;
                currentQuad = world.get(i);
                if(currentQuad.centreOfQuad[2] > 0){
                    inFrontOfCamera = true;
                }
                for (int j = 0; j < 4; j++) {
                    if(inFrontOfCamera){
                        gc.setStroke(Color.DEEPPINK);
                        gc.strokeLine(0, currentQuad.pointsOnScreen[j][1] + (resolutionHeight / 2.0), canvasWidth, currentQuad.pointsOnScreen[j][1] + (resolutionHeight / 2.0));
                        gc.strokeLine(currentQuad.pointsOnScreen[j][0] + (resolutionWidth / 2.0), 0, currentQuad.pointsOnScreen[j][0] + (resolutionWidth / 2.0), canvasHeight);
                        gc.setFill(Color.YELLOW);
                        gc.fillRect(currentQuad.pointsOnScreen[j][0] - 5 + (resolutionWidth / 2.0), currentQuad.pointsOnScreen[j][1] - 5 + (resolutionHeight / 2.0), 10, 10);
                    }
                }
            }
        }

        String debugInfo;
        for (int i = 0; i < quadCount; i++) {
            inFrontOfCamera = false;
            currentQuad = world.get(i);

            if(currentQuad.centreOfQuad[2] > 0){
                inFrontOfCamera = true;
            }

            blueBrightness = 5 / currentQuad.distanceToCamera;
            if(blueBrightness > 1){
                blueBrightness = 1;
            }
            blueRGB[0] = (70 * blueBrightness) / 255;
            blueRGB[1] = (100 * blueBrightness) / 255;
            blueRGB[2] = (255 * blueBrightness) / 255;
            variableBlue = Color.color(blueRGB[0], blueRGB[1], blueRGB[2]);
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 4; k++) {
                    if(j == 0){
                        pointsToUse[j][k] = currentQuad.pointsOnScreen[k][j] + (resolutionWidth / 2.0);
                    }else{
                        pointsToUse[j][k] = currentQuad.pointsOnScreen[k][j] + (resolutionHeight / 2.0);
                    }
                }
            }
            debugInfo = "F3 - Debug mode. V" +
                    "\n (" + Math.round(currentQuad.pointsOnScreen[0][0]) + ", " + Math.round(currentQuad.pointsOnScreen[0][1]) + ") " +
                    "\n (" + Math.round(currentQuad.pointsOnScreen[1][0]) + ", " + Math.round(currentQuad.pointsOnScreen[1][1]) + ") " +
                    "\n (" + Math.round(currentQuad.pointsOnScreen[2][0]) + ", " + Math.round(currentQuad.pointsOnScreen[2][1]) + ") " +
                    "\n (" + Math.round(currentQuad.pointsOnScreen[3][0]) + ", " + Math.round(currentQuad.pointsOnScreen[3][1]) + ") " +
                    "\n DZ: " + DZ +
                    "\n (" + Math.round(coordsT[0][0]) + ", " + Math.round(coordsT[0][1]) + ", " + Math.round(coordsT[0][2]) + ") " +
                    "\n (" + Math.round(coordsT[1][0]) + ", " + Math.round(coordsT[1][1]) + ", " + Math.round(coordsT[0][2]) + ") " +
                    "\n (" + Math.round(coordsT[2][0]) + ", " + Math.round(coordsT[2][1]) + ", " + Math.round(coordsT[0][2]) + ") " +
                    "\n (" + Math.round(coordsT[3][0]) + ", " + Math.round(coordsT[3][1]) + ", " + Math.round(coordsT[0][2]) + ") " +
                    "\n (" +  cameraAngle[0] + ", " + cameraAngle[1] + ")";
            if(debugMode){
                debugText.setText(debugInfo);
            }else{
                debugText.setText("F3 - Debug mode. >");
            }
            if(inFrontOfCamera){
                gc.setFill(variableBlue);
                gc.fillPolygon(pointsToUse[0], pointsToUse[1], 4);
                gc.setStroke(variableBlue);
                gc.strokePolygon(pointsToUse[0], pointsToUse[1], 4);
            }
        }
    }

    private static void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        double[][][] pointsOnScreen = new double[quadCount][4][2];
        double[][] centresOfShapes = new double[quadCount][3];
        double[][] centresOnScreen = new double[quadCount][2];
        double[] distancesToCentres = new double[quadCount];

        double DZConstant = 0;
        double centreDZ;
        Quad editQuad;
        Quad currentQuad;

        double[][] distancesToPoints = new double[quadCount][4];
        double distanceToScreen = (resolutionWidth / 2.0) / Math.tan(Math.toRadians(fieldOfView / 2.0));
        double[][] coordsTranslated = new double[4][];

        if(cameraAngle[0] >= 360){
            cameraAngle[0] -= 360;
        }else if(cameraAngle[0] < 0){
            cameraAngle[0] += 360;
        }

        for (int i = 0; i < quadCount; i++) {
            currentQuad = world.get(i);
            for (int j = 0; j < 4; j++) {
                distancesToPoints[i][j] = Math.sqrt(((currentQuad.coordinatesInWorld[j][0]) * (currentQuad.coordinatesInWorld[j][0])) + ((currentQuad.coordinatesInWorld[j][1]) * (currentQuad.coordinatesInWorld[j][1])) + ((currentQuad.coordinatesInWorld[j][2]) * (currentQuad.coordinatesInWorld[j][2])));

                double yaw = -cameraAngle[0];
                double pitch = 90;
                double roll = cameraAngle[1];

                double cosA = Math.sin(Math.toRadians(pitch));
                double sinA = Math.cos(Math.toRadians(pitch));

                double cosB = Math.sin(Math.toRadians(roll));
                double sinB = Math.cos(Math.toRadians(roll));

                double cosC = Math.cos(Math.toRadians(yaw));
                double sinC = Math.sin(Math.toRadians(yaw));

                double[][] mA = {
                        {cosA, -sinA, 0},
                        {sinA, cosA, 0},
                        {0, 0, 1}
                };

                double[][] mB = {
                        {1, 0, 0},
                        {0, cosB, -sinB},
                        {0, sinB, cosB}
                };

                double[][] mC = {
                        {cosC, -sinC, 0},
                        {sinC, cosC, 0},
                        {0, 0, 1}
                };

                double[][] m2 = multiplyMatrices(mC, mB);
                double[][] m3 = multiplyMatrices(m2, mA);

                double px = currentQuad.coordinatesInWorld[j][0] - cameraPos[0];
                double py = currentQuad.coordinatesInWorld[j][1] - cameraPos[1];
                double pz = currentQuad.coordinatesInWorld[j][2] - cameraPos[2];

                coordsTranslated[j] = new double[3];
                coordsTranslated[j][0] = m3[0][0] * px + m3[1][0] * pz + m3[2][0] * py;
                coordsTranslated[j][1] = m3[0][1] * px + m3[1][1] * pz + m3[2][1] * py;
                coordsTranslated[j][2] = m3[0][2] * px + m3[1][2] * pz + m3[2][2] * py;

                DZConstant = distanceToScreen / (coordsTranslated[j][2]);
                if(DZConstant > 0){
                    DZConstant *= -1;
                }
                for (int k = 0; k < 2; k++) {
                    pointsOnScreen[i][j][k] = (coordsTranslated[j][k]) * DZConstant;
                }
            }
            for (int j = 0; j < 3; j++) {
                centresOfShapes[i][j] = coordsTranslated[0][j] + ((coordsTranslated[2][j] - coordsTranslated[0][j]) / 2.0);
            }
            distancesToCentres[i] = Math.sqrt(((centresOfShapes[i][0]) * (centresOfShapes[i][0]))
                    + ((centresOfShapes[i][1]) * (centresOfShapes[i][1]))
                    + ((centresOfShapes[i][2]) * (centresOfShapes[i][2])));
            centreDZ = distanceToScreen / centresOfShapes[i][2];
            for (int j = 0; j < 2; j++) {
                centresOnScreen[i][j] = centresOfShapes[i][j] * centreDZ;
            }

            editQuad = world.get(i);
            editQuad.setPointsScreen(pointsOnScreen[i]);
            editQuad.setCoordsTranslated(coordsTranslated);
            editQuad.setCentre(centresOfShapes[i]);
            editQuad.setCentreScreen(centresOnScreen[i]);
            editQuad.setDistance(distancesToCentres[i]);
            world.set(i, editQuad);
        }

        boolean sorted;
        boolean[] checks = new boolean[world.size() - 1];
        Quad tempQuad;
        int pointer = 0;
        do{
            pointer++;
            if(pointer == world.size()){
                pointer = 1;
            }
            if(world.get(pointer - 1).distanceToCamera < world.get(pointer).distanceToCamera){
                tempQuad = world.get(pointer - 1);
                world.set(pointer - 1, world.get(pointer));
                world.set(pointer, tempQuad);
            }
            checks[pointer - 1] = true;
            sorted = true;
            for (int i = 0; i < world.size() - 1; i++) {
                if(!checks[i]){
                    sorted = false;
                    break;
                }
            }
        }while(!sorted);

        draw(gc, canvasWidth, canvasHeight, DZConstant, coordsTranslated, distanceToScreen, distancesToPoints);
    }

    public static double[][] multiplyMatrices(double[][] m1, double[][] m2){
        double[][] result = new double[m1.length][m2[0].length];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = multiplyMatricesCell(m1, m2, i, j);
            }
        }

        return result;
    }

    public static double multiplyMatricesCell(double[][] m1, double[][] m2, int row, int col){
        double cell = 0;

        for (int i = 0; i < m2.length; i++) {
            cell += m1[row][i] * m2[i][col];
        }

        return cell;
    }

    private void keyPressed(KeyEvent e){
        int keyCode = e.getCode().hashCode();
        keysPressed.set(keyCode);
    }

    private void keyReleased(KeyEvent e){
        int keyCode = e.getCode().hashCode();
        keysPressed.clear(keyCode);
    }

    private void mouseMoved(MouseEvent e, Stage stage){
        mouseInBounds = (e.getScreenX() > stage.getX() && e.getScreenX() < stage.getX() + resolutionWidth)
                && (e.getScreenY() > stage.getY() && e.getScreenY() < stage.getY() + resolutionHeight + 11);

        if(mouseInBounds && !paused){
            gameMouse(stage);
        }

        cameraAngle[0] += (e.getX() - (resolutionWidth / 2.0)) / 8.0;
        double angleToAdd = -(e.getY() + 11 - (resolutionHeight / 2.0)) / 8.0;
        if(!((cameraAngle[1] >= 90 && angleToAdd > 0)
                || (cameraAngle[1] <= -90 && angleToAdd < 0))){
            cameraAngle[1] += angleToAdd;
        }


        if(cameraAngle[1] > 90){
            cameraAngle[1] = 90;
        }
        if(cameraAngle[1] < -90){
            cameraAngle[1] = -90;
        }
    }

    private void checkKeys(){
        double cosX = Math.cos(Math.toRadians(cameraAngle[0]));
        double sinX = Math.sin(Math.toRadians(cameraAngle[0]));
        double nCosX = Math.cos(Math.toRadians(-cameraAngle[0]));
        double nSinX = Math.sin(Math.toRadians(-cameraAngle[0]));

        if(keysPressed.get(KeyCode.UP.hashCode())){
            cameraDeltaUpDown[2] = -moveSpeed * cosX;
            cameraDeltaUpDown[0] = -moveSpeed * sinX;
        }
        if(keysPressed.get(KeyCode.DOWN.hashCode())){
            cameraDeltaUpDown[2] = moveSpeed * cosX;
            cameraDeltaUpDown[0] = moveSpeed * sinX;
        }
        if(keysPressed.get(KeyCode.LEFT.hashCode())){
            cameraDeltaLeftRight[0] = moveSpeed * nCosX;
            cameraDeltaLeftRight[2] = moveSpeed * nSinX;
        }
        if(keysPressed.get(KeyCode.RIGHT.hashCode())){
            cameraDeltaLeftRight[0] = -moveSpeed * nCosX;
            cameraDeltaLeftRight[2] = -moveSpeed * nSinX;
        }

        if(cameraDeltaUpDown[0] > moveSpeed){
            cameraDeltaUpDown[0] = moveSpeed;
        }else if(cameraDeltaUpDown[0] < -moveSpeed){
            cameraDeltaUpDown[0] = -moveSpeed;
        }

        if(cameraDeltaUpDown[2] > moveSpeed){
            cameraDeltaUpDown[2] = moveSpeed;
        }else if(cameraDeltaUpDown[2] < -moveSpeed){
            cameraDeltaUpDown[2] = -moveSpeed;
        }
        if(keysPressed.get(KeyCode.SPACE.hashCode())){
            cameraDeltaUpDown[1] = moveSpeed;
        }
        if(keysPressed.get(KeyCode.SHIFT.hashCode())){
            cameraDeltaUpDown[1] = -moveSpeed;
        }
//        if(keysPressed.get(KeyCode.W.hashCode())){
//            cameraAngle[1] += 2;
//        }
//        if(keysPressed.get(KeyCode.A.hashCode())){
//            cameraAngle[0] -= 2;
//        }
//        if(keysPressed.get(KeyCode.S.hashCode())){
//            cameraAngle[1] -= 2;
//        }
//        if(keysPressed.get(KeyCode.D.hashCode())){
//            cameraAngle[0] += 2;
//        }
    }

    private static void cycleMovement(){
        for (int i = 0; i < 3; i++) {
            cameraDeltaUpDown[i] *= 0.9;
            cameraDeltaLeftRight[i] *= 0.9;
            cameraPos[i] += cameraDeltaUpDown[i] + cameraDeltaLeftRight[i];
        }
    }

    private void gameMouse(Stage stage){
        double x = stage.getX();
        double y = stage.getY();
        int rX = (int) Math.round(x + (stage.getWidth() / 2.0));
        int rY = (int) Math.round(y + (stage.getHeight() / 2.0));
        try{
            Robot robot = new Robot();
            robot.mouseMove(rX, rY);
        }catch(AWTException e){
            reportError("Error moving the mouse cursor.", e);
        }
    }
}

class Quad{

    protected int ID;
    protected double[][] coordinatesInWorld;
    protected double[][] pointsOnScreen;
    protected double[][] coordsTranslated;
    protected double[] centreOfQuad;
    protected double[] centreOfQuadOnScreen;
    protected double distanceToCamera;

    public Quad(int Id, double[][] coords, double[][] points, double[] centre, double[] centreScreen, double distance){
        ID = Id;
        coordinatesInWorld = coords;
        pointsOnScreen = points;
        centreOfQuad = centre;
        centreOfQuadOnScreen = centreScreen;
        distanceToCamera = distance;
    }

    public void setPointsScreen(double[][] newPoints){
        pointsOnScreen = newPoints;
    }

    public void setCoordsTranslated(double[][] newCT){
        coordsTranslated = newCT;
    }

    public void setCentre(double[] newCentre){
        centreOfQuad = newCentre;
    }

    public void setCentreScreen(double[] newCentreScreen){
        centreOfQuadOnScreen = newCentreScreen;
    }

    public void setDistance(double newDistance){
        distanceToCamera = newDistance;
    }
}

class Object{

    protected int ID;
    protected double[] startingPos;
    protected double[] centreCoords;
    protected double[][] vertices;
    protected double[][] verticesOnScreen;
    protected double[][] faceCentres;
    protected double[] faceShades;

    public Object(int Id, double[] startP, double[][] v){
        ID = Id;
        startingPos = startP;
        centreCoords = startP;
        vertices = v;
    }

    public void setVerticesScreen(double[][] newVertices){
        verticesOnScreen = newVertices;
    }

    public void setFaceCentres(double[][] newCentres){
        faceCentres = newCentres;
    }

    public void setShades(double[] newShades){
        faceShades = newShades;
    }
}

class Light{

    protected int ID;
    protected double[] pos;
    protected double[] posOnScreen;
    protected double brightness;
    protected Color colour;

    public Light(int Id, double[] p, double b, Color c){
        ID = Id;
        pos = p;
        brightness = b;
        colour = c;
    }

    public void setPosScreen(double[] newScreenPos){
        posOnScreen = newScreenPos;
    }

    public void setBrightness(double newBrightness){
        brightness = newBrightness;
    }

    public void setColour(Color newColour){
        colour = newColour;
    }
}

class CustomImage {

    protected Image image;
    protected ImageView imageView;
    protected double raisedPixels1;
    protected double raisedPixels2;
    protected double rightPixels;
    protected double theta1;
    protected double theta2;
    protected double theta3;
    protected double theta4;
    protected double distance;
    protected double translateY;
    protected double rightDistance;
    protected double opacity;
    protected boolean transitioning = false;
    protected boolean moving = false;

    public CustomImage(Image i, double d, double rD, double op, double ty){
        image = i;
        imageView = new ImageView(i);
        raisedPixels1 = 0;
        raisedPixels2 = 0;
        rightPixels = 0;
        opacity = op;
        theta1 = 0;
        theta2 = 0;
        theta3 = 0;
        theta4 = 0;
        distance = d;
        rightDistance = rD;
        translateY = ty;
        imageView.setTranslateY(translateY);
        imageView.setOpacity(opacity);
    }

    public void changeImage(Image newImage){
        image = newImage;
        imageView.setImage(newImage);
    }

    public void enterAnim(){
        if(theta2 < 90){
            transitioning = true;
            theta2 += Main.buttonSpeed / 5.0;
            raisedPixels2 = Math.sin(Math.toRadians(theta2)) * distance;
            imageView.setTranslateY(translateY + distance -(raisedPixels1 + raisedPixels2));
        }else if(theta2 == 90){
            transitioning = false;
        }
    }

    public void exitAnim(){
        if(theta2 > 0){
            transitioning = true;
            theta2 -= Main.buttonSpeed / 5.0;
            raisedPixels2 = Math.sin(Math.toRadians(theta2)) * distance;
            imageView.setTranslateY(translateY + distance -(raisedPixels1 + raisedPixels2));
        }else if(theta2 == 0){
            transitioning = false;
        }
    }

    public void hoverAnimUp(){
        if(theta1 < 180){
            theta1 += Main.buttonSpeed;
            raisedPixels1 = ((Math.sin(Math.toRadians(theta1 - 90)) / 2) + 0.5) * 10;
            imageView.setTranslateY(translateY + distance - (raisedPixels1 + raisedPixels2));
        }
    }

    public void hoverAnimDown(){
        if(theta1 > 0){
            theta1 -= Main.buttonSpeed;
            raisedPixels1 = ((Math.sin(Math.toRadians(theta1 - 90)) / 2) + 0.5) * 10;
            imageView.setTranslateY(translateY + distance -(raisedPixels1 + raisedPixels2));
        }
    }

    public void moveRight(){
        if(theta3 < 90){
            moving = true;
            theta3 += Main.buttonSpeed / 3.0;
            rightPixels = Math.sin(Math.toRadians(theta3)) * rightDistance;
            imageView.setTranslateX(rightPixels);
        }else if(theta3 == 90){
            moving = false;
        }
    }

    public void moveBackRight(){
        if(theta3 > 0){
            moving = true;
            theta3 -= Main.buttonSpeed / 3.0;
            rightPixels = Math.sin(Math.toRadians(theta3)) * rightDistance;
            imageView.setTranslateX(rightPixels);
        }else if(theta3 == 0){
            moving = false;
        }
    }

    public void fadeIn(){
        if(theta4 < 90){
            transitioning = true;
            theta4 += Main.buttonSpeed / 3.0;
            opacity = Math.sin(Math.toRadians(theta4));
            imageView.setOpacity(opacity);
        }else if (theta4 == 90){
            transitioning = false;
        }
    }

    public void fadeOut(){
        if(theta4 > 0){
            transitioning = true;
            theta4 -= Main.buttonSpeed / 3.0;
            opacity = Math.sin(Math.toRadians(theta4));
            imageView.setOpacity(opacity);
        }else if (theta4 == 0){
            transitioning = false;
        }
    }
}
class CustomText{

    protected String string;
    protected Text text;
    protected int theta;
    protected double raisedPixels;
    protected double distance;
    protected boolean transitioning = false;

    public CustomText(String s, double d){
        string = s;
        text = new Text(s);
        distance = d;
    }

    public void enterAnim(){
        if(theta < 90){
            transitioning = true;
            theta += Main.buttonSpeed / 5.0;
            raisedPixels = Math.sin(Math.toRadians(theta)) * distance;
            text.setTranslateY(raisedPixels - distance);
        }else if(theta == 90){
            transitioning = false;
        }
    }

    public void exitAnim(){
        if(theta > 0){
            transitioning = true;
            theta -= Main.buttonSpeed;
            raisedPixels = Math.sin(Math.toRadians(theta)) * distance;
            text.setTranslateY(raisedPixels - distance);
        }else if(theta == 0){
            transitioning = false;
        }
    }
}
class ListMap{

    protected String title;
    protected String[] description;
    protected Text titleText;
    protected Text descText1;
    protected Text descText2;
    protected Image baseImage;
    protected Image baseImageSelected;
    protected ImageView base;
    protected Image iconImage;
    protected ImageView icon;
    protected int theta1;
    protected double opacity;
    protected boolean transitioning = false;
    protected Group map;
    protected int theta2;
    protected double raisedPixels;
    protected boolean selected;
    protected File mapFile;

    public ListMap(String t, String desc1, String desc2, Image b, Image bS, Image i, String pN){
        title = t;
        theta1 = 0;
        theta2 = 0;
        description = new String[]{desc1, desc2};
        opacity = 0;
        titleText = new Text(title);
        descText1 = new Text(description[0]);
        descText2 = new Text(description[1]);
        titleText.setFont(javafx.scene.text.Font.font("Monospaced", FontWeight.BOLD,20));
        titleText.setFill(Color.DARKCYAN);
        titleText.setX(70);
        titleText.setY(30);
        descText1.setFont(javafx.scene.text.Font.font("Monospaced", FontWeight.BOLD,15));
        descText1.setFill(Color.SILVER);
        descText1.setX(70);
        descText1.setY(50);
        descText2.setFont(javafx.scene.text.Font.font("Monospaced", FontWeight.BOLD,15));
        descText2.setFill(Color.SILVER);
        descText2.setX(70);
        descText2.setY(70);
        baseImage = b;
        baseImageSelected = bS;
        base = new ImageView(baseImage);
        iconImage = i;
        icon = new ImageView(iconImage);
        map = new Group(base);
        map.getChildren().add(titleText);
        map.getChildren().add(descText1);
        map.getChildren().add(descText2);
        map.getChildren().add(icon);
        map.setOpacity(opacity);
        selected = false;
        mapFile = new File(pN);
    }

    public void enterAnim(){
        if(theta1 < 90){
            transitioning = true;
            theta1 += Main.buttonSpeed / 3.0;
            opacity = Math.sin(Math.toRadians(theta1));
            map.setOpacity(opacity);
        }else if(theta1 == 90){
            transitioning = false;
        }
    }

    public void exitAnim(){
        if(theta1 > 0){
            transitioning = true;
            theta1 -= Main.buttonSpeed / 3.0;
            opacity = Math.sin(Math.toRadians(theta1));
            map.setOpacity(opacity);
        }else if(theta1 == 0){
            transitioning = false;
        }
    }

    public void hoverAnimUp(){
        if(theta2 < 180){
            theta2 += Main.buttonSpeed;
            raisedPixels = ((Math.sin(Math.toRadians(theta2 - 90)) / 2) + 0.5) * 10;
            map.setTranslateY(-raisedPixels);
        }
    }

    public void hoverAnimDown(){
        if(theta2 > 0){
            theta2 -= Main.buttonSpeed;
            raisedPixels = ((Math.sin(Math.toRadians(theta2 - 90)) / 2) + 0.5) * 10;
            map.setTranslateY(-raisedPixels);
        }
    }

    public void select(){
        if(selected){
            if(base.getImage() != baseImageSelected){
                base.setImage(baseImageSelected);
            }
        }else{
            if(base.getImage() != baseImage){
                base.setImage(baseImage);
            }
        }
    }
}