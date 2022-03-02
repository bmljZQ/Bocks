package com.company;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
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
import javafx.scene.input.MouseButton;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

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

    public static final String TITLE = "_";
    public static final String MAIN_MENU = "Main menu";
    public static final String MAP_SELECT = "Map select";
    public static final String OPTIONS = "Options";
    public static final String GAME = "Game";
    public static final String GAME_MENU = "Game menu";
    public static final String R_WORLD = "World";
    public static final String R_OBJECTS = "Objects";
    public static final String R_LIGHTS = "Lights";

    public static int buttonSpeed = 15;
    public static boolean running = false;
    public static boolean paused = true;
    public static int targetFPS = 60;
    public static int quadCount = 0;
    public static int objectCount = 0;
    public static int lightCount = 0;
    public static int triCount = 0;
    public static ArrayList<String> worldStrings;
    public static ArrayList<String> objectStrings;
    public static ArrayList<String> lightStrings;
    public static ArrayList<Quad> world;
    public static ArrayList<Object> objects;
    public static ArrayList<Light> lights;
    public static ArrayList<Tri> worldTris;
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
    public static Text loadingText = new Text("Loading:");
    public static Text loadingContext = new Text();
    public boolean mouseInBounds = false;
    public static boolean loading = false;
    public static ImageView loadingBar;
    public static ImageView emptyLoadingBar;
    public int loadsFadeOut = 0;
    public static int loadingBarAnimTheta = 0;
    public static boolean fakeLoadingTime = false;
    public static boolean editMode = false;
    public static boolean leftMouseHeld = false;
    public static boolean rightMouseHeld = false;
    public static boolean[] draggingArrow = {false,false,false};
    public static double[] mouseMovement = {0,0};
    public static String arrowInfo = null;
    public static double FPS;
    public double FPSLong;
    public double lastTime;
    public double currentTime;

    public static Quad[] axisArrows = {
            new Quad(-1, new double[][]{
                    {0.5, 0.05, 0},
                    {0.5, -0.05, 0},
                    {2, -0.05, 0},
                    {2, 0.05, 0}
            }, null, null, null, 0, Color.color(1.0, 0.0, 0.0), new int[]{0, 0}, true),
            new Quad(-2, new double[][]{
                    {2, 0.1, 0},
                    {2, 0, 0},
                    {2, -0.1, 0},
                    {2.1, 0, 0}
            }, null, null, null, 0, Color.color(1.0, 0.0, 0.0), new int[]{0, 0}, true),
            new Quad(-3, new double[][]{
                    {0.05, 0.5, 0},
                    {-0.05, 0.5, 0},
                    {-0.05, 2, 0},
                    {0.05, 2, 0}
            }, null, null, null, 0, Color.color(0.0, 1.0, 0.0), new int[]{0, 0}, true),
            new Quad(-4, new double[][]{
                    {0.1, 2, 0},
                    {0, 2, 0},
                    {-0.1, 2, 0},
                    {0, 2.1, 0}
            }, null, null, null, 0, Color.color(0.0, 1.0, 0.0), new int[]{0, 0}, true),
            new Quad(-5, new double[][]{
                    {0, 0.05, 0.5},
                    {0, -0.05, 0.5},
                    {0, -0.05, 2},
                    {0, 0.05, 2}
            }, null, null, null, 0, Color.color(0.0, 0.0, 1.0), new int[]{0, 0}, true),
            new Quad(-6, new double[][]{
                    {0, 0.1, 2},
                    {0, 0, 2},
                    {0, -0.1, 2},
                    {0, 0, 2.1}
            }, null, null, null, 0, Color.color(0.0, 0.0, 1.0), new int[]{0, 0}, true)
    };


    public static void updateProgressBar(int loadedElements, int elementsToLoad){
        double progress = (double) loadedElements / (double) elementsToLoad;
        loadingBar.setScaleX(progress);
        double fullBarWidth = loadingBar.getImage().getWidth();
        loadingBar.setTranslateX(fullBarWidth * progress * 0.47 - fullBarWidth * 0.5 + 15);
    }

    public static void updateLoadingContext(String text){
        loadingContext.setText(text);
    }

    public static void loadingBarAnimation(){
        loadingBarAnimTheta += 5;
        if(loadingBarAnimTheta >= 180){
            loadingBarAnimTheta = 0;
        }
        loadingBar.setTranslateX(0);
        loadingBar.setScaleX(Math.sin(Math.toRadians(loadingBarAnimTheta)));
    }

    public static void main(String[] args) {
        running = true;
        launch(args);
    }

    private static void loadMap(File mapFile) throws InterruptedException {
        loading = true;
        int linesRead = 0;
        loadingText.setOpacity(1);
        loadingContext.setOpacity(1);
        emptyLoadingBar.setOpacity(1);
        loadingBar.setScaleX(0);
        loadingBar.setOpacity(1);
        BufferedReader reader;
        worldStrings = new ArrayList<>();
        objectStrings = new ArrayList<>();
        lightStrings = new ArrayList<>();
        world = new ArrayList<>();
        objects = new ArrayList<>();
        lights = new ArrayList<>();
        worldTris = new ArrayList<>();
        String readLine;
        int bracketNo = 0;
        boolean endLoop = false;
        String currentlyReading = "";
        updateLoadingContext("Reading file...");

        try{
            reader = new BufferedReader(new FileReader(mapFile));
            do{
                readLine = reader.readLine().trim();
                loadingBarAnimation();
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
                if(fakeLoadingTime){
                    Thread.sleep(33);
                }
                loadingBarAnimation();
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
                linesRead++;
                if(fakeLoadingTime){
                    Thread.sleep(33);
                }
                loadingBarAnimation();
            }while(!endLoop);
        }catch(FileNotFoundException e){
            reportError("Error initialising the file reader to read the map.", e);
        }catch(IOException e){
            reportError("Error counting the lines in the map file.", e);
        }

        quadCount = worldStrings.size();
        objectCount = objectStrings.size();
        lightCount = lightStrings.size();
        triCount = worldStrings.size() * 2;

        int elementsToLoad = quadCount + objectCount + lightCount;
        int loadedElements = 0;
        String[][] tempCoordinates;
        String[][][] coordinatesString;
        String[] tempColourString;

        tempCoordinates = new String[quadCount][4];
        coordinates = new double[quadCount][4][3];
        coordinatesString = new String[quadCount][4][3];
        loadingContext.setTranslateY(resolutionHeight / 2.4 + 39);
        updateLoadingContext("Indexing world " +
                "\n geometry.");
        for (int i = 0; i < quadCount; i++) {
            //pppfpfpfpfpfpfpfpfpffpfpfppfpfpfp
            tempColourString = worldStrings.get(i).split("¦")[1].split(",");
            tempCoordinates[i] = worldStrings.get(i).split("¦")[0].split("/");
            for (int j = 0; j < 4; j++) {
                coordinatesString[i][j] = tempCoordinates[i][j].split(",");
                for (int k = 0; k < 3; k++) {
                    coordinates[i][j][k] = Double.parseDouble(coordinatesString[i][j][k]);
                }
            }
            worldTris.add(new Tri(i + 1, new double[][]{coordinates[i][0], coordinates[i][1], coordinates[i][2]}, null, null, null, 0, Color.color(Double.parseDouble(tempColourString[0]) / 255, Double.parseDouble(tempColourString[1]) / 255, Double.parseDouble(tempColourString[2]) / 255), new int[]{0,0}, false));
            worldTris.add(new Tri(i + 1, new double[][]{coordinates[i][0], coordinates[i][2], coordinates[i][3]}, null, null, null, 0, Color.color(Double.parseDouble(tempColourString[0]) / 255, Double.parseDouble(tempColourString[1]) / 255, Double.parseDouble(tempColourString[2]) / 255), new int[]{0,0}, false));
            world.add(new Quad(i + 1, coordinates[i], null, null, null, 0, Color.color(Double.parseDouble(tempColourString[0]) / 255, Double.parseDouble(tempColourString[1]) / 255, Double.parseDouble(tempColourString[2]) / 255), new int[]{0, 0}, false));
            if(fakeLoadingTime){
                Thread.sleep(100);
            }
            loadedElements++;
            updateProgressBar(loadedElements, elementsToLoad);
        }

        ArrayList<String>[] tempCoordsDynamic = new ArrayList[objectCount];
        ArrayList<double[]>[] coordsDynamic = new ArrayList[objectCount];
        ArrayList<String[]>[] coordsStringDynamic = new ArrayList[objectCount];
        String[] startPosString;
        updateLoadingContext("Indexing objects.");
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
            if(fakeLoadingTime){
                Thread.sleep(100);
            }
            loadedElements++;
            updateProgressBar(loadedElements, elementsToLoad);
        }

        double[][] pos = new double[lightCount][3];
        String[][] posString = new String[lightCount][3];
        String[][] lightData = new String[lightCount][3];
        double[] brightnesses = new double[lightCount];
        String[][] colourData = new String[lightCount][3];
        Color[] colours = new Color[lightCount];
        updateLoadingContext("Indexing lights.");
        for (int i = 0; i < lightCount; i++) {
            lightData[i] = lightStrings.get(i).split("/");
            posString[i] = lightData[i][0].split(",");
            for (int j = 0; j < 3; j++) {
                pos[i][j] = Double.parseDouble(posString[i][j]);
            }
            brightnesses[i] = Double.parseDouble(lightData[i][1]);
            colourData[i] = lightData[i][2].split(",");
            colours[i] = Color.color(Double.parseDouble(colourData[i][0]) / 255, Double.parseDouble(colourData[i][1]) / 255, Double.parseDouble(colourData[i][2]) / 255);

            lights.add(new Light(i, pos[i], brightnesses[i], colours[i]));
            if(fakeLoadingTime){
                Thread.sleep(100);
            }
            loadedElements++;
            updateProgressBar(loadedElements, elementsToLoad);
        }

        updateLoadingContext("Finished!");

        mapLoaded = true;
        loading = false;
    }

    public static void unloadMap(){
        mapLoaded = false;
        world.clear();
        worldTris.clear();
        objects.clear();
        lights.clear();
        cameraPos = new double[]{0,0,0};
        cameraAngle = new double[]{0,0};
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
        Image emptyLoadingBarImage = new Image(new FileInputStream("resources/Empty_Loading_Bar.png"));
        Image loadingBarImage = new Image(new FileInputStream("resources/Loading_JUICE.png"));

        emptyLoadingBar = new ImageView(emptyLoadingBarImage);
        loadingBar = new ImageView(loadingBarImage);

        CustomText title = new CustomText(TITLE, 300);
        loadingText.setFont(javafx.scene.text.Font.font("Monospaced", FontWeight.BOLD,30));
        loadingText.setFill(Color.WHITE);
        loadingContext.setFont(javafx.scene.text.Font.font("Monospaced", FontWeight.BOLD,15));
        loadingContext.setFill(Color.WHITE);
        title.text.setFont(javafx.scene.text.Font.font("Monospaced", FontWeight.BOLD,130));
        title.text.setFill(Color.LIMEGREEN);
        Background boxesBackground = new Background(new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize((resolutionHeight / background.getHeight()), (resolutionWidth / background.getWidth()), true, true, false, false)));

        CustomImage mainMenuButton = new CustomImage(mainMenu, resolutionHeight + 200, 0, 1, 0, -200, 1);
        CustomImage mainMenuGameButton = new CustomImage(mainMenu, 0, 0, 0, 100, 200, 2);
        CustomImage mapSelectButton = new CustomImage(mapSelect, resolutionHeight + 200, 0, 1, 0, 0, 1);
        CustomImage optionsButton = new CustomImage(options, resolutionHeight + 200, 0, 1, 0, 0, 1);
        CustomImage exitButton = new CustomImage(exit, resolutionHeight + 200, 0, 1, 0, 0, 1);
        CustomImage startButton = new CustomImage(start, resolutionHeight + 200, 0, 1, 0, 200, 1);
        CustomImage leftMapsBracket = new CustomImage(leftMapsBracketImage, resolutionHeight + 200, 0, 1, 0, 0, 1);
        CustomImage rightMapsBracket = new CustomImage(rightMapsBracketImage, resolutionHeight + 200, 1100, 1, 0, 0, 1);
        CustomImage leftMenuBracket = new CustomImage(leftMapsBracketImage, resolutionHeight + 200, 0, 1, 10, 10, 2);
        CustomImage rightMenuBracket = new CustomImage(rightMapsBracketImage, resolutionHeight + 200, 1050, 1, 200, 10, 2);
        CustomImage mapSelectBackground = new CustomImage(mapSelectBackgroundImage, resolutionHeight + 200, 0, 0, 0, 0, 1);
        CustomImage gameMenuBackground = new CustomImage(mapSelectBackgroundImage, 0, 0, 0, 36, 11, 2);

        ListMap testMap = new ListMap("Test Map", "Just a test map.", "Used for...testing.", mapBase, mapBaseSelected, defaultMapIcon, "maps/testMap.mfb");
        buttonList = new CustomImage[]{mainMenuButton, mapSelectButton, optionsButton, exitButton, startButton, mainMenuGameButton};
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
                if(paused){
                    focusedWindow = GAME;
                }else{
                    focusedWindow = GAME_MENU;
                }
                paused = !paused;
            }
            if(e.getCode() == KeyCode.E){
                editMode = !editMode;
            }
            screenshot(gameScene, e);
        });
        gameScene.setOnKeyReleased(this::keyReleased);
        gameScene.setOnMouseMoved(e -> mouseMoved(e, primaryStage));
        gameScene.setOnMouseDragged(e -> mouseMoved(e, primaryStage));
        gameScene.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                leftMouseHeld = true;
            }
            if(e.getButton() == MouseButton.SECONDARY){
                rightMouseHeld = true;
            }
        });
        gameScene.setOnMouseReleased(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                leftMouseHeld = false;
            }
            if(e.getButton() == MouseButton.SECONDARY){
                rightMouseHeld = false;
            }
        });

        mapSelectBox.setPadding(new Insets(10));
        mapSelectBox.setHgap(10);
        mapSelectBox.setVgap(20);
        mapSelectBox.add(mapSelectBackground.imageView, 2, 0);
        mapSelectBackground.imageView.setTranslateX(-603);
        mapSelectBox.add(leftMapsBracket.imageView,0,0);
        mapSelectBox.add(rightMapsBracket.imageView, 1, 0);
        mapSelectBox.add(mainMenuButton.imageView, 2, 0);
        mapSelectBox.add(startButton.imageView, 2, 0);
        mapSelectBox.add(testMap.map, 1, 0);
        mapSelectBox.add(loadingText, 1,0);
        mapSelectBox.add(loadingContext, 1, 0);
        mapSelectBox.add(emptyLoadingBar, 1, 0);
        mapSelectBox.add(loadingBar, 1, 0);
        mapSelectBox.setBackground(boxesBackground);

        pane.getChildren().add(gameMenuBackground.imageView);
        pane.getChildren().add(mainMenuGameButton.imageView);
        pane.getChildren().add(leftMenuBracket.imageView);
        pane.getChildren().add(rightMenuBracket.imageView);

        debugText = new Text(0, 15, "No information available yet");
        pane.getChildren().add(debugText);
        pane.setStyle("-fx-background-color: #000000;");
        Scene mapSelectScene = new Scene(mapSelectBox, resolutionWidth, resolutionHeight);
        mapSelectButton.imageView.setOnMouseClicked(e -> focusedWindow = MAP_SELECT);
        optionsButton.imageView.setOnMouseClicked(e -> focusedWindow = OPTIONS);
        mainMenuButton.imageView.setOnMouseClicked(e -> focusedWindow = MAIN_MENU);
        mainMenuGameButton.imageView.setOnMouseClicked(e -> focusedWindow = MAIN_MENU);
        startButton.imageView.setOnMouseClicked(e -> {
            if(mapToLoad != null){
                focusedWindow = GAME;
                paused = false;
                if(!mapLoaded){
                    new Thread(new Task<Void>(){
                        @Override
                        protected Void call() throws Exception {
                            loadingText.setOpacity(1);
                            loadingContext.setOpacity(1);
                            emptyLoadingBar.setOpacity(1);
                            loadingBar.setScaleX(0);
                            loadingBar.setOpacity(1);
                            loadMap(mapToLoad);
                            return null;
                        }
                    }).start();
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
        mainMenuButton.imageView.setTranslateX(785);
        startButton.imageView.setTranslateX(785);
        mainMenuGameButton.imageView.setTranslateX(mainMenuGameButton.translateX);
        mainMenuGameButton.imageView.setTranslateY(mainMenuGameButton.translateY);
        gameMenuBackground.imageView.setTranslateX(gameMenuBackground.translateX);
        gameMenuBackground.imageView.setTranslateY(gameMenuBackground.translateY);
        leftMenuBracket.imageView.setTranslateX(leftMenuBracket.translateX);
        leftMenuBracket.imageView.setTranslateY(leftMenuBracket.distance);
        rightMenuBracket.imageView.setTranslateY(rightMenuBracket.distance);
        rightMenuBracket.imageView.setTranslateX(rightMenuBracket.translateX);
        loadingText.setTranslateX(-150);
        loadingText.setTranslateY(resolutionHeight / 2.4);
        loadingContext.setTranslateX(-150);
        loadingContext.setTranslateY(resolutionHeight / 2.4 + 20);
        emptyLoadingBar.setTranslateY(resolutionHeight / 2.4);
        loadingBar.setTranslateX(0);
        loadingBar.setTranslateY(resolutionHeight / 2.4);

        loadingText.setOpacity(0);
        loadingContext.setOpacity(0);
        emptyLoadingBar.setOpacity(0);
        loadingBar.setOpacity(0);

        Timeline renderTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1.0 / targetFPS),
                        event -> {
                            lastTime = System.nanoTime();
                            if(!paused && mapLoaded){
                                checkKeys();
                                cycleMovement();
                                render(gc, canvas.getWidth(), canvas.getHeight());
                            }

                            if(focusedWindow.equals(MAIN_MENU) && !rightMapsBracket.transitioning){
                                title.enterAnim();
                                mapSelectButton.enterAnim();
                                optionsButton.enterAnim();
                                exitButton.enterAnim();
                                if(mapLoaded){
                                    unloadMap();
                                }
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
                                if(loadsFadeOut < 90 && mapLoaded){
                                    loadsFadeOut += 2;
                                }
                                if(mapLoaded) {
                                    loadingText.setOpacity(Math.sin(Math.toRadians(90 - loadsFadeOut)));
                                    emptyLoadingBar.setOpacity(Math.sin(Math.toRadians(90 - loadsFadeOut)));
                                    loadingBar.setOpacity(Math.sin(Math.toRadians(90 - loadsFadeOut)));
                                    loadingContext.setOpacity(Math.sin(Math.toRadians(90 - loadsFadeOut)));
                                }
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
                            if(focusedWindow.equals(GAME) && !(rightMapsBracket.transitioning || rightMapsBracket.moving || testMap.transitioning) && mapLoaded){
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
                            }else{
                                if(gameScene.getCursor() == Cursor.NONE){
                                    gameScene.setCursor(Cursor.DEFAULT);
                                }
                            }
                            if(focusedWindow.equals(GAME_MENU)){
                                leftMenuBracket.enterAnim();
                                rightMenuBracket.enterAnim();
                                if(!rightMenuBracket.transitioning){
                                    rightMenuBracket.moveRight();
                                    if(!rightMenuBracket.moving){
                                        gameMenuBackground.fadeIn();
                                        mainMenuGameButton.fadeIn();
                                    }
                                }
                            }else{
                                gameMenuBackground.fadeOut();
                                mainMenuGameButton.fadeOut();
                                if(!mainMenuGameButton.transitioning){
                                    rightMenuBracket.moveBackRight();
                                    if(!rightMenuBracket.moving){
                                        leftMenuBracket.exitAnim();
                                        rightMenuBracket.exitAnim();
                                    }
                                }
                            }
                            if(paused && gameFade < 90){
                                gameFade += 5;
                            }else if(!paused && gameFade > 0){
                                gameFade -= 5;
                            }
                            canvas.setOpacity(1 - (Math.sin(Math.toRadians(gameFade) * 0.5)));
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

                            currentTime = System.nanoTime();
                            FPSLong = 1.0 / ((currentTime - lastTime) / 1000000000.0);
                            FPS = (Math.round(FPSLong * 10)) / 10.0;
                        }
                )
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

    public static Quad findQuad(int ID){
        Quad currentQuad;
        for (int i = 0; i < world.size(); i++) {
            currentQuad = world.get(i);
            if(currentQuad.ID == ID){
                return currentQuad;
            }
        }
        return null;
    }

    private static void draw(GraphicsContext gc, double canvasWidth, double canvasHeight, double DZ, double[][] coordsT, double distanceToScreen, double[][] distancesToPoints) {
        ArrayList<double[]> overlaps = new ArrayList<>();


        double[][] pointsToUse = new double[2][3];
        double[][] aPointsToUse = new double[2][4];
        double colourBrightness;
        double[] colourRGB = new double[3];
        Color variableColour;
        gc.clearRect(0,0,canvasWidth,canvasHeight);
        gc.setFill(Color.GRAY);
        gc.fillRect(0,0,canvasWidth,canvasHeight);
        boolean inFrontOfCamera;
        Tri currentTri;
        if(debugMode){
            for (int i = 0; i < triCount; i++) {
                inFrontOfCamera = false;
                currentTri = worldTris.get(i);
                if(!currentTri.hidden){
                    if(currentTri.centreOfTri[2] > 0){
                        inFrontOfCamera = true;
                    }
                    for (int j = 0; j < 3; j++) {
                        if(inFrontOfCamera){
                            gc.setStroke(Color.DEEPPINK);
                            gc.strokeLine(0, currentTri.pointsOnScreen[j][1] + (resolutionHeight / 2.0), canvasWidth, currentTri.pointsOnScreen[j][1] + (resolutionHeight / 2.0));
                            gc.strokeLine(currentTri.pointsOnScreen[j][0] + (resolutionWidth / 2.0), 0, currentTri.pointsOnScreen[j][0] + (resolutionWidth / 2.0), canvasHeight);
                            gc.setFill(Color.YELLOW);
                            gc.fillRect(currentTri.pointsOnScreen[j][0] - 5 + (resolutionWidth / 2.0), currentTri.pointsOnScreen[j][1] - 5 + (resolutionHeight / 2.0), 10, 10);
                        }
                    }
                }
            }
        }
        String debugInfo;
        String debugString;
        for (int i = 0; i < triCount; i++) {
            inFrontOfCamera = false;
            currentTri = worldTris.get(i);

            if(!currentTri.hidden){
                if(currentTri.centreOfTri[2] > 0){
                    inFrontOfCamera = true;
                }

                colourBrightness = 5 / currentTri.distanceToCamera;
                if(colourBrightness > 1){
                    colourBrightness = 1;
                }
                colourRGB[0] = currentTri.colour.getRed() * colourBrightness;
                colourRGB[1] = currentTri.colour.getGreen() * colourBrightness;
                colourRGB[2] = currentTri.colour.getBlue() * colourBrightness;
                variableColour = Color.color(colourRGB[0], colourRGB[1], colourRGB[2]);
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 3; k++) {
                        if(j == 0){
                            pointsToUse[j][k] = currentTri.pointsOnScreen[k][j] + (resolutionWidth / 2.0);
                        }else{
                            pointsToUse[j][k] = currentTri.pointsOnScreen[k][j] + (resolutionHeight / 2.0);
                        }
                    }
                }
//                debugInfo = "FPS: " + FPS +
//                        "\n F3 - Debug mode. V" +
//                        "\n (" + Math.round(currentTri.pointsOnScreen[0][0]) + ", " + Math.round(currentTri.pointsOnScreen[0][1]) + ") " +
//                        "\n (" + Math.round(currentTri.pointsOnScreen[1][0]) + ", " + Math.round(currentTri.pointsOnScreen[1][1]) + ") " +
//                        "\n (" + Math.round(currentTri.pointsOnScreen[2][0]) + ", " + Math.round(currentTri.pointsOnScreen[2][1]) + ") " +
//                        "\n DZ: " + DZ +
//                        "\n (" + Math.round(coordsT[0][0]) + ", " + Math.round(coordsT[0][1]) + ", " + Math.round(coordsT[0][2]) + ") " +
//                        "\n (" + Math.round(coordsT[1][0]) + ", " + Math.round(coordsT[1][1]) + ", " + Math.round(coordsT[0][2]) + ") " +
//                        "\n (" + Math.round(coordsT[2][0]) + ", " + Math.round(coordsT[2][1]) + ", " + Math.round(coordsT[0][2]) + ") " +
//                        "\n (" +  cameraAngle[0] + ", " + cameraAngle[1] + ") " +
//                        "\n {" + draggingArrow[0] + ", " + draggingArrow[1] + ", " + draggingArrow[2] + "}" +
//                        "\n Intersection Points: " + currentTri.iPointsOnScreen.length;
//                debugString = debugInfo;
//                if(debugMode){
//                    debugString = debugInfo + "\n \n" + arrowInfo;
//                    debugText.setText(debugString);
//                }else{
//                    debugText.setText("FPS: " + FPS +
//                            "\n F3 - Debug mode. >");
//                }
                debugText.setText("");

                gc.setStroke(Color.WHITE);
                gc.strokeLine(resolutionWidth / 2.0, resolutionHeight / 2.0, resolutionWidth / 2.0, resolutionHeight / 2.0);
                if(inFrontOfCamera){
                    gc.setFill(variableColour);
                    gc.fillPolygon(pointsToUse[0], pointsToUse[1], 3);
                    if(debugMode){
                        gc.setStroke(Color.WHITE);
                    }else{
                        gc.setStroke(variableColour);
                    }
                    gc.strokePolygon(pointsToUse[0], pointsToUse[1], 3);
//                    if(currentTri.iPointsOnScreen.length > 3){
//                        for (int j = 0; j < 3; j++) {
//                            gc.fillRect(currentTri.iPointsOnScreen[j][0] - 5, currentTri.iPointsOnScreen[j][1] - 5, 10, 10);
//                        }
//                    }else{
//
//                    }


                    for (int j = 0; j < 3; j++) {
                        if(editMode){
                            if(distancesToPoints[i][j] <= 30){
                                // If statement condition to check if the centre of the screen is within a point's highlight box.
                                if((currentTri.pointsOnScreen[j][0] - 10 < 0 && currentTri.pointsOnScreen[j][0] + 10 > 0)
                                        && (currentTri.pointsOnScreen[j][1] - 10 < 0 && currentTri.pointsOnScreen[j][1] + 10 > 0)){
                                    if(leftMouseHeld || currentTri.selectedPoint == j + 1){
                                        gc.setFill(Color.PURPLE);
                                        currentTri.selectedPoint = j + 1;
                                    }else{
                                        gc.setFill(Color.LIME);
                                    }
                                    worldTris.set(i, currentTri);
                                }else{
                                    if(currentTri.selectedPoint == j + 1){
                                        gc.setFill(Color.PURPLE);
                                    }else{
                                        gc.setFill(Color.YELLOW);
                                    }
                                    if(rightMouseHeld){
                                        currentTri.selectedPoint = 0;
                                    }
                                }
                            }else{
                                gc.setFill(Color.RED);
                            }
                            // Drawing method to highlight points.
                            gc.fillRect(currentTri.pointsOnScreen[j][0] - 10 + (resolutionWidth / 2.0), currentTri.pointsOnScreen[j][1] - 10 + (resolutionHeight / 2.0), 20, 20);
                        }else{
                            currentTri.selectedPoint = 0;
                        }
                    }
                }
            }
        }


        Quad aCurrentQuad;
        Quad aOtherQuad;

        boolean aInFrontOfCamera;
        for (int i = 0; i < axisArrows.length; i++) {
            aInFrontOfCamera = false;
            aCurrentQuad = axisArrows[i];
            if(i % 2 == 0){
                aOtherQuad = axisArrows[i + 1];
            }else{
                aOtherQuad = axisArrows[i - 1];
            }

            if(!aCurrentQuad.hidden){
                if(aCurrentQuad.centreOfQuad[2] > 0){
                    aInFrontOfCamera = true;
                }

                colourBrightness = 5 / aCurrentQuad.distanceToCamera;
                if(colourBrightness > 1){
                    colourBrightness = 1;
                }
                colourRGB[0] = aCurrentQuad.colour.getRed() * colourBrightness;
                colourRGB[1] = aCurrentQuad.colour.getGreen() * colourBrightness;
                colourRGB[2] = aCurrentQuad.colour.getBlue() * colourBrightness;
                variableColour = Color.color(colourRGB[0], colourRGB[1], colourRGB[2]);

                if(Math.sqrt((aCurrentQuad.centreOfQuadOnScreen[0] * aCurrentQuad.centreOfQuadOnScreen[0] * 2) + (aCurrentQuad.centreOfQuadOnScreen[1] * aCurrentQuad.centreOfQuadOnScreen[1] * 2)) <= 100 * (15 / aCurrentQuad.distanceToCamera)
                    || Math.sqrt((aOtherQuad.centreOfQuadOnScreen[0] * aOtherQuad.centreOfQuadOnScreen[0] * 2) + (aOtherQuad.centreOfQuadOnScreen[1] * aOtherQuad.centreOfQuadOnScreen[1] * 2)) <= 100 * (15 / aOtherQuad.distanceToCamera)){
                    variableColour = variableColour.brighter().brighter().brighter();
                    if(leftMouseHeld){
                        variableColour = variableColour.invert();
                        draggingArrow[(int) Math.round((i + 1) / 2.0) - 1] = true;
                    }else{
                        draggingArrow[(int) Math.round((i + 1) / 2.0) - 1] = false;
                    }
                }

                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 4; k++) {
                        if(j == 0){
                            aPointsToUse[j][k] = aCurrentQuad.pointsOnScreen[k][j] + (resolutionWidth / 2.0);
                        }else{
                            aPointsToUse[j][k] = aCurrentQuad.pointsOnScreen[k][j] + (resolutionHeight / 2.0);
                        }
                    }
                }

                gc.setStroke(Color.WHITE);
                gc.strokeLine(resolutionWidth / 2.0, resolutionHeight / 2.0, resolutionWidth / 2.0, resolutionHeight / 2.0);
                if(aInFrontOfCamera){
                    gc.setFill(variableColour);
                    gc.fillPolygon(aPointsToUse[0], aPointsToUse[1], 4);
                    gc.setStroke(variableColour);
                    gc.strokePolygon(aPointsToUse[0], aPointsToUse[1], 4);
                }
            }
        }
