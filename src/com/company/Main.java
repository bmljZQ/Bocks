package com.company;

// A-Level Computer Science NEA Project by Mark Connolly 2021-2022.

// Import statements.
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class Main extends Application {

    // Codes to colour the output text in the console.
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    // Constant strings, most are used to identify which scene is being shown or which part of the map is being read.
    public static final String TITLE = "Bocks";
    public static final String MAIN_MENU = "Main menu";
    public static final String MAP_SELECT = "Map select";
    public static final String OPTIONS = "Options";
    public static final String GAME = "Game";
    public static final String GAME_MENU = "Game menu";
    public static final String R_WORLD = "World";
    public static final String R_OBJECTS = "Objects";
    public static final String R_LIGHTS = "Lights";

    //All global variables are placed here so they can be used in multiple methods or in the JavaFX Timeline.
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
    public double lastTime = 0;
    public double currentTime;
    public boolean anyMapTransitioning;
    public int pageNo = 1;
    public int lastPage;
    public static int maxPages;
    public static boolean[] cursorInBounds = {false, false, false};
    public static String intersectionInfo;
    public Pane pane;

    // Hard-coded data on how to draw the arrows when moving a point in the game, stored in an array.
    public static final Quad[] axisArrows = {
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

    /**
     * A method to update the progress bar, with parameters about how many elements are left to load and how many have been loaded.
     * @param loadedElements Number of already loaded elements
     * @param elementsToLoad Number of elements to load
     */
    public static void updateProgressBar(int loadedElements, int elementsToLoad){
        double progress = (double) loadedElements / (double) elementsToLoad;
        loadingBar.setScaleX(progress);
        double fullBarWidth = loadingBar.getImage().getWidth();
        loadingBar.setTranslateX(fullBarWidth * progress * 0.47 - fullBarWidth * 0.5 + 15);
    }

    /**
     * Simply updates the small text below the loading bar.
     * @param text Text to display beneath the loading bar
     */
    public static void updateLoadingContext(String text){
        loadingContext.setText(text);
    }

    /**
     * A method which uses the same idea as the GUI animations called repeatedly to create an animation for the loading bar.
     */
    public static void loadingBarAnimation(){
        loadingBarAnimTheta += 5;
        if(loadingBarAnimTheta >= 180){
            loadingBarAnimTheta = 0;
        }
        loadingBar.setTranslateX(0);
        loadingBar.setScaleX(Math.sin(Math.toRadians(loadingBarAnimTheta)));
    }

    /**
     * The main method simply sets the 'running' boolean to true, and launches the JavaFX application.
     * @param args
     */
    public static void main(String[] args) {
        running = true;
        launch(args);
    }

    /**
     * Maps details are loaded from the folder here, it takes in a few resources so it can create the ListMap custom objects,
     * then returns a dynamic array of said custom object containing the title, description, and icon of every map in the maps folder.
     * There is some support in this method for multiple pages of maps, but it is not enabled due to t being unfinished.
     * @param mapBase GUI background for an unselected map.
     * @param mapBaseSelected GUI background for a selected map.
     * @param defaultMapIcon Default map icon if none is found in the file.
     * @return mapList - List of all maps ready to be fully loaded.
     */
    public static ArrayList<ListMap> loadMaps(Image mapBase, Image mapBaseSelected, Image defaultMapIcon) {
        ArrayList<ListMap> mapList = new ArrayList<>();
        BufferedReader reader;
        String title;
        String wholeDescription;
        String[] description;
        String iconPath;
        boolean titleFound;
        boolean descriptionFound;
        boolean iconPathFound;
        Image icon = defaultMapIcon;
        try{
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("maps"));
            int j = 0;
            for (Path path : stream){
                j++;
                if (!Files.isDirectory(path)) {
                    // Variable initialisation.
                    title = "Untitled map";
                    description = new String[]{"",""};
                    iconPath = "None";
                    reader = new BufferedReader(new FileReader(path.toString()));
                    titleFound = false;
                    descriptionFound = false;
                    iconPathFound = false;
                    // A while loop to read the file until it has found the data it needs or if it reaches the end of the file.
                    while(!titleFound || !descriptionFound || !iconPathFound){
                        String readLine = reader.readLine();
                        if(readLine != null){
                            if(readLine.startsWith("<Title>")){
                                title = readLine.substring(6).trim();
                                titleFound = true;
                            }else if(readLine.startsWith("<Description>")){
                                wholeDescription = readLine.substring(13).trim();
                                if(wholeDescription.length() > 32){
                                    for (int i = 0; i < 33; i++) {
                                        if(wholeDescription.charAt(32 - i) == ' '){
                                            description = new String[]{wholeDescription.substring(0, 32 - i), wholeDescription.substring(33 - i)};
                                            if(description[1].length() > 32){
                                                description[1] = description[1].substring(0, 29) + "...";
                                            }
                                            break;
                                        }
                                    }
                                }else{
                                    description = new String[]{wholeDescription, ""};
                                }
                                descriptionFound = true;
                            }else if(readLine.startsWith("<Icon Path>")) {
                                iconPath = readLine.substring(11).trim();
                                iconPathFound = true;
                            }
                        }else{
                            // Reaching the end of the file breaks the loop here, and sets the unfound data to defaults later on.
                            break;
                        }
                    }
                    if(!(iconPath.equals("None"))){
                        icon = new Image(new FileInputStream(iconPath));
                    }else{
                        icon = defaultMapIcon;
                    }
                    // Calculations to decide which page to place the new map file on in the map select menu.
                    int page = Integer.parseInt(Double.toString((j / 9.0) + 1).split("\\.")[0]);
                    if(j % 9 == 0){
                        page--;
                    }
                    mapList.add(new ListMap(title, description[0], description[1], mapBase, mapBaseSelected, icon, path.toString(), page, (int) ((j + 1) % 3.0) + 1, (int) Double.parseDouble(Double.toString(((j + 1) / 3.0)).split("\\.")[0])));
                    if(j == 10){
                        break;
                    }
                }
            }
        }catch(IOException e){
            reportError("Error loading all maps.", e);
        }
        // Calculations to find out how many pages are needed to contain all the maps.
        double maxPagesDouble = mapList.size() / 9.0;
        if(Math.round(maxPagesDouble) == maxPagesDouble){
            maxPages = (int) maxPagesDouble;
        }else{
            maxPages = (int) Math.round(maxPagesDouble) + 1;
        }
        return mapList;
    }

    /**
     * The method that fully loads a given map. It takes a single map file and stores the loaded details in the maps dynamic array.
     * @param mapFile The map file to be loaded
     * @throws InterruptedException The loading bar rarely encounters an exception with the loading bar text, which proves to be practically unfixable
     */
    private static void loadMap(File mapFile) throws InterruptedException {
        // Variable initialisation.
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
                // Reading the next line in the file and identifying which part is being read.
                readLine = reader.readLine().trim();
                loadingBarAnimation();
                if(readLine.contains("<World>") || readLine.contains("<world>")){
                    currentlyReading = R_WORLD;
                }else if(readLine.contains("<Objects>") || readLine.contains("<objects>")){
                    currentlyReading = R_OBJECTS;
                }else if(readLine.contains("<Lights>") || readLine.contains("<lights>")){
                    currentlyReading = R_LIGHTS;
                }
                // If the line contains a closing bracket, increment the bracket counter.
                if(readLine.contains("}")){
                    currentlyReading = "";
                    bracketNo++;
                }
                // If the bracket counter reaches three, terminate the loop after this iteration as the end of the file has been reached.
                if(bracketNo == 3){
                    endLoop = true;
                }
                // Create a delay if the fake loading time boolean is true, used to test the loading bar visuals at an observable speed.
                if(fakeLoadingTime){
                    Thread.sleep(33);
                }
                loadingBarAnimation();
                // If the read line contains square brackets, it is world data and therefore is added to the respective dynamic array of strings.
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

        // Variable initialisation for organising the read data.
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
            /*
             * Algorithm to convert the strings to doubles and store them in the correct places inside the Quad and Tri custom objects.
             * This idea is the same for the other two types of map data (objects and lights).
             */
            tempColourString = worldStrings.get(i).split("¦")[1].split(",");
            tempCoordinates[i] = worldStrings.get(i).split("¦")[0].split("/");
            for (int j = 0; j < 4; j++) {
                coordinatesString[i][j] = tempCoordinates[i][j].split(",");
                for (int k = 0; k < 3; k++) {
                    coordinates[i][j][k] = Double.parseDouble(coordinatesString[i][j][k]);
                }
            }
            worldTris.add(new Tri((2 * i) + 1, new double[][]{coordinates[i][0], coordinates[i][1], coordinates[i][2]}, null, null, null, 0, Color.color(Double.parseDouble(tempColourString[0]) / 255, Double.parseDouble(tempColourString[1]) / 255, Double.parseDouble(tempColourString[2]) / 255), new int[]{0,0}, false));
            worldTris.add(new Tri((2 * i) + 2, new double[][]{coordinates[i][0], coordinates[i][2], coordinates[i][3]}, null, null, null, 0, Color.color(Double.parseDouble(tempColourString[0]) / 255, Double.parseDouble(tempColourString[1]) / 255, Double.parseDouble(tempColourString[2]) / 255), new int[]{0,0}, false));
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
        updateLoadingContext("Indexing lights.");
        for (int i = 0; i < lightCount; i++) {
            lightData[i] = lightStrings.get(i).split("/");
            posString[i] = lightData[i][0].split(",");
            for (int j = 0; j < 3; j++) {
                pos[i][j] = Double.parseDouble(posString[i][j]);
            }
            brightnesses[i] = Double.parseDouble(lightData[i][1]);

            lights.add(new Light(i, pos[i], brightnesses[i]));
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

    /**
     * Simply wipes all map data when exiting a map from the variables.
     */
    public static void unloadMap(){
        mapLoaded = false;
        world.clear();
        worldTris.clear();
        objects.clear();
        lights.clear();
        cameraPos = new double[]{0,0,0};
        cameraAngle = new double[]{0,0};
    }

    /**
     * This method simply takes the string it was given and prints it to the console in a visually appealing format.
     * Important for reporting errors and is used in all try-catch blocks.
     * @param error The string specifying which algorithm encountered the error
     * @param e The error object to print the stack trace for additional information
     */
    public static void reportError(String error, Exception e){
        System.out.println(ANSI_RED + error + ANSI_RESET);
        e.printStackTrace();
    }

    /**
     * This method is called whenever a key is pressed. It checks if the key is the screenshotting key (F7) and if so, converts
     * the current scene to a BufferedImage, which is then saved as a png and a colour coded message is printed in console.
     * @param scene The scene to convert to an image
     * @param e The key pressed when the method is called
     */
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

    /**
     * Simply selects the map specified by a parameter and deselects the previously selected map, if any.
     * @param map The map to be selected
     */
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

    /**
     * The Javafx applications start method, containing almost every single GUI element, as well as the Timeline
     * to run the simulation.
     * @param primaryStage The JavaFX application stage
     * @throws Exception The exception which must be included as it is part of the JavaFX application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Initialisation of the game canvas and graphics context.
        Canvas canvas = new Canvas(resolutionWidth, resolutionHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
//        System.out.println(javafx.scene.text.Font.getFamilies());
        VBox menuBox = new VBox();
        GridPane mapSelectBox = new GridPane();
        menuBox.setAlignment(Pos.TOP_CENTER);
        mapSelectBox.setAlignment(Pos.CENTER_LEFT);
        primaryStage.setResizable(false);

        // Loading of all GUI element images.
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
        Image leftButton = new Image(new FileInputStream("resources/Left_Button.png"));
        Image rightButton = new Image(new FileInputStream("resources/Right_Button.png"));

        // Initialisation of loading bar elements, title, and background.
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

        // Creation of all image based GUI elements using a custom object 'CustomImage'.
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
        CustomImage leftScrollButton = new CustomImage(leftButton, resolutionHeight + 200, 0, 1, 550, 0, 1);
        CustomImage rightScrollButton = new CustomImage(rightButton, resolutionHeight + 200, 0, 1, 550, 0, 1);
        buttonList = new CustomImage[]{mainMenuButton, mapSelectButton, optionsButton, exitButton, startButton, mainMenuGameButton, leftScrollButton, rightScrollButton};

        // Loading map details.
        maps = loadMaps(mapBase, mapBaseSelected, defaultMapIcon);

        // Designing of the main menu.
        menuBox.setPadding(new Insets(30,0,10,0));
        menuBox.setSpacing(70);
        menuBox.getChildren().add(title.text);
        menuBox.getChildren().add(mapSelectButton.imageView);
//        menuBox.getChildren().add(optionsButton.imageView);
        menuBox.getChildren().add(exitButton.imageView);
        menuBox.setBackground(boxesBackground);

        // Binding of controls to the scenes.
        Scene menuScene = new Scene(menuBox, resolutionWidth, resolutionHeight);
        pane = new Pane();
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

        // Designing of the map selection scene.
        mapSelectBox.setPadding(new Insets(10));
        mapSelectBox.setHgap(10);
        mapSelectBox.setVgap(20);
        mapSelectBox.add(mapSelectBackground.imageView, 2, 0);
        mapSelectBackground.imageView.setTranslateX(-603);
        mapSelectBox.add(leftMapsBracket.imageView,0,0);
        mapSelectBox.add(rightMapsBracket.imageView, 1, 0);
        mapSelectBox.add(mainMenuButton.imageView, 2, 0);
//        mapSelectBox.add(leftScrollButton.imageView, 1, 1);
        leftScrollButton.imageView.setTranslateX(700);
//        mapSelectBox.add(rightScrollButton.imageView, 1, 1);
        rightScrollButton.imageView.setTranslateX(800);
        mapSelectBox.add(startButton.imageView, 2, 0);
        mapSelectBox.setOnScroll(e -> {
            if(e.getDeltaY() < 0){
                if(pageNo > 1){
                    lastPage = pageNo;
                    pageNo--;
                }
            }else if(e.getDeltaY() > 0){
                if(pageNo < maxPages){
                    lastPage = pageNo;
                    pageNo++;
                }
            }
        });
        // Placement of the maps in the map select menu.
        for (int i = 0; i < maps.size(); i++) {
            mapSelectBox.add(maps.get(i).map, 1, 0);
            maps.get(i).xPos = ((maps.get(i).rowCol[1] - 1) * 400) - 65;
            maps.get(i).yPos = ((maps.get(i).rowCol[0] - 1) * 120) - 120;
            maps.get(i).updatePos();
        }

        mapSelectBox.add(loadingText, 1,0);
        mapSelectBox.add(loadingContext, 1, 0);
        mapSelectBox.add(emptyLoadingBar, 1, 0);
        mapSelectBox.add(loadingBar, 1, 0);
        mapSelectBox.setBackground(boxesBackground);

        // Initialisation of the in-game menu.
        pane.getChildren().add(gameMenuBackground.imageView);
        pane.getChildren().add(mainMenuGameButton.imageView);
        pane.getChildren().add(leftMenuBracket.imageView);
        pane.getChildren().add(rightMenuBracket.imageView);

        // Binding of functionality to GUI buttons and initialisation of debugging information.
        debugText = new Text(0, 15, "No information available yet");
        debugText.setFont(javafx.scene.text.Font.font(Font.SANS_SERIF));
        pane.getChildren().add(debugText);
        pane.setStyle("-fx-background-color: #000000;");
        Scene mapSelectScene = new Scene(mapSelectBox, resolutionWidth, resolutionHeight);
        mapSelectButton.imageView.setOnMouseClicked(e -> focusedWindow = MAP_SELECT);
        optionsButton.imageView.setOnMouseClicked(e -> focusedWindow = OPTIONS);
        mainMenuButton.imageView.setOnMouseClicked(e -> focusedWindow = MAIN_MENU);
        leftScrollButton.imageView.setOnMouseClicked(e -> {
            if(pageNo > 1){
                lastPage = pageNo;
                pageNo--;
            }
//            System.out.println(pageNo + ", " + lastPage);
        });
        rightScrollButton.imageView.setOnMouseClicked(e -> {
            if(pageNo < maxPages){
                lastPage = pageNo;
                pageNo++;
            }
//            System.out.println(pageNo + ", " + lastPage);
        });
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
        for (int i = 0; i < maps.size(); i++) {
            ListMap m = maps.get(i);
            m.map.setOnMouseClicked(e -> {
                if(m.opacity > 0){
                    selectMap(m);
                }
            });
        }

        Alert alert = new Alert(Alert.AlertType.NONE, "REALLY exit?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit");
        exitButton.imageView.setOnMouseClicked(e -> {
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                exit();
            }
        });

        // Placement of many GUI elements.
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
        lastPage = pageNo;

        /*
         * The Timeline is the most important part of the program, responsible for controlling every GUI feature and
         * calling the gameplay related methods.
         */
        Timeline renderTimeline = new Timeline(

                new KeyFrame(
                        // The keyframe calling the gameplay related elements lasts zero seconds as to call said elements as quickly as possible.
                        Duration.ZERO,
                        event -> {
                            lastTime = System.nanoTime();
                            if(!paused && mapLoaded){
                                checkKeys();
                                cycleMovement();
                                render(gc, canvas.getWidth(), canvas.getHeight());
                            }
                            currentTime = System.nanoTime();
                            FPSLong = 1.0 / ((currentTime - lastTime) / 1000000000.0);
                            FPS = (Math.round(FPSLong * 10)) / 10.0;
                }),
                new KeyFrame(
                        // The keyframe of every GUI element lasts a certain interval of time so the target number of frames per second can be reached.
                        Duration.seconds(1.0 / targetFPS),
                        event -> {
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
                                leftScrollButton.enterAnim();
                                rightScrollButton.enterAnim();
                                startButton.enterAnim();
                                if(pageNo == 1){
                                    leftScrollButton.imageView.setOpacity(0.3);
                                    leftScrollButton.enabled = false;
                                }else{
                                    leftScrollButton.imageView.setOpacity(1);
                                    leftScrollButton.enabled = true;
                                }
                                if(pageNo == maxPages){
                                    rightScrollButton.imageView.setOpacity(0.3);
                                    rightScrollButton.enabled = false;
                                }else{
                                    rightScrollButton.imageView.setOpacity(1);
                                    rightScrollButton.enabled = true;
                                }
                                if(!rightMapsBracket.transitioning){
                                    rightMapsBracket.moveRight();
                                    if(!rightMapsBracket.moving){
                                        for (int i = 0; i < maps.size(); i++) {
                                            maps.get(i).enterAnim();
                                        }
                                        mapSelectBackground.fadeIn();
                                    }
                                }
                                for (int i = 0; i < maps.size(); i++) {
                                    if(maps.get(i).page == pageNo){
                                        if(lastPage > pageNo){
                                            maps.get(i).pageInFromLeft();
                                        }else if(lastPage < pageNo){
                                            maps.get(i).pageInFromRight();
                                        }
                                    }else{
                                        if(lastPage > pageNo){
                                            maps.get(i).pageOutToRight();
                                        }else if(lastPage < pageNo){
                                            maps.get(i).pageOutToLeft();
                                        }
                                    }
                                }
                            }else{
                                for (int i = 0; i < maps.size(); i++) {
                                    if(maps.get(i).opacity != 0){
                                        maps.get(i).exitAnim();
                                    }else{
                                        maps.get(i).transitioning = false;
                                    }
                                }
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
                                anyMapTransitioning = false;
                                for (int i = 0; i < maps.size(); i++) {
                                    if(maps.get(i).transitioning){
                                        anyMapTransitioning = true;
                                        break;
                                    }
                                }
                                if(!anyMapTransitioning){
                                    rightMapsBracket.moveBackRight();
                                    if(!rightMapsBracket.moving){
                                        leftMapsBracket.exitAnim();
                                        rightMapsBracket.exitAnim();
                                        mainMenuButton.exitAnim();
                                        leftScrollButton.exitAnim();
                                        rightScrollButton.exitAnim();
                                        startButton.exitAnim();
                                    }
                                }
                            }
                            if(focusedWindow.equals(MAIN_MENU) && !(rightMapsBracket.transitioning || rightMapsBracket.moving || anyMapTransitioning)){
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
                            if(focusedWindow.equals(GAME) && !(rightMapsBracket.transitioning || rightMapsBracket.moving || anyMapTransitioning) && mapLoaded){
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
                                    if(!(!buttonList[i].enabled && (buttonList[i] == leftScrollButton || buttonList[i] == rightScrollButton))){
                                        if(buttonList[i].imageView.isHover()){
                                            buttonList[i].hoverAnimUp();
                                        }else{
                                            buttonList[i].hoverAnimDown();
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < maps.size(); i++) {
                                if (maps.get(i).map.isHover()) {
                                    maps.get(i).hoverAnimUp();
                                } else {
                                    maps.get(i).hoverAnimDown();
                                }
                                maps.get(i).select();
                            }

                            if(paused){
                                debugText.setText("|PAUSED|");
                                debugText.setFill(Color.SILVER);
                            }else{
                                debugText.setFill(Color.BLACK);
                            }
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

    /**
     * Simply toggles the debugging mode.
     */
    public void debugMode(){
        debugMode = !debugMode;
    }

    /**
     * Effectively the opposite of the main method, this sets the running boolean to false and ends the program.
     */
    public static void exit(){
        running = false;
        System.exit(0);
    }

    /**
     * A method which simply locates a Quad in the world array by ID and by using linear search.
     * @param ID The ID of the Quad to find.
     * @return currentQuad - The Quad found in the world array, returns null if no such Quad exists.
     */
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

    /**
     * This method is responsible for drawing every frame.
     * @param gc Graphics Context used to draw
     * @param canvasWidth The width of the canvas
     * @param canvasHeight The height of the canvas
     * @param DZ The variable used to project 3D points to a 2D screen
     * @param coordsT The coordinates of the geometry after being rotated and moved
     * @param distanceToScreen The theoretical pixel distance between the camera and the game window
     * @param distancesToPoints The distances to every point in the world geometry
     */

    private static void draw(GraphicsContext gc, double canvasWidth, double canvasHeight, double DZ, double[][][] coordsT, double distanceToScreen, double[][] distancesToPoints) {
        /* Experimental methods involving the next steps in a possible version 1.1.
        double[][][] inequalities = new double[triCount][3][2];
        int jPlus1;
        int nPlus1;

        Tri altTri;
        for (int i = 0; i < triCount; i++) {
            currentTri = worldTris.get(i);
            for (int j = 0; j < 3; j++) {
                jPlus1 = j + 1;
                if(jPlus1 > 2){
                    jPlus1 -= 3;
                }
                inequalities[i][j][0] = ((currentTri.pointsOnScreen[jPlus1][1] + (resolutionHeight / 2.0)) - (currentTri.pointsOnScreen[j][1] + (resolutionHeight / 2.0))) / ((currentTri.pointsOnScreen[jPlus1][0] + (resolutionWidth / 2.0)) - (currentTri.pointsOnScreen[j][0] + (resolutionWidth / 2.0)));
                inequalities[i][j][1] = ((currentTri.pointsOnScreen[j][1] + (resolutionHeight / 2.0)) - (inequalities[i][j][0] * (currentTri.pointsOnScreen[j][0] + (resolutionWidth / 2.0))));
            }
        }

        boolean inFrontOfCameraAlt;
        for (int i = 0; i < triCount; i++) {
            ArrayList<double[]> overlaps = new ArrayList<>();
            currentTri = worldTris.get(i);
            inFrontOfCamera = currentTri.centreOfTri[2] > 0;
            for (int j = 0; j < triCount; j++) {
                inFrontOfCameraAlt = worldTris.get(j).centreOfTri[2] > 0;
                for (int k = 0; k < 3; k++) {
                    int lPlus1;
                    for (int l = 0; l < 3; l++) {
                        lPlus1 = l + 1;
                        if(lPlus1 >= 3){
                            lPlus1 -= 3;
                        }
                        if(inFrontOfCamera && inFrontOfCameraAlt){
                            if(round(inequalities[j][l][0], 1) != round(inequalities[i][k][0], 1)){
                                double pointX = ((inequalities[i][k][1] - inequalities[j][l][1]) / (inequalities[j][l][0] - inequalities[i][k][0]));
                                double[] point = {pointX, ((inequalities[i][k][0] * pointX) + inequalities[i][k][1]), j, k, l};

                                boolean on1 = round(point[1], 1) == round((inequalities[i][k][0] * point[0]) + inequalities[i][k][1], 1);
                                boolean on2 = round(point[1], 1) == round((inequalities[j][l][0] * point[0]) + inequalities[j][l][1], 1);
                                int withinDP = 0;
                                boolean withinX = (round(point[0], withinDP) > round(currentTri.pointsOnScreen[l][0] + (resolutionHeight / 2.0), withinDP) && round(point[0], withinDP) < round(currentTri.pointsOnScreen[lPlus1][0] + (resolutionHeight / 2.0), withinDP)) || (round(point[0], withinDP) < round(currentTri.pointsOnScreen[l][0] + (resolutionHeight / 2.0), withinDP) && round(point[0], withinDP) > round(currentTri.pointsOnScreen[lPlus1][0] + (resolutionHeight / 2.0), withinDP));
                                boolean withinY = (round(point[1], withinDP) > round(currentTri.pointsOnScreen[l][1] + (resolutionHeight / 2.0), withinDP) && round(point[1], withinDP) < round(currentTri.pointsOnScreen[lPlus1][1] + (resolutionHeight / 2.0), withinDP)) || (round(point[1], withinDP) < round(currentTri.pointsOnScreen[l][1] + (resolutionHeight / 2.0), withinDP) && round(point[1], withinDP) > round(currentTri.pointsOnScreen[lPlus1][1] + (resolutionHeight / 2.0), withinDP));
                                if(withinX && withinY && on1 && on2){
                                    overlaps.add(point);
                                }
                            }
                        }
                    }
                }
            }

            worldTris.get(i).setOverlapPoints(overlaps.toArray(new double[overlaps.size()][5]));
        }
         */

        Tri currentTri;
        boolean inFrontOfCamera;
        double[][] pointsToUse = new double[2][3];
        double[][] aPointsToUse = new double[2][4];
        double colourBrightness;
        double[] colourRGB = new double[3];
        double[] totalBrightnesses = new double[triCount];
        Light currentLight;
        Color variableColour;
        gc.clearRect(0,0,canvasWidth,canvasHeight);
        gc.setFill(Color.GRAY);
        gc.fillRect(0,0,canvasWidth,canvasHeight);

        // Drawing of the lines which pinpoint the points in the world geometry in debug mode.
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
        /*
         * The large 'for' loop which repeats for every world geometry element, calculates lighting,
         * and draws every triangle in the world with the correct shape, colour, and circumstances.
         * Debugging information is also compiled here and displayed if debug mode is on.
         */

        for (int i = 0; i < triCount; i++) {
            inFrontOfCamera = false;
            currentTri = worldTris.get(i);

            for (int j = 0; j < lightCount; j++) {
                currentLight = lights.get(j);
                double distance = Math.sqrt(((currentTri.coordinatesInWorld[0][0] - currentLight.pos[0]) * (currentTri.coordinatesInWorld[0][0] - currentLight.pos[0]))
                        + ((currentTri.coordinatesInWorld[0][1] - currentLight.pos[1]) * (currentTri.coordinatesInWorld[0][1] - currentLight.pos[1]))
                        + ((currentTri.coordinatesInWorld[0][2] - currentLight.pos[2]) * (currentTri.coordinatesInWorld[0][2] - currentLight.pos[2])));
                totalBrightnesses[i] += currentLight.brightness / distance;
            }

            if(!currentTri.hidden){
                if(currentTri.centreOfTri[2] > 0){
                    inFrontOfCamera = true;
                }

//                colourBrightness = 5 / currentTri.distanceToCamera;
                if(totalBrightnesses[i] > 1){
                    totalBrightnesses[i] = 1;
                }
                colourRGB[0] = currentTri.colour.getRed() * totalBrightnesses[i];
                colourRGB[1] = currentTri.colour.getGreen() * totalBrightnesses[i];
                colourRGB[2] = currentTri.colour.getBlue() * totalBrightnesses[i];
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
                try{
                    debugInfo = "F3 - Debug mode. V" +
                            "\n FPS: " + FPS +
                            "\n (" + Math.round(currentTri.pointsOnScreen[0][0]) + ", " + Math.round(currentTri.pointsOnScreen[0][1]) + ") " +
                            "\n (" + Math.round(currentTri.pointsOnScreen[1][0]) + ", " + Math.round(currentTri.pointsOnScreen[1][1]) + ") " +
                            "\n (" + Math.round(currentTri.pointsOnScreen[2][0]) + ", " + Math.round(currentTri.pointsOnScreen[2][1]) + ") " +
                            "\n DZ: " + DZ +
                            "\n (" + Math.round(coordsT[i][0][0]) + ", " + Math.round(coordsT[i][0][1]) + ", " + Math.round(coordsT[i][0][2]) + ") " +
                            "\n (" + Math.round(coordsT[i][1][0]) + ", " + Math.round(coordsT[i][1][1]) + ", " + Math.round(coordsT[i][0][2]) + ") " +
                            "\n (" + Math.round(coordsT[i][2][0]) + ", " + Math.round(coordsT[i][2][1]) + ", " + Math.round(coordsT[i][0][2]) + ") " +
                            "\n (" +  cameraAngle[0] + ", " + cameraAngle[1] + ") " +
                            "\n {" + draggingArrow[0] + ", " + draggingArrow[1] + ", " + draggingArrow[2] + "}" +
                            "\n Intersection Points: " + currentTri.iPointsOnScreen.length;
                }catch(NullPointerException e){
                    debugInfo = "F3 - Debug mode. V" +
                            "\n FPS: " + FPS +
                            "\n (" + Math.round(currentTri.pointsOnScreen[0][0]) + ", " + Math.round(currentTri.pointsOnScreen[0][1]) + ") " +
                            "\n (" + Math.round(currentTri.pointsOnScreen[1][0]) + ", " + Math.round(currentTri.pointsOnScreen[1][1]) + ") " +
                            "\n (" + Math.round(currentTri.pointsOnScreen[2][0]) + ", " + Math.round(currentTri.pointsOnScreen[2][1]) + ") " +
                            "\n DZ: " + DZ +
                            "\n (" + Math.round(coordsT[i][0][0]) + ", " + Math.round(coordsT[i][0][1]) + ", " + Math.round(coordsT[i][0][2]) + ") " +
                            "\n (" + Math.round(coordsT[i][1][0]) + ", " + Math.round(coordsT[i][1][1]) + ", " + Math.round(coordsT[i][0][2]) + ") " +
                            "\n (" + Math.round(coordsT[i][2][0]) + ", " + Math.round(coordsT[i][2][1]) + ", " + Math.round(coordsT[i][0][2]) + ") " +
                            "\n (" +  cameraAngle[0] + ", " + cameraAngle[1] + ") " +
                            "\n {" + draggingArrow[0] + ", " + draggingArrow[1] + ", " + draggingArrow[2] + "}";
                }

                debugString = debugInfo;
                if(debugMode){
                    debugString = debugInfo + "\n \n" + arrowInfo + "\n \n" + intersectionInfo;
                    debugText.setText(debugString);
                }else{
                    debugText.setText("F3 - Debug mode. >");
                }

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

        if(debugMode){
            gc.setFill(Color.FUCHSIA);
            for (int i = 0; i < worldTris.size(); i++) {
                currentTri = worldTris.get(i);
                if(currentTri.iPointsOnScreen != null){
                    for (int j = 0; j < currentTri.iPointsOnScreen.length; j++) {
                        gc.fillOval(currentTri.iPointsOnScreen[j][0] - 5, currentTri.iPointsOnScreen[j][1] - 5, 10, 10);
                    }
                }
            }
        }

//        gc.setFill(Color.ORANGE);
//        for (int i = 0; i < worldTris.size(); i++) {
//            currentTri = worldTris.get(i);
//            for (int j = 0; j < currentTri.overlapPoints.length; j++) {
//                gc.fillOval(currentTri.overlapPoints[j][0] - 5, currentTri.overlapPoints[j][1] - 5, 10, 10);
//            }
//        }

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
                    }
                }
                if(!leftMouseHeld){
                    draggingArrow[(int) Math.round((i + 1) / 2.0) - 1] = false;
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
    }

    /**
     * Most of the mathematical calculations are contained here to project and translate world geometry.
     * @param gc Graphics Context to be passed onto the draw method
     * @param canvasWidth The width of the canvas
     * @param canvasHeight The height of the canvas
     */

    private static void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        double[][][] pointsOnScreen = new double[triCount][3][2];
        double[][] centresOfShapes = new double[triCount][3];
        double[][] centresOnScreen = new double[triCount][2];
        double[] distancesToCentres = new double[triCount];

        double DZConstant = 0;
        double centreDZ;
        Quad currentQuad;
        Tri currentTri;

        double[][] distancesToPoints = new double[triCount][3];
        double[][] distancesToArrowPoints = new double[axisArrows.length][4];
        double distanceToScreen = (resolutionWidth / 2.0) / Math.tan(Math.toRadians(fieldOfView / 2.0));
        double[][][] coordsTranslated = new double[triCount][3][];
        double[][] arrowCoordsTranslated = new double[4][];
        boolean pointSelected = false;

        if(cameraAngle[0] >= 360){
            cameraAngle[0] -= 360;
        }else if(cameraAngle[0] < 0){
            cameraAngle[0] += 360;
        }

        double[][] eulerMatrix = eulerAngleCalc();

        for (int i = 0; i < triCount; i++) {
            currentTri = worldTris.get(i);
            // Rotation and managing the anchoring feature.
            for (int j = 0; j < 3; j++) {
                distancesToPoints[i][j] = Math.sqrt(((currentTri.coordinatesInWorld[j][0] - cameraPos[0]) * (currentTri.coordinatesInWorld[j][0] - cameraPos[0])) + ((currentTri.coordinatesInWorld[j][1] - cameraPos[1]) * (currentTri.coordinatesInWorld[j][1] - cameraPos[1])) + ((currentTri.coordinatesInWorld[j][2] - cameraPos[2]) * (currentTri.coordinatesInWorld[j][2] - cameraPos[2])));

                if(currentTri.anchoredTo[0] > 0){
                    for (Tri tris : worldTris) {
                        if (tris.ID == currentTri.anchoredTo[0]) {
                            System.arraycopy(tris.coordinatesInWorld[currentTri.anchoredTo[1] - 1], 0, currentTri.pointsMovement[j], 0, 3);
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

                if(currentTri.selectedPoint == j + 1){
                    pointSelected = true;
                    for (int k = 0; k < 6; k++) {
                        axisArrows[k].anchoredTo = new int[]{currentTri.ID, j + 1};
                        axisArrows[k].hidden = false;
                    }
                }

                coordsTranslated[i][j] = rotatePoint(currentTri.coordinatesInWorld[j], currentTri.pointsMovement[j], eulerMatrix);

                DZConstant = distanceToScreen / (coordsTranslated[i][j][2]);
                if(DZConstant > 0){
                    DZConstant *= -1;
                }
                for (int k = 0; k < 2; k++) {
                    pointsOnScreen[i][j][k] = (coordsTranslated[i][j][k]) * DZConstant;
                }

            }
            // Projection of points to game window.
            for (int j = 0; j < 3; j++) {
                centresOfShapes[i][j] = coordsTranslated[i][0][j] + ((coordsTranslated[i][2][j] - coordsTranslated[i][0][j]) / 2.0);
            }
            distancesToCentres[i] = Math.sqrt(((centresOfShapes[i][0]) * (centresOfShapes[i][0]))
                    + ((centresOfShapes[i][1]) * (centresOfShapes[i][1]))
                    + ((centresOfShapes[i][2]) * (centresOfShapes[i][2])));
            centreDZ = distanceToScreen / centresOfShapes[i][2];
            for (int j = 0; j < 2; j++) {
                centresOnScreen[i][j] = centresOfShapes[i][j] * centreDZ;
            }

            worldTris.get(i).setPointsScreen(pointsOnScreen[i]);
            worldTris.get(i).setCoordsTranslated(coordsTranslated[i]);
            worldTris.get(i).setCentre(centresOfShapes[i]);
            worldTris.get(i).setCentreScreen(centresOnScreen[i]);
            worldTris.get(i).setDistance(distancesToCentres[i]);
        }

        if(!pointSelected){
            for (int k = 0; k < 6; k++) {
                axisArrows[k].anchoredTo = new int[]{0, 0};
                axisArrows[k].hidden = true;
            }
        }
        double[][][] aPointsOnScreen = new double[6][4][2];
        double[][] aCentresOnScreen = new double[6][2];
        double[] aDistancesToCentres = new double[6];
        double[][] arrowCentresOfShapes = new double[axisArrows.length][3];
        double aDZConstant;
        double aCentreDZ;
        Quad aCurrentQuad;
        // Similar code as above, only supporting the Quad object instead of the Tri object to draw the editing arrows.
        for (int i = 0; i < axisArrows.length; i++) {
            aCurrentQuad = axisArrows[i];
            for (int j = 0; j < 4; j++) {
                distancesToArrowPoints[i][j] = Math.sqrt(((aCurrentQuad.coordinatesInWorld[j][0] - cameraPos[0]) * (aCurrentQuad.coordinatesInWorld[j][0] - cameraPos[0])) + ((aCurrentQuad.coordinatesInWorld[j][1] - cameraPos[1]) * (aCurrentQuad.coordinatesInWorld[j][1] - cameraPos[1])) + ((aCurrentQuad.coordinatesInWorld[j][2] - cameraPos[2]) * (aCurrentQuad.coordinatesInWorld[j][2] - cameraPos[2])));

                if(aCurrentQuad.anchoredTo[0] > 0){
                    for (int k = 0; k < world.size(); k++) {
                        if(world.get(k).ID == aCurrentQuad.anchoredTo[0]){
                            for (int l = 0; l < 3; l++) {
                                aCurrentQuad.pointsMovement[j][l] = world.get(k).coordinatesInWorld[aCurrentQuad.anchoredTo[1] - 1][l];
                            }
                        }
                    }
                }

                arrowCoordsTranslated[j] = rotatePoint(aCurrentQuad.coordinatesInWorld[j], aCurrentQuad.pointsMovement[j], eulerMatrix);

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
        findIntersections(distanceToScreen);

        draw(gc, canvasWidth, canvasHeight, DZConstant, coordsTranslated, distanceToScreen, distancesToPoints);
    }

    /**
     * An experimental method which finds where world geometry would intersect.
     * These intersection points are viewable in debug mode.
     * @param distanceToScreen The theoretical distance in pixels between the camera and the game window
     */

    public static void findIntersections(double distanceToScreen){
        double[][] distancesBetweenPAndC = new double[triCount][3];
        Tri currentTri;
        for (int i = 0; i < triCount; i++) {
            currentTri = worldTris.get(i);
            for (int j = 0; j < 3; j++) {
                distancesBetweenPAndC[i][j] = Math.sqrt(((currentTri.centreOfTri[0] - currentTri.coordinatesInWorld[j][0]) * (currentTri.centreOfTri[0] - currentTri.coordinatesInWorld[j][0]))
                        + ((currentTri.centreOfTri[1] - currentTri.coordinatesInWorld[j][1]) * (currentTri.centreOfTri[1] - currentTri.coordinatesInWorld[j][1]))
                        + ((currentTri.centreOfTri[0] - currentTri.coordinatesInWorld[j][0]) * (currentTri.centreOfTri[0] - currentTri.coordinatesInWorld[j][0])));
            }
        }

        double[][][][] edgeLines = new double[triCount][3][3][2];
        double[][][] cameraToCentreLines = new double[triCount][3][2];
        double[][][] edgeVectors = new double[triCount][3][3];
        double[][][][] altEdgeLines = new double[triCount][3][3][2];
        double[][] planeEq = new double[triCount][4];
        double[][][] intersectionPoints = new double[triCount][][];
        double[][][] inequalities = new double[triCount][3][2];
        boolean[][] inequalityGT = new boolean[triCount][3];
        double[][] eulerMatrix = eulerAngleCalc();

//        for (int i = 0; i < triCount; i++) {
//            currentTri = worldTris.get(i);
//            for (int j = 0; j < 3; j++) {
//                double[] centreTranslated = rotatePoint(new double[][]{currentTri.centreOfTri}, new double[1][3], 0, eulerMatrix);
//                cameraToCentreLines[i][j][0] = centreTranslated[j];
//                cameraToCentreLines[i][j][1] = (cameraPos[j]) - (centreTranslated[j]);
//            }
//        }

        for (int i = 0; i < triCount; i++) {
            currentTri = worldTris.get(i);
//            if(distancesBetweenPAndC[i][0] < 3 && distancesBetweenPAndC[i][1] < 3 && distancesBetweenPAndC[i][2] < 3){
                double[][] planeCoordsToIntersect;
                double[][] lineCoordsToIntersect;
                int jPlus1;
                int jPlus2;
                planeCoordsToIntersect = currentTri.coordsTranslated;
                for (int j = 0; j < 3; j++) {
                    jPlus1 = j + 1;
                    if(jPlus1 >= 3){
                        jPlus1 -= 3;
                    }
                    for (int k = 0; k < 3; k++) {
                        edgeVectors[i][j][k] = (planeCoordsToIntersect[jPlus1][k]) - (planeCoordsToIntersect[j][k]);
                    }
                }
                for (int j = 0; j < 3; j++) {
                    jPlus1 = j + 1;
                    jPlus2 = j + 2;
                    if(jPlus1 > 2){
                        jPlus1 -= 3;
                    }
                    if(jPlus2 > 2){
                        jPlus2 -= 3;
                    }
                    inequalities[i][j][0] = ((currentTri.pointsOnScreen[jPlus1][1] + (resolutionHeight / 2.0)) - (currentTri.pointsOnScreen[j][1] + (resolutionHeight / 2.0))) / ((currentTri.pointsOnScreen[jPlus1][0] + (resolutionWidth / 2.0)) - (currentTri.pointsOnScreen[j][0] + (resolutionWidth / 2.0)));
                    inequalities[i][j][1] = ((currentTri.pointsOnScreen[j][1] + (resolutionHeight / 2.0)) - (inequalities[i][j][0] * (currentTri.pointsOnScreen[j][0] + (resolutionWidth / 2.0))));
                    inequalityGT[i][j] = (currentTri.pointsOnScreen[jPlus2][1] + (resolutionHeight / 2.0)) > ((inequalities[i][j][0] * (currentTri.pointsOnScreen[jPlus2][0] + (resolutionWidth / 2.0))) + inequalities[i][j][1]);
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
                planeEq[i][3] = (((planeCoordsToIntersect[0][0]) * planeEq[i][0]) + ((planeCoordsToIntersect[0][1]) * planeEq[i][1]) + ((planeCoordsToIntersect[0][2]) * planeEq[i][2]));

                double g = planeEq[i][0];
                double h = planeEq[i][1];
                double ii = planeEq[i][2];
                double k = planeEq[i][3];
                ArrayList<double[]> intersections = new ArrayList<>();
                ArrayList<double[]> otherPlaneIntersections = new ArrayList<>();
//            double[][][] intersectionsT = new double[triCount][][];
                ArrayList<double[]> intersectionsOnScreen = new ArrayList<>();
                ArrayList<double[]> otherIntersectionsOnScreen = new ArrayList<>();
                Tri altTri;
                cursorInBounds = new boolean[]{false, false, false};

                for (int m = 0; m < triCount; m++) {
                    altTri = worldTris.get(m);
                    lineCoordsToIntersect = altTri.coordsTranslated;
//                for (int j = 0; j < 3; j++) {
//                    for (int p = 0; p < 3; p++) {
//
//                    }
//                }
                    for (int j = 0; j < 3; j++) {
                        jPlus1 = j + 1;
                        if(jPlus1 >= 3){
                            jPlus1 -= 3;
                        }
                        for (int n = 0; n < 3; n++) {
                            altEdgeLines[m][j][n][0] = lineCoordsToIntersect[j][n];
                            altEdgeLines[m][j][n][1] = (lineCoordsToIntersect[jPlus1][n]) - (lineCoordsToIntersect[j][n]);
                        }
                    }

                    /*
                    double a2 = cameraToCentreLines[m][0][0];
                    double b2 = cameraToCentreLines[m][0][1];
                    double c2 = cameraToCentreLines[m][1][0];
                    double d2 = cameraToCentreLines[m][1][1];
                    double e2 = cameraToCentreLines[m][2][0];
                    double f2 = cameraToCentreLines[m][2][1];

                    double t2 = ((g * a2) + (h * c2) + (ii * e2) - k) / ((g * b2) + (h * d2) + (ii * f2));

                    double[] point2;
                    if(t2 != Double.POSITIVE_INFINITY && !Double.isNaN(t2) && t2 != Double.NEGATIVE_INFINITY){
                        point2 = new double[]{a2 - (b2 * t2), c2 - (d2 * t2), e2 - (f2 * t2)};

                        otherPlaneIntersections.add(point2);
                    }
                     */

                    for (int j = 0; j < 3; j++) {

                        a = altEdgeLines[m][j][0][0];
                        b = altEdgeLines[m][j][0][1];
                        c = altEdgeLines[m][j][1][0];
                        d = altEdgeLines[m][j][1][1];
                        e = altEdgeLines[m][j][2][0];
                        f = altEdgeLines[m][j][2][1];

                        if(round(((planeEq[i][0] * b) + (planeEq[i][1] * d) + (planeEq[i][2] * f)), 3) != 0){

                            double t = ((g * a) + (h * c) + (ii * e) - k) / ((g * b) + (h * d) + (ii * f));

                            double[] point;
                            if(t != Double.POSITIVE_INFINITY && !Double.isNaN(t) && t != Double.NEGATIVE_INFINITY){
                                point = new double[]{a - (b * t), c - (d * t), e - (f * t), altTri.ID};

                                intersections.add(point);
                            }
                        }
                    }

                    double intersectionDZ;
                    double[] iOnScreen;
                    double[] iXY;

                    double oIntersectionDZ;
                    double[] oIOnScreen;
                    double[] oIXY;

                    /*
                    if(i != m){
                        for (int j = 0; j < otherPlaneIntersections.size(); j++) {
                            oIXY = new double[2];
                            oIntersectionDZ = distanceToScreen / otherPlaneIntersections.get(j)[2];
                            if(oIntersectionDZ > 0){
                                oIntersectionDZ *= -1;
                            }
                            for (int l = 0; l < 2; l++) {
                                oIXY[l] = otherPlaneIntersections.get(j)[l];
                            }
                            oIOnScreen = new double[]{(oIXY[0] * oIntersectionDZ) + (resolutionWidth / 2.0), (oIXY[1] * oIntersectionDZ) + (resolutionHeight / 2.0), altTri.ID};

                            boolean[] withinBounds;
                            withinBounds = new boolean[]{false, false, false};
                            int oPlus1;
                            for (int o = 0; o < 3; o++) {
                                oPlus1 = o + 1;
                                if(oPlus1 >= 3){
                                    oPlus1 -= 3;
                                }
                                if(inequalityGT[i][o]){
                                    if((round(oIOnScreen[1], 4) >= round(((inequalities[i][o][0] * oIOnScreen[0]) + inequalities[i][o][1]), 4))){
                                        withinBounds[o] = true;
                                    }
                                }else{
                                    if((round(oIOnScreen[1], 4) <= round(((inequalities[i][o][0] * oIOnScreen[0]) + inequalities[i][o][1]), 4))){
                                        withinBounds[o] = true;
                                    }
                                }
                            }
                            if((withinBounds[0] && withinBounds[1] && withinBounds[2])){
                                otherIntersectionsOnScreen.add(oIOnScreen);
                            }else{
                                otherPlaneIntersections.remove(j);
                            }
                        }
                    }

                    double[] distancesToPoints = new double[otherIntersectionsOnScreen.size()];

                    for (int j = 0; j < otherIntersectionsOnScreen.size(); j++) {
                        distancesToPoints[j] = Math.sqrt(((otherIntersectionsOnScreen.get(j)[0] - cameraPos[0]) * (otherIntersectionsOnScreen.get(j)[0] - cameraPos[0]))
                                + ((otherIntersectionsOnScreen.get(j)[1] - cameraPos[1]) * (otherIntersectionsOnScreen.get(j)[1] - cameraPos[1]))
                                + ((otherIntersectionsOnScreen.get(j)[2] - cameraPos[2]) * (otherIntersectionsOnScreen.get(j)[2] - cameraPos[2])));
                    }

                    if(otherIntersectionsOnScreen.size() > 1){
                        boolean sorted;
                        boolean[] checks = new boolean[otherIntersectionsOnScreen.size() - 1];
                        Tri tempTri;
                        int pointer = 0;
                        do{
                            pointer++;
                            if(pointer == otherIntersectionsOnScreen.size()){
                                pointer = 0;
                            }
                            if(distancesToPoints[pointer - 1] < distancesToPoints[pointer]){
                                tempTri = worldTris.get(pointer - 1);
                                worldTris.set(pointer - 1, worldTris.get(pointer));
                                worldTris.set(pointer, tempTri);
                            }
                            checks[pointer - 1] = true;
                            sorted = true;
                            for (int z = 0; z < otherIntersectionsOnScreen.size() - 1; z++) {
                                if(!checks[z]){
                                    sorted = false;
                                    break;
                                }
                            }
                        }while(!sorted);
                    }
                     */

                    for (int j = 0; j < intersections.size(); j++) {
//                    intersectionsT[i][j] = rotatePoint(intersections.toArray(new double[intersections.size()][3]), new double[intersections.size()][3], j, eulerMatrix);
                        iXY = new double[2];
                        intersectionDZ = distanceToScreen / intersections.get(j)[2];
                        if(intersectionDZ > 0){
                            intersectionDZ *= -1;
                        }
                        for (int l = 0; l < 2; l++) {
                            iXY[l] = intersections.get(j)[l];
                        }
                        iOnScreen = new double[]{(iXY[0] * intersectionDZ) + (resolutionWidth / 2.0), (iXY[1] * intersectionDZ) + (resolutionHeight / 2.0), altTri.ID};

                        boolean[] withinBounds;
                        withinBounds = new boolean[]{false, false, false};
                        int oPlus1;
                        for (int o = 0; o < 3; o++) {
                            oPlus1 = o + 1;
                            if(oPlus1 >= 3){
                                oPlus1 -= 3;
                            }
//                        boolean withinX = ((round(iOnScreen[0], 0) >= round((altTri.pointsOnScreen[o][0] + (resolutionWidth / 2.0)), 0)) && (round(iOnScreen[0], 0) <= round((altTri.pointsOnScreen[oPlus1][0] + (resolutionWidth / 2.0)), 0)))
//                                || ((round(iOnScreen[0], 0) <= round((altTri.pointsOnScreen[o][0] + (resolutionWidth / 2.0)), 0)) && (round(iOnScreen[0], 0) >= round((altTri.pointsOnScreen[oPlus1][0] + (resolutionWidth / 2.0)), 0)));
//                        boolean withinY = ((round(iOnScreen[1], 0) >= round((altTri.pointsOnScreen[o][1] + (resolutionHeight / 2.0)), 0)) && (round(iOnScreen[1], 0) <= round((altTri.pointsOnScreen[oPlus1][1] + (resolutionHeight / 2.0)), 0)))
//                                || ((round(iOnScreen[1], 0) <= round((altTri.pointsOnScreen[o][1] + (resolutionHeight / 2.0)), 0)) && (round(iOnScreen[1], 0) >= round((altTri.pointsOnScreen[oPlus1][1] + (resolutionHeight / 2.0)), 0)));
                            if(inequalityGT[i][o]){
                                if((round(iOnScreen[1], 4) >= round(((inequalities[i][o][0] * iOnScreen[0]) + inequalities[i][o][1]), 4))){
//                                if(withinX || withinY){
//
//                                }
                                    withinBounds[o] = true;
//                                System.out.println(Math.round(iOnScreen[1] * 100) / 100.0 + " >= " + Math.round(((inequalities[i][o][0] * iOnScreen[0]) + inequalities[i][o][1]) * 100) / 100.0);
                                }
                            }else{
                                if((round(iOnScreen[1], 4) <= round(((inequalities[i][o][0] * iOnScreen[0]) + inequalities[i][o][1]), 4))){
//                                if(withinX || withinY){
//
//                                }
                                    withinBounds[o] = true;
//                                System.out.println(Math.round(iOnScreen[1] * 100) / 100.0 + " <= " + Math.round(((inequalities[i][o][0] * iOnScreen[0]) + inequalities[i][o][1]) * 100) / 100.0);
                                }
                            }

                            if(currentTri.ID == 1){
//                            if(rightMouseHeld){
//                                System.out.println();
//                            }
//                            StringBuilder intersectionList = new StringBuilder("Intersections: ");
//                            for (int l = 0; l < intersections.size(); l++) {
//                                intersectionList.append("\n {").append(Math.round(intersections.get(l)[0] * 100) / 100.0).append(", ").append(Math.round(intersections.get(l)[1] * 100) / 100.0).append(", ").append(Math.round(intersections.get(l)[2] * 100) / 100.0).append("} ");
//                            }
                                intersectionInfo = "Specs for Tri ID 1: " +
                                        "\n 1. y = " + (round(inequalities[i][0][0], 2)) + "x + " + (round(inequalities[i][0][1], 2)) +
                                        "\n 2. y = " + (round(inequalities[i][1][0], 2)) + "x + " + (round(inequalities[i][1][1], 2)) +
                                        "\n 3. y = " + (round(inequalities[i][2][0], 2)) + "x + " + (round(inequalities[i][2][1], 2)) +
                                        "\n {" + inequalityGT[i][0] + ", " + inequalityGT[i][1] + ", " + inequalityGT[i][2] + "}" +
                                        "\n {" + withinBounds[0] + ", " + withinBounds[1] + ", " + withinBounds[2] + "}" +
//                                    "\n " +
//                                    "\n " + intersectionList;
                                        "\n {" + cursorInBounds[0] + ", " + cursorInBounds[1] + ", " + cursorInBounds[2] + "}";
//                            if(inequalityGT[i][o]){
//                                if((50 >= ((inequalities[i][o][0] * 50) + inequalities[i][o][1] * 100))){
//                                    cursorInBounds[o] = true;
////                                    System.out.println((Math.round(inequalities[i][o][1] * 100) / 100.0));
//                                }
//                            }else{
//                                if((50 <= ((inequalities[i][o][0] * 50) + inequalities[i][o][1] * 100))){
//                                    cursorInBounds[o] = true;
////                                    System.out.println((Math.round(inequalities[i][o][1] * 100) / 100.0));
//                                }
//                            }
                            }
                        }
                        if((withinBounds[0] && withinBounds[1] && withinBounds[2])){
                            intersectionsOnScreen.add(iOnScreen);
//                        if(rightMouseHeld){
//                            System.out.println();
//                        }
                        }else{
                            intersections.remove(j);
//                        if(rightMouseHeld){
//                            System.out.println();
//                        }
                        }
                    }
                }
                worldTris.get(i).setIntersectionPoints(intersections.toArray(new double[intersections.size()][4]));
                worldTris.get(i).setIPointsOnScreen(intersectionsOnScreen.toArray(new double[intersectionsOnScreen.size()][3]));
//            }
        }
    }

    /**
     * A very simple utility method which rounds the given double to the given number of decimal places.
     * @param value The value to be rounded
     * @param DP The number of decimal places to round to
     * @return The rounded value
     */

    public static double round(double value, int DP){
        return Math.round(value * (10 ^ DP)) / (double) (10 ^ DP);
    }

    /**
     * Another utility method returning the euler matrix of the current camera angle, solely used in rotating the
     * world geometry around the user.
     * @return The euler matrix
     */

    public static double[][] eulerAngleCalc(){
        double yaw = -cameraAngle[0];
        double pitch = 90;
        double roll = cameraAngle[1];

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

        return multiplyMatrices(m2, mA);
    }

    /**
     * A specialised method in rotating a given array of points.
     * @param coords Two dimensional array of points of a world triangle
     * @param coordsM The offset of said points if they had been moved by the user
     * @param m3 The euler matrix fetched before the point translation
     * @return The resultant translated coordinates
     */

    public static double[] rotatePoint(double[] coords,double[] coordsM, double[][] m3){
        double[] coordsTranslated;

        double px = coords[0] - cameraPos[0] + coordsM[0];
        double py = coords[1] - cameraPos[1] + coordsM[1];
        double pz = coords[2] - cameraPos[2] + coordsM[2];

        coordsTranslated = new double[3];
        double a = m3[0][0];
        double b = m3[1][0];
        double c = m3[2][0];
        double d = m3[0][1];
        double e = m3[1][1];
        double f = m3[2][1];
        double g = m3[0][2];
        double h = m3[1][2];
        double ii = m3[2][2];

        coordsTranslated[0] = (a * px + b * pz + c * py);
        coordsTranslated[1] = (d * px + e * pz + f * py);
        coordsTranslated[2] = (g * px + h * pz + ii * py);

        return coordsTranslated;
    }

    /**
     * A bubble sort of the dynamic array containing all world geometry details by their distances to the camera.
     * Used to draw closer shapes in front of further shapes to have the world viewed correctly.
     */

    public static void sortWorld(){
        boolean sorted;
        boolean[] checks = new boolean[world.size() - 1];
        Quad tempQuad;
        int pointer = 0;
        if(quadCount > 1){
            do{
                pointer++;
                if(pointer == world.size()){
                    pointer = 0;
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
        }

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

    /**
     * Utility method used for the specialised way to multiply two matrices.
     * @param m1 Matrix 1
     * @param m2 Matrix 2
     * @return The product of matrix 1 and 2
     */

    public static double[][] multiplyMatrices(double[][] m1, double[][] m2){
        double[][] result = new double[m1.length][m2[0].length];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = multiplyMatricesCell(m1, m2, i, j);
            }
        }

        return result;
    }

    /**
     * A small method solely used in conjunction in the multiplyMatrices method to multiply the correct row and column of two matrices.
     * @param m1 Matrix 1
     * @param m2 Matrix 2
     * @param row The row of matrix 1 to be used
     * @param col The column of matrix 2 to be used
     * @return The result of multiplying the specified row and column
     */

    public static double multiplyMatricesCell(double[][] m1, double[][] m2, int row, int col){
        double cell = 0;

        for (int i = 0; i < m2.length; i++) {
            cell += m1[row][i] * m2[i][col];
        }

        return cell;
    }

    /**
     * A short method called upon a key press and adds it to the bit set of pressed keys.
     * @param e The KeyEvent called
     */

    private void keyPressed(KeyEvent e){
        int keyCode = e.getCode().hashCode();
        keysPressed.set(keyCode);
    }

    /**
     * A short method called upon a key released and removes it from the bit set instead.
     * @param e The KeyEvent called
     */

    private void keyReleased(KeyEvent e){
        int keyCode = e.getCode().hashCode();
        keysPressed.clear(keyCode);
    }

    /**
     * When the mouse is moved in the game scene, this method is called to process the mouse movement and change the camera angles appropriately.
     * If the mouse is not currently within the game window bounds, this mouse movement is not processed.
     * @param e The MouseEvent called
     * @param stage The JavaFX stage to determine if the mouse is in bounds or not
     */

    private void mouseMoved(MouseEvent e, Stage stage){
        /*
         * Since the MouseEvent returns the cursor's position relative to the whole screen,
         * not just the game window, this boolean is made to check if it is indeed in the game window.
         */

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

    /**
     * This method is continuously called along with the render method and evaluates the bit set of keys pressed,
     * moving the player in the appropriate direction.
     * Trigonometry is used to correctly move the player if they are facing diagonally.
     */

    private void checkKeys(){
        double cosX = Math.cos(Math.toRadians(cameraAngle[0]));
        double sinX = Math.sin(Math.toRadians(cameraAngle[0]));
        double nCosX = Math.cos(Math.toRadians(-cameraAngle[0]));
        double nSinX = Math.sin(Math.toRadians(-cameraAngle[0]));

        //Forwards
        if(keysPressed.get(KeyCode.UP.hashCode())){
            cameraDeltaUpDown[2] = -moveSpeed * cosX;
            cameraDeltaUpDown[0] = -moveSpeed * sinX;
        }
        //Backwards
        if(keysPressed.get(KeyCode.DOWN.hashCode())){
            cameraDeltaUpDown[2] = moveSpeed * cosX;
            cameraDeltaUpDown[0] = moveSpeed * sinX;
        }
        //Left
        if(keysPressed.get(KeyCode.LEFT.hashCode())){
            cameraDeltaLeftRight[0] = moveSpeed * nCosX;
            cameraDeltaLeftRight[2] = moveSpeed * nSinX;
        }
        //Right
        if(keysPressed.get(KeyCode.RIGHT.hashCode())){
            cameraDeltaLeftRight[0] = -moveSpeed * nCosX;
            cameraDeltaLeftRight[2] = -moveSpeed * nSinX;
        }

        // Limit the players movement if it is above the allowed movement speed.
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

        //Up
        if(keysPressed.get(KeyCode.SPACE.hashCode())){
            cameraDeltaUpDown[1] = moveSpeed;
        }

        //Down
        if(keysPressed.get(KeyCode.SHIFT.hashCode())){
            cameraDeltaUpDown[1] = -moveSpeed;
        }
    }

    /**
     * Another method continuously called, its purpose is to gradually slow the player down
     * and move the player by the amounts specified by the checkKeys method.
     */

    private static void cycleMovement(){
        for (int i = 0; i < 3; i++) {
            cameraDeltaUpDown[i] *= 0.9;
            cameraDeltaLeftRight[i] *= 0.9;
            cameraPos[i] += cameraDeltaUpDown[i] + cameraDeltaLeftRight[i];
        }
    }

    /**
     * Only used by the mouseMoved method, it simply uses a Java AWT robot to move the cursor to
     * the middle of the screen when it is within the game window and the game is not paused.
     * @param stage The game stage used to judge where the game window's centre is
     */

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

/**
 * A custom object only used to draw the editing arrows, previously used to draw all world elements.
 */

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

    // Many methods merely used to set certain variables within this object.
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

/**
 * An object which is used similarly to the Quad object, only supporting three points and not four.
 * This object is used to draw the world elements instead of the Quad object so they can be observed correctly.
 */

class Tri{

    protected int ID;
    protected double[][] coordinatesInWorld;
    protected double[][] pointsOnScreen;
    protected double[][] coordsTranslated;
    protected double[] centreOfTri;
    protected double[] centreOfTriOnScreen;
    protected double[][] intersectionPoints;
    protected double[][] iPointsOnScreen;
    protected Text[] iInfo;
    protected double distanceToCamera;
    protected int selectedPoint;
    protected double[][] pointsMovement;
    protected double[][] oldPointsMovement;
    protected Color colour;
    protected int[] anchoredTo;
    protected boolean hidden;
    protected double[] planeEq;
    protected double[][] edgeLines;
    protected boolean split;
    protected Tri[] subTris;
    protected int overLappedBy;
    protected int overLapping;
    protected double[][] overlapPoints;


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
        split = false;
        overLappedBy = 0;
        overLapping = 0;
    }

    // Many methods again merely used to set certain variables within this object.
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

    public void setIInfo(Text[] i){
        iInfo = i;
    }

    public void setDistance(double newDistance){
        distanceToCamera = newDistance;
    }

    public void setColour(Color newColour){
        colour = newColour;
    }

    public void setSplit(boolean s){
        split = s;
    }

    public void setSubTris(Tri[] sT){
        subTris = sT;
    }

    public void setOverlapPoints(double[][] oP){
        overlapPoints = oP;
    }
}

/**
 * A custom object which was meant to be used further on in development in order to render higher definition elements.
 */

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

    // Many methods again merely used to set certain variables within this object.
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

/**
 * A custom object storing the details of light elements in the world.
 */

class Light{

    protected int ID;
    protected double[] pos;
    protected double[] posOnScreen;
    protected double brightness;

    public Light(int Id, double[] p, double b){
        ID = Id;
        pos = p;
        brightness = b;
    }

    // Many methods again merely used to set certain variables within this object.
    public void setPosScreen(double[] newScreenPos){
        posOnScreen = newScreenPos;
    }

    public void setBrightness(double newBrightness){
        brightness = newBrightness;
    }
}

/**
 * A custom object again, this time used in creating the GUI. It contains all variables to do with its animations.
 */

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
    protected boolean enabled;

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
        imageView.setTranslateX(translateX);
        imageView.setTranslateY(translateY);
        imageView.setOpacity(opacity);
        speedMultiplier = sM;
        enabled = true;
    }

    // Animation methods designed to be continuously called to create an animation
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

/**
 * Another custom object only used in GUI, this object is similar to the CustomImage class, only supporting a JavaFX text object instead of an image and ImageView.
 */

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

    // Animation methods again designed to be continuously called to create an animation
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

/**
 * The final custom object, used in GUI in the map select menu. It is similar to the CustomImage class,
 * only it features a map icon, title, description, and background all contained within a JavaFX Group.
 */

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
    protected int theta3;
    protected double raisedPixels;
    protected double pagePixels;
    protected boolean selected;
    protected File mapFile;
    protected int xPos;
    protected int yPos;
    protected int page;
    protected int[] rowCol;

    public ListMap(String t, String desc1, String desc2, Image b, Image bS, Image i, String pN, int p, int row, int column){
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
        if(iconImage.getWidth() != 64){
            icon.setFitWidth(64);
        }
        if(iconImage.getHeight() != 64){
            icon.setFitHeight(64);
        }
        map = new Group(base);
        map.getChildren().add(titleText);
        map.getChildren().add(descText1);
        map.getChildren().add(descText2);
        map.getChildren().add(icon);
        map.setOpacity(opacity);
        selected = false;
        mapFile = new File(pN);
        xPos = 0;
        yPos = 0;
        page = p;
        rowCol = new int[]{row, column};
        if(page == 1){
            theta3 = 90;
        }else{
            theta3 = 0;
        }
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

    // Animation methods again designed to be continuously called to create an animation
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

    /*
     * The series of 'page' methods are currently not used, but would have been used to create an animation when the user scrolls between map pages.
     * Currently scrolling between pages is not supported.
     */
    public void pageInFromLeft(){
        if(theta3 < 90){
            theta3 += Main.buttonSpeed;
            pagePixels = ((Math.sin(Math.toRadians(theta3)) / 2) + 0.5) * 15;
            opacity = Math.sin(Math.toRadians(theta3));
            map.setTranslateX(xPos + pagePixels);
            map.setOpacity(opacity);
        }
    }

    public void pageOutToLeft(){
        if(theta3 > 0){
            theta3 -= Main.buttonSpeed;
            pagePixels = ((Math.sin(Math.toRadians(theta3)) / 2) + 0.5) * 15;
            map.setTranslateX(xPos + pagePixels);
            opacity = Math.sin(Math.toRadians(theta3));
            map.setOpacity(opacity);
        }
    }

    public void pageInFromRight(){
        if(theta3 < 90){
            theta3 += Main.buttonSpeed;
            pagePixels = ((Math.sin(Math.toRadians(theta3)) / 2) + 0.5) * 15;
            opacity = Math.sin(Math.toRadians(theta3));
            map.setTranslateX(xPos - pagePixels);
            map.setOpacity(opacity);
        }
    }

    public void pageOutToRight(){
        if(theta3 > 0){
            theta3 -= Main.buttonSpeed;
            pagePixels = ((Math.sin(Math.toRadians(theta3)) / 2) + 0.5) * 15;
            opacity = Math.sin(Math.toRadians(theta3));
            map.setTranslateX(xPos - pagePixels);
            map.setOpacity(opacity);
        }
    }

    public void hoverAnimUp(){
        if(theta2 < 180){
            theta2 += Main.buttonSpeed;
            raisedPixels = ((Math.sin(Math.toRadians(theta2 - 90)) / 2) + 0.5) * 10;
            map.setTranslateY(-raisedPixels + yPos);
        }
    }

    public void hoverAnimDown(){
        if(theta2 > 0){
            theta2 -= Main.buttonSpeed;
            raisedPixels = ((Math.sin(Math.toRadians(theta2 - 90)) / 2) + 0.5) * 10;
            map.setTranslateY(-raisedPixels + yPos);
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

    public void updatePos(){
        pagePixels = ((Math.sin(Math.toRadians(theta3)) / 2) + 0.5) * 15;
        map.setTranslateX(xPos + pagePixels);
        map.setTranslateY(yPos);
        map.setOpacity(opacity);
    }
}