//        gc.setFill(Color.WHITE);
//        for (int i = 0; i < triCount; i++) {
//            currentTri = worldTris.get(i);
//            if(currentTri.ID == 1){
//                for (int j = 0; j < currentTri.iPointsOnScreen.length; j++) {
//                    gc.fillOval(currentTri.iPointsOnScreen[j][0] - 5, currentTri.iPointsOnScreen[j][1] - 5, 10, 10);
//                }
//            }
//        }

    }

    private static void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        double[][][] pointsOnScreen = new double[triCount][3][2];
        double[][] centresOfShapes = new double[triCount][3];
        double[][] centresOnScreen = new double[triCount][2];
        double[] distancesToCentres = new double[triCount];

        double DZConstant = 0;
        double centreDZ;
        Quad editQuad;
        Quad currentQuad;
        Tri editTri;
        Tri currentTri;

        double[][] distancesToPoints = new double[triCount][3];
        double[][] distancesToArrowPoints = new double[axisArrows.length][4];
        double distanceToScreen = (resolutionWidth / 2.0) / Math.tan(Math.toRadians(fieldOfView / 2.0));
        double[][] coordsTranslated = new double[3][];
        double[][] arrowCoordsTranslated = new double[4][];
        boolean pointSelected = false;

        if(cameraAngle[0] >= 360){
            cameraAngle[0] -= 360;
        }else if(cameraAngle[0] < 0){
            cameraAngle[0] += 360;
        }

        for (int i = 0; i < triCount; i++) {
            currentTri = worldTris.get(i);
            editTri = worldTris.get(i);
            for (int j = 0; j < 3; j++) {
                distancesToPoints[i][j] = Math.sqrt(((currentTri.coordinatesInWorld[j][0] - cameraPos[0]) * (currentTri.coordinatesInWorld[j][0] - cameraPos[0])) + ((currentTri.coordinatesInWorld[j][1] - cameraPos[1]) * (currentTri.coordinatesInWorld[j][1] - cameraPos[1])) + ((currentTri.coordinatesInWorld[j][2] - cameraPos[2]) * (currentTri.coordinatesInWorld[j][2] - cameraPos[2])));

//                if(currentQuad.selectedPoint == j + 1){
//                    double yaw = -cameraAngle[0];
//                    double pitch = 90;
//                    double roll = cameraAngle[1];
//
//                    double cosA = Math.sin(Math.toRadians(pitch));
//                    double sinA = Math.cos(Math.toRadians(pitch));
//
//                    double cosB = Math.sin(Math.toRadians(roll));
//                    double sinB = Math.cos(Math.toRadians(roll));
//
//                    double cosC = Math.cos(Math.toRadians(yaw));
//                    double sinC = Math.sin(Math.toRadians(yaw));
//
//                    double[][] mA = {
//                            {cosA, -sinA, 0},
//                            {sinA, cosA, 0},
//                            {0, 0, 1}
//                    };
//
//                    double[][] mB = {
//                            {1, 0, 0},
//                            {0, cosB, -sinB},
//                            {0, sinB, cosB}
//                    };
//
//                    double[][] mC = {
//                            {cosC, -sinC, 0},
//                            {sinC, cosC, 0},
//                            {0, 0, 1}
//                    };
//
//                    double[][] m2 = multiplyMatrices(mC, mB);
//                    double[][] m3 = multiplyMatrices(m2, mA);
//
//                    double px = currentQuad.coordinatesInWorld[j][0] - cameraPos[0];
//                    double py = currentQuad.coordinatesInWorld[j][1] - cameraPos[1];
//                    double pz = currentQuad.coordinatesInWorld[j][2] - cameraPos[2];
//
//                    coordsTranslated[j] = new double[3];
//                    coordsTranslated[j][0] = m3[0][0] * px + m3[1][0] * pz + m3[2][0] * py;
//                    coordsTranslated[j][1] = m3[0][1] * px + m3[1][1] * pz + m3[2][1] * py;
//                    coordsTranslated[j][2] = m3[0][2] * px + m3[1][2] * pz + m3[2][2] * py;
//
//                    currentQuad.pointsMovement[j][0] = coordsTranslated[j][0];
//                    currentQuad.pointsMovement[j][1] = coordsTranslated[j][1];
//                    currentQuad.pointsMovement[j][2] = coordsTranslated[j][2] - distancesToPoints[i][j];
//
//                    coordsTranslated[j][0] = 0;
//                    coordsTranslated[j][1] = 0;
//                    coordsTranslated[j][2] = distancesToPoints[i][j];
//                }

                double yaw = -cameraAngle[0];
                double pitch = 90;
                double roll = cameraAngle[1];

//                if(currentQuad.selectedPoint == j + 1){
//                    yaw = cameraAngle[0];
//                    roll = -cameraAngle[1];
//                }

                double cosA = Math.sin(Math.toRadians(pitch));
                double sinA = Math.cos(Math.toRadians(pitch));

                double cosB = Math.sin(Math.toRadians(roll));
                double sinB = Math.cos(Math.toRadians(roll));

                double cosC = Math.cos(Math.toRadians(yaw));
                double sinC = Math.sin(Math.toRadians(yaw));

                double[][] mA = new double[][]{
                        {cosA, -sinA, 0},
                        {sinA, cosA, 0},
                        {0, 0, 1}
                };

                double[][] mB = new double[][]{
                        {1, 0, 0},
                        {0, cosB, -sinB},
                        {0, sinB, cosB}
                };

                double[][] mC = new double[][]{
                        {cosC, -sinC, 0},
                        {sinC, cosC, 0},
                        {0, 0, 1}
                };

                double[][] m2 = multiplyMatrices(mC, mB);
                double[][] m3 = multiplyMatrices(m2, mA);

                if(currentTri.anchoredTo[0] > 0){
                    for (int k = 0; k < worldTris.size(); k++) {
                        if(worldTris.get(k).ID == currentTri.anchoredTo[0]){
                            for (int l = 0; l < 3; l++) {
                                currentTri.pointsMovement[j][l] = worldTris.get(k).coordinatesInWorld[currentTri.anchoredTo[1] - 1][l];
                            }
                        }
                    }
                }

                if(!draggingArrow[0] && !draggingArrow[2]){
                    mouseMovement[0] = 0;
                }

                if(!draggingArrow[1]){
                    mouseMovement[1] = 0;
                }

                if(!draggingArrow[0]){
                    currentTri.oldPointsMovement[j][0] = currentTri.pointsMovement[j][0];
                }
                if(!draggingArrow[1]){
                    currentTri.oldPointsMovement[j][1] = currentTri.pointsMovement[j][1];
                }
                if(!draggingArrow[2]){
                    currentTri.oldPointsMovement[j][2] = currentTri.pointsMovement[j][2];
                }

                if(currentTri.selectedPoint == j + 1){
                    if(draggingArrow[0]){
                        if(cameraAngle[0] >= 90 && cameraAngle[0] < 270){
                            currentTri.pointsMovement[j][0] = currentTri.oldPointsMovement[j][0] + mouseMovement[0] / 5;
                        }else{
                            currentTri.pointsMovement[j][0] = currentTri.oldPointsMovement[j][0] - mouseMovement[0] / 5;
                        }
                    }else if(draggingArrow[1]){
                        currentTri.pointsMovement[j][1] = currentTri.oldPointsMovement[j][1] + mouseMovement[1] / 5;
                    }else if(draggingArrow[2]){
                        if(cameraAngle[0] >= 180 && cameraAngle[0] < 360){
                            currentTri.pointsMovement[j][2] = currentTri.oldPointsMovement[j][2] - mouseMovement[0] / 5;
                        }else{
                            currentTri.pointsMovement[j][2] = currentTri.oldPointsMovement[j][2] + mouseMovement[0] / 5;
                        }
                    }
                }

                double px = currentTri.coordinatesInWorld[j][0] - cameraPos[0] + currentTri.pointsMovement[j][0];
                double py = currentTri.coordinatesInWorld[j][1] - cameraPos[1] + currentTri.pointsMovement[j][1];
                double pz = currentTri.coordinatesInWorld[j][2] - cameraPos[2] + currentTri.pointsMovement[j][2];

                if(currentTri.selectedPoint == j + 1){
                    arrowInfo = draggingArrow[0] + ", " + draggingArrow[1] + ", " + draggingArrow[2] +
                            "\n" + px + ", " + py + ", " + pz +
                            "\n (" + currentTri.pointsMovement[j][0] + ", " + currentTri.pointsMovement[j][1] + ", " + currentTri.pointsMovement[j][2] + "), (" + currentTri.oldPointsMovement[j][0] + ", " + currentTri.oldPointsMovement[j][1] + ", " + currentTri.oldPointsMovement[j][2] + ")" +
                            "\n (" + cameraPos[0] + ", " + cameraPos[1] + ", " + cameraPos[2] + ")" +
                            "\n (" + currentTri.coordinatesInWorld[j][0] + ", " + currentTri.coordinatesInWorld[j][1] + ", " + currentTri.coordinatesInWorld[j][2] + ")" +
                            "\n " + mouseMovement[0] + ", " + mouseMovement[1] + ".";
                }

//                px = currentQuad.coordinatesInWorld[j][0] - cameraPos[0] + currentQuad.pointsMovement[j][0];
//                py = currentQuad.coordinatesInWorld[j][1] - cameraPos[1] + currentQuad.pointsMovement[j][1];
//                pz = currentQuad.coordinatesInWorld[j][2] - cameraPos[2] + currentQuad.pointsMovement[j][2];

                coordsTranslated[j] = new double[3];
                double a = m3[0][0];
                double b = m3[1][0];
                double c = m3[2][0];
                double d = m3[0][1];
                double e = m3[1][1];
                double f = m3[2][1];
                double g = m3[0][2];
                double h = m3[1][2];
                double ii = m3[2][2];
                coordsTranslated[j][0] = (a * px + b * pz + c * py);
                coordsTranslated[j][1] = (d * px + e * pz + f * py);
                coordsTranslated[j][2] = (g * px + h * pz + ii * py);

                if(currentTri.selectedPoint == j + 1){
                    pointSelected = true;
                    for (int k = 0; k < 6; k++) {
                        axisArrows[k].anchoredTo = new int[]{currentTri.ID, j + 1};
                        axisArrows[k].hidden = false;
                    }
                }

//                if(currentQuad.selectedPoint == j + 1){
////                    currentQuad.pointsMovement[j][1] = (d * b * a * coordsTranslated[j][2] - a * a * e * coordsTranslated[j][2] + g * a * e * coordsTranslated[j][0]
////                            - g * d * b * coordsTranslated[j][0] - g * a * b * coordsTranslated[j][1] + a * a * h * coordsTranslated[j][1] - a * d * h * coordsTranslated[j][0]
////                            - g * b * d * coordsTranslated[j][0]) / (d * a * c * h + g * b * d * c - g * b * a * f - a * a * f * h + a * a * ii * e - g * c * a * e + g * c * d * b - d * a * ii * b);
////                    currentQuad.pointsMovement[j][0] = (coordsTranslated[j][0] - b * (a * coordsTranslated[j][1] - d * coordsTranslated[j][0] + d * c * currentQuad.pointsMovement[j][1] - a * f * currentQuad.pointsMovement[j][1]) - c * currentQuad.pointsMovement[j][1]) / a;
////
////                    currentQuad.pointsMovement[j][2] = coordsTranslated[j][2] - distancesToPoints[i][j];
//
//
//                    coordsTranslated[j][0] = 0;
//                    coordsTranslated[j][1] = 0;
//                    coordsTranslated[j][2] = distancesToPoints[i][j];
//                }

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



            editTri.setPointsScreen(pointsOnScreen[i]);
            editTri.setCoordsTranslated(coordsTranslated);
            editTri.setCentre(centresOfShapes[i]);
            editTri.setCentreScreen(centresOnScreen[i]);
            editTri.setDistance(distancesToCentres[i]);
            worldTris.set(i, editTri);
        }

        if(!pointSelected){
            for (int k = 0; k < 6; k++) {
                axisArrows[k].anchoredTo = new int[]{0, 0};
                axisArrows[k].hidden = true;
            }
        }
        double[][][] aPointsOnScreen = new double[quadCount][4][2];
        double[][] aCentresOnScreen = new double[quadCount][2];
        double[] aDistancesToCentres = new double[quadCount];
        double[][] arrowCentresOfShapes = new double[axisArrows.length][3];
        double aDZConstant;
        double aCentreDZ;
        Quad aCurrentQuad;
        for (int i = 0; i < axisArrows.length; i++) {
            aCurrentQuad = axisArrows[i];
            for (int j = 0; j < 4; j++) {
                distancesToArrowPoints[i][j] = Math.sqrt(((aCurrentQuad.coordinatesInWorld[j][0] - cameraPos[0]) * (aCurrentQuad.coordinatesInWorld[j][0] - cameraPos[0])) + ((aCurrentQuad.coordinatesInWorld[j][1] - cameraPos[1]) * (aCurrentQuad.coordinatesInWorld[j][1] - cameraPos[1])) + ((aCurrentQuad.coordinatesInWorld[j][2] - cameraPos[2]) * (aCurrentQuad.coordinatesInWorld[j][2] - cameraPos[2])));

//                if(currentQuad.selectedPoint == j + 1){
//                    double yaw = -cameraAngle[0];
//                    double pitch = 90;
//                    double roll = cameraAngle[1];
//
//                    double cosA = Math.sin(Math.toRadians(pitch));
//                    double sinA = Math.cos(Math.toRadians(pitch));
//
//                    double cosB = Math.sin(Math.toRadians(roll));
//                    double sinB = Math.cos(Math.toRadians(roll));
//
//                    double cosC = Math.cos(Math.toRadians(yaw));
//                    double sinC = Math.sin(Math.toRadians(yaw));
//
//                    double[][] mA = {
//                            {cosA, -sinA, 0},
//                            {sinA, cosA, 0},
//                            {0, 0, 1}
//                    };
//
//                    double[][] mB = {
//                            {1, 0, 0},
//                            {0, cosB, -sinB},
//                            {0, sinB, cosB}
//                    };
//
//                    double[][] mC = {
//                            {cosC, -sinC, 0},
//                            {sinC, cosC, 0},
//                            {0, 0, 1}
//                    };
//
//                    double[][] m2 = multiplyMatrices(mC, mB);
//                    double[][] m3 = multiplyMatrices(m2, mA);
//
//                    double px = currentQuad.coordinatesInWorld[j][0] - cameraPos[0];
//                    double py = currentQuad.coordinatesInWorld[j][1] - cameraPos[1];
//                    double pz = currentQuad.coordinatesInWorld[j][2] - cameraPos[2];
//
//                    coordsTranslated[j] = new double[3];
//                    coordsTranslated[j][0] = m3[0][0] * px + m3[1][0] * pz + m3[2][0] * py;
//                    coordsTranslated[j][1] = m3[0][1] * px + m3[1][1] * pz + m3[2][1] * py;
//                    coordsTranslated[j][2] = m3[0][2] * px + m3[1][2] * pz + m3[2][2] * py;
//
//                    currentQuad.pointsMovement[j][0] = coordsTranslated[j][0];
//                    currentQuad.pointsMovement[j][1] = coordsTranslated[j][1];
//                    currentQuad.pointsMovement[j][2] = coordsTranslated[j][2] - distancesToPoints[i][j];
//
//                    coordsTranslated[j][0] = 0;
//                    coordsTranslated[j][1] = 0;
//                    coordsTranslated[j][2] = distancesToPoints[i][j];
//                }

                double yaw = -cameraAngle[0];
                double pitch = 90;
                double roll = cameraAngle[1];

//                if(currentQuad.selectedPoint == j + 1){
//                    yaw = cameraAngle[0];
//                    roll = -cameraAngle[1];
//                }

                double cosA = Math.sin(Math.toRadians(pitch));
                double sinA = Math.cos(Math.toRadians(pitch));

                double cosB = Math.sin(Math.toRadians(roll));
                double sinB = Math.cos(Math.toRadians(roll));

                double cosC = Math.cos(Math.toRadians(yaw));
                double sinC = Math.sin(Math.toRadians(yaw));

                double[][] mA = new double[][]{
                        {cosA, -sinA, 0},
                        {sinA, cosA, 0},
                        {0, 0, 1}
                };

                double[][] mB = new double[][]{
                        {1, 0, 0},
                        {0, cosB, -sinB},
                        {0, sinB, cosB}
                };

                double[][] mC = new double[][]{
                        {cosC, -sinC, 0},
                        {sinC, cosC, 0},
                        {0, 0, 1}
                };

                double[][] m2 = multiplyMatrices(mC, mB);
                double[][] m3 = multiplyMatrices(m2, mA);

                if(aCurrentQuad.anchoredTo[0] > 0){
                    for (int k = 0; k < world.size(); k++) {
                        if(world.get(k).ID == aCurrentQuad.anchoredTo[0]){
                            for (int l = 0; l < 3; l++) {
                                aCurrentQuad.pointsMovement[j][l] = world.get(k).coordinatesInWorld[aCurrentQuad.anchoredTo[1] - 1][l];
                            }
                        }
                    }
                }

                double px = aCurrentQuad.coordinatesInWorld[j][0] - cameraPos[0] + aCurrentQuad.pointsMovement[j][0];
                double py = aCurrentQuad.coordinatesInWorld[j][1] - cameraPos[1] + aCurrentQuad.pointsMovement[j][1];
                double pz = aCurrentQuad.coordinatesInWorld[j][2] - cameraPos[2] + aCurrentQuad.pointsMovement[j][2];

                arrowCoordsTranslated[j] = new double[3];
                double a = m3[0][0];
                double b = m3[1][0];
                double c = m3[2][0];
                double d = m3[0][1];
                double e = m3[1][1];
                double f = m3[2][1];
                double g = m3[0][2];
                double h = m3[1][2];
                double ii = m3[2][2];
                arrowCoordsTranslated[j][0] = (a * px + b * pz + c * py);
                arrowCoordsTranslated[j][1] = (d * px + e * pz + f * py);
                arrowCoordsTranslated[j][2] = (g * px + h * pz + ii * py);

                aDZConstant = distanceToScreen / (arrowCoordsTranslated[j][2]);
                if(aDZConstant > 0){
                    aDZConstant *= -1;
                }
                for (int k = 0; k < 2; k++) {
                    aPointsOnScreen[i][j][k] = (arrowCoordsTranslated[j][k]) * aDZConstant;
                }
            }
            for (int j = 0; j < 3; j++) {
                arrowCentresOfShapes[i][j] = arrowCoordsTranslated[0][j] + ((arrowCoordsTranslated[2][j] - arrowCoordsTranslated[0][j]) / 2.0);
            }
            aDistancesToCentres[i] = Math.sqrt(((arrowCentresOfShapes[i][0]) * (arrowCentresOfShapes[i][0]))
                    + ((arrowCentresOfShapes[i][1]) * (arrowCentresOfShapes[i][1]))
                    + ((arrowCentresOfShapes[i][2]) * (arrowCentresOfShapes[i][2])));
            aCentreDZ = distanceToScreen / arrowCentresOfShapes[i][2];
            for (int j = 0; j < 2; j++) {
                aCentresOnScreen[i][j] = arrowCentresOfShapes[i][j] * aCentreDZ;
            }

            axisArrows[i].setPointsScreen(aPointsOnScreen[i]);
            axisArrows[i].setCoordsTranslated(arrowCoordsTranslated);
            axisArrows[i].setCentre(arrowCentresOfShapes[i]);
            axisArrows[i].setCentreScreen(aCentresOnScreen[i]);
            axisArrows[i].setDistance(aDistancesToCentres[i]);
        }
        sortWorld();
//        findIntersections(distanceToScreen);

        draw(gc, canvasWidth, canvasHeight, DZConstant, coordsTranslated, distanceToScreen, distancesToPoints);
    }

    public static void findIntersections(double distanceToScreen){
        Tri editTri;
        Tri currentTri;
        double[][][][] edgeLines = new double[triCount][3][3][2];
        double[][][] edgeVectors = new double[triCount][3][3];
        double[][][][] altEdgeLines = new double[triCount][3][3][2];
        double[][] planeEq = new double[triCount][4];
        double[][][] intersectionPoints = new double[triCount][][];

        for (int i = 0; i < triCount; i++) {
            currentTri = worldTris.get(i);
            editTri = currentTri;
            int jPlus1;
            for (int j = 0; j < 3; j++) {
                jPlus1 = j + 1;
                if(jPlus1 >= 3){
                    jPlus1 -= 3;
                }
                for (int k = 0; k < 3; k++) {
//                    edgeLines[i][j][k][0] = currentTri.coordinatesInWorld[j][k] + currentTri.pointsMovement[j][k];
//                    edgeLines[i][j][k][1] = (currentTri.coordinatesInWorld[jPlus1][k] + currentTri.pointsMovement[jPlus1][k]) - (currentTri.coordinatesInWorld[j][k] + currentTri.pointsMovement[j][k]);
                    edgeVectors[i][j][k] = (currentTri.coordsTranslated[jPlus1][k]) - (currentTri.coordsTranslated[j][k]);
                }
            }
            double a = edgeVectors[i][0][0];
            double b = edgeVectors[i][0][1];
            double c = edgeVectors[i][0][2];
            double d = edgeVectors[i][1][0];
            double e = edgeVectors[i][1][1];
            double f = edgeVectors[i][1][2];

            planeEq[i][0] = ((b * f) - (c * e));
            planeEq[i][1] = ((c * d) - (a * f));
            planeEq[i][2] = ((a * e) - (b * d));
            planeEq[i][3] = (((currentTri.coordsTranslated[0][0]) * planeEq[i][0]) + ((currentTri.coordsTranslated[0][1]) * planeEq[i][1]) + ((currentTri.coordsTranslated[0][2]) * planeEq[i][2]));

            double g = planeEq[i][0];
            double h = planeEq[i][1];
            double ii = planeEq[i][2];
            double k = planeEq[i][3];
            ArrayList<double[]> intersections = new ArrayList<>();
            ArrayList<double[]> intersectionsOnScreen = new ArrayList<>();
            boolean withinX;
            boolean withinY;
            boolean withinZ;
            Tri altTri;

            for (int m = 0; m < triCount; m++) {
                altTri = worldTris.get(m);
                for (int j = 0; j < 3; j++) {
                    jPlus1 = j + 1;
                    if(jPlus1 >= 3){
                        jPlus1 -= 3;
                    }
                    for (int n = 0; n < 3; n++) {
                        altEdgeLines[m][j][n][0] = altTri.coordsTranslated[j][n];
                        altEdgeLines[m][j][n][1] = (altTri.coordsTranslated[jPlus1][n]) - (altTri.coordsTranslated[j][n]);
                    }
                }

                for (int j = 0; j < 3; j++) {
                    jPlus1 = j + 1;
                    if(jPlus1 >= 3){
                        jPlus1 -= 3;
                    }
                    a = altEdgeLines[m][j][0][0];
                    b = altEdgeLines[m][j][0][1];
                    c = altEdgeLines[m][j][1][0];
                    d = altEdgeLines[m][j][1][1];
                    e = altEdgeLines[m][j][2][0];
                    f = altEdgeLines[m][j][2][1];

                    double t = ((g * a) + (h * c) + (ii * e) - k) / ((g * b) + (h * d) + (ii * f));
//                    if(i == 0){
//                        System.out.println(t);
//                    }
                    double[] point = {a + (b * t), c + (d * t), e + (f * t)};
                    withinX = (point[0] > (currentTri.coordsTranslated[j][0]) && point[0] < (currentTri.coordsTranslated[jPlus1][0])) || (point[0] < (currentTri.coordsTranslated[j][0]) && point[0] > (currentTri.coordsTranslated[jPlus1][0]));
                    withinY = (point[1] > (currentTri.coordsTranslated[j][1]) && point[1] < (currentTri.coordsTranslated[jPlus1][1])) || (point[1] < (currentTri.coordsTranslated[j][1]) && point[1] > (currentTri.coordsTranslated[jPlus1][1]));
                    withinZ = (point[0] > (currentTri.coordsTranslated[j][2]) && point[2] < (currentTri.coordsTranslated[jPlus1][2])) || (point[0] < (currentTri.coordsTranslated[j][2]) && point[2] > (currentTri.coordsTranslated[jPlus1][2]));

                    if(t != Double.POSITIVE_INFINITY && !Double.isNaN(t) && t != Double.NEGATIVE_INFINITY){
                        if(point[0] > -50 && point[0] < 50){
                            intersections.add(point);
                        }
                    }


                }

//                if(intersections.size() > 0){
//                    System.out.println(i);
//                }

                double intersectionDZ;
                double[] iXY;
                for (int j = 0; j < intersections.size(); j++) {
                    iXY = new double[2];
                    intersectionDZ = distanceToScreen / intersections.get(j)[2];
                    if(intersectionDZ > 0){
                        intersectionDZ *= -1;
                    }
                    for (int l = 0; l < 2; l++) {
                        iXY[0] = intersections.get(j)[0];
                        iXY[1] = intersections.get(j)[1];
                        intersectionsOnScreen.add(new double[]{(iXY[0] * intersectionDZ) + (resolutionWidth / 2.0), (iXY[1] * intersectionDZ) + (resolutionHeight / 2.0)});
                    }
                }
            }
            editTri.setIntersectionPoints(intersections.toArray(new double[intersections.size()][3]));
            editTri.setIPointsOnScreen(intersectionsOnScreen.toArray(new double[intersectionsOnScreen.size()][2]));
            worldTris.set(i,editTri);
        }
    }

    public static void sortWorld(){
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

        boolean sortedT;
        boolean[] checksT = new boolean[worldTris.size() - 1];
        Tri tempQuadT;
        int pointerT = 0;
        do{
            pointerT++;
            if(pointerT == worldTris.size()){
                pointerT = 1;
            }
            if(worldTris.get(pointerT - 1).distanceToCamera < worldTris.get(pointerT).distanceToCamera){
                tempQuadT = worldTris.get(pointerT - 1);
                worldTris.set(pointerT - 1, worldTris.get(pointerT));
                worldTris.set(pointerT, tempQuadT);
            }
            checksT[pointerT - 1] = true;
            sortedT = true;
            for (int i = 0; i < worldTris.size() - 1; i++) {
                if(!checksT[i]){
                    sortedT = false;
                    break;
                }
            }
        }while(!sortedT);
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

        if(!(draggingArrow[0] || draggingArrow[1] || draggingArrow[2])){
            cameraAngle[0] += (e.getX() - (resolutionWidth / 2.0)) / 8.0;
            double angleToAdd = -(e.getY() + 11 - (resolutionHeight / 2.0)) / 8.0;
            if(!((cameraAngle[1] >= 90 && angleToAdd > 0)
                    || (cameraAngle[1] <= -90 && angleToAdd < 0))){
                cameraAngle[1] += angleToAdd;
            }
        }else{
            if(draggingArrow[0] || draggingArrow[2]){
                mouseMovement[0] += (e.getX() - (resolutionWidth / 2.0)) / 8.0;
            }else{
                mouseMovement[1] += -(e.getY() + 11 - (resolutionHeight / 2.0)) / 8.0;
            }
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
    protected int selectedPoint;
    protected double[][] pointsMovement;
    protected double[][] oldPointsMovement;
    protected Color colour;
    protected int[] anchoredTo;
    protected boolean hidden;

    public Quad(int Id, double[][] coords, double[][] points, double[] centre, double[] centreScreen, double distance, Color col, int[] aT, boolean h){
        ID = Id;
        coordinatesInWorld = coords;
        pointsOnScreen = points;
        centreOfQuad = centre;
        centreOfQuadOnScreen = centreScreen;
        distanceToCamera = distance;
        selectedPoint = 0;
        pointsMovement = new double[4][3];
        oldPointsMovement = new double[4][3];
        colour = col;
        anchoredTo = aT;
        hidden = h;
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

    public void setColour(Color newColour){
        colour = newColour;
    }
}

class Tri{

    protected int ID;
    protected double[][] coordinatesInWorld;
    protected double[][] pointsOnScreen;
    protected double[][] coordsTranslated;
    protected double[] centreOfTri;
    protected double[] centreOfTriOnScreen;
    protected double[][] intersectionPoints;
    protected double[][] iPointsOnScreen;
    protected double distanceToCamera;
    protected int selectedPoint;
    protected double[][] pointsMovement;
    protected double[][] oldPointsMovement;
    protected Color colour;
    protected int[] anchoredTo;
    protected boolean hidden;
    protected double[] planeEq;
    protected double[][] edgeLines;

    public Tri(int Id, double[][] coords, double[][] points, double[] centre, double[] centreScreen, double distance, Color col, int[] aT, boolean h){
        ID = Id;
        coordinatesInWorld = coords;
        pointsOnScreen = points;
        centreOfTri = centre;
        centreOfTriOnScreen = centreScreen;
        distanceToCamera = distance;
        selectedPoint = 0;
        pointsMovement = new double[3][3];
        oldPointsMovement = new double[3][3];
        colour = col;
        anchoredTo = aT;
        hidden = h;
        planeEq = new double[4];
        edgeLines = new double[3][2];
    }

    public void setPointsScreen(double[][] newPoints){
        pointsOnScreen = newPoints;
    }

    public void setCoordsTranslated(double[][] newCT){
        coordsTranslated = newCT;
    }

    public void setCentre(double[] newCentre){
        centreOfTri = newCentre;
    }

    public void setCentreScreen(double[] newCentreScreen){
        centreOfTriOnScreen = newCentreScreen;
    }

    public void setIntersectionPoints(double[][] iPoints){
        intersectionPoints = iPoints;
    }

    public void setIPointsOnScreen(double[][] iPOS){
        iPointsOnScreen = iPOS;
    }

    public void setDistance(double newDistance){
        distanceToCamera = newDistance;
    }

    public void setColour(Color newColour){
        colour = newColour;
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
    protected double translateX;
    protected double translateY;
    protected double rightDistance;
    protected double opacity;
    protected boolean transitioning = false;
    protected boolean moving = false;
    protected double speedMultiplier;

    public CustomImage(Image i, double d, double rD, double op, double tx, double ty, double sM){
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
        translateX = tx;
        translateY = ty;
        imageView.setTranslateY(translateY);
        imageView.setOpacity(opacity);
        speedMultiplier = sM;
    }

    public void changeImage(Image newImage){
        image = newImage;
        imageView.setImage(newImage);
    }

    public void enterAnim(){
        if(theta2 < 90){
            transitioning = true;
            theta2 += (Main.buttonSpeed * speedMultiplier) / 5.0;
            raisedPixels2 = Math.sin(Math.toRadians(theta2)) * distance;
            imageView.setTranslateY(translateY + distance -(raisedPixels1 + raisedPixels2));
        }else if(theta2 == 90){
            transitioning = false;
        }
    }

    public void exitAnim(){
        if(theta2 > 0){
            transitioning = true;
            theta2 -= (Main.buttonSpeed * speedMultiplier) / 5.0;
            raisedPixels2 = Math.sin(Math.toRadians(theta2)) * distance;
            imageView.setTranslateY(translateY + distance -(raisedPixels1 + raisedPixels2));
        }else if(theta2 == 0){
            transitioning = false;
        }
    }

    public void hoverAnimUp(){
        if(theta1 < 180){
            theta1 += (Main.buttonSpeed * speedMultiplier);
            raisedPixels1 = ((Math.sin(Math.toRadians(theta1 - 90)) / 2) + 0.5) * 10;
            imageView.setTranslateY(translateY + distance - (raisedPixels1 + raisedPixels2));
        }
    }

    public void hoverAnimDown(){
        if(theta1 > 0){
            theta1 -= (Main.buttonSpeed * speedMultiplier);
            raisedPixels1 = ((Math.sin(Math.toRadians(theta1 - 90)) / 2) + 0.5) * 10;
            imageView.setTranslateY(translateY + distance -(raisedPixels1 + raisedPixels2));
        }
    }

    public void moveRight(){
        if(theta3 < 90){
            moving = true;
            theta3 += (Main.buttonSpeed * speedMultiplier) / 3.0;
            rightPixels = Math.sin(Math.toRadians(theta3)) * rightDistance;
            imageView.setTranslateX(rightPixels + translateX);
        }else if(theta3 == 90){
            moving = false;
        }
    }

    public void moveBackRight(){
        if(theta3 > 0){
            moving = true;
            theta3 -= (Main.buttonSpeed * speedMultiplier) / 3.0;
            rightPixels = Math.sin(Math.toRadians(theta3)) * rightDistance;
            imageView.setTranslateX(rightPixels + translateX);
        }else if(theta3 == 0){
            moving = false;
        }
    }

    public void fadeIn(){
        if(theta4 < 90){
            transitioning = true;
            theta4 += (Main.buttonSpeed * speedMultiplier) / 3.0;
            opacity = Math.sin(Math.toRadians(theta4));
            imageView.setOpacity(opacity);
        }else if (theta4 == 90){
            transitioning = false;
        }
    }

    public void fadeOut(){
        if(theta4 > 0){
            transitioning = true;
            theta4 -= (Main.buttonSpeed * speedMultiplier) / 3.0;
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