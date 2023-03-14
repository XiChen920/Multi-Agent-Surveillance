/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;

import com.google.gson.Gson;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.maastrichtuniversity.dke.gamecontrollersample.experiments.Results;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static javafx.application.Application.launch;

/**
 *
 * @author joel
 */

public class GameRunner extends Application {
    protected String mapDoc;
    protected static Scenario scenario;
    final int RECTANGLE_SIZE = 6;
    private GridPane grid;
    private Label label;
    private Label labelTimesteps;
    private Stage mainStage;
    int steps;
    private Timeline timeline;
    ArrayList<Rectangle> rectangles;

    static GamePlayer p;
    boolean isExplorerGame;
    boolean isRandomAgent;
    static String mapD_easy;
    static String mapD_medium;
    static String mapD_hard;

    public static void main(String[] args){
        // the mapscenario should be passed as a parameter
        String userDir = System.getProperty("user.dir");
        System.out.println(userDir);
        String separator = System.getProperty("file.separator");
        mapD_easy= userDir + separator + "testmap.txt";
        mapD_medium = userDir + separator + "testmap_medium.txt";
//        mapD_medium = userDir + separator + "testmap_test.txt";
        mapD_hard = userDir + separator + "testmap_hard.txt";
//        scenario = new Scenario(mapD_easy);
        launch(args);

    }


    public void start(Stage primaryStage) throws Exception{
        openingMenu();
    }

    public void initUI(boolean isExplorerGame, boolean isRandomAgent) throws Exception {
        p.setup(isRandomAgent);
        p.start(isExplorerGame, isRandomAgent);

/*
        Board testBoard = p.board;
        Grid[][] boardXY = testBoard.getBoardXY();
        Yell testYell =new Yell(boardXY[8][8], testBoard);
        Rustle testRustle =new Rustle(boardXY[8][10], testBoard);
        Yell testYell_1 =new Yell(boardXY[12][12], testBoard);
        Rustle testRustle_1 =new Rustle(boardXY[11][7], testBoard);
*/
        if (isExplorerGame) {
            steps = 0;
            rectangles = new ArrayList<>();

            int SCENE_HEIGHT = RECTANGLE_SIZE * p.board.getBoardXY()[0].length + 25;
            int SCENE_WIDTH = RECTANGLE_SIZE * p.board.getBoardXY().length;
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
            mainStage = new Stage();
            Scene scene = new Scene(root, SCENE_WIDTH + SCENE_WIDTH / 100, SCENE_HEIGHT + SCENE_HEIGHT / 100);
            grid = new GridPane();

            for (int i = 0; i < p.board.getBoardXY().length; i++) {
                for (int j = 0; j < p.board.getBoardXY()[0].length; j++) {
                    Rectangle rectangle1 = new Rectangle(RECTANGLE_SIZE, RECTANGLE_SIZE);
                    rectangles.add(rectangle1);
                    rectangle1.setFill(Color.BLACK);
                    if (p.board.getGrid(i, j).isGuardSpawn()) {
                        rectangle1.setFill(Color.BLACK);
                    }
                    if (p.board.getGrid(i, j).isIntruderSpawn()) {
                        rectangle1.setFill(Color.BLACK);
                    }
                    if (p.board.getGrid(i, j).isTarget()) {
                        rectangle1.setFill(Color.BLACK);
                    }
                    if (p.board.getGrid(i, j).isWall()) {
                        rectangle1.setFill(Color.BLACK);
                    }

                    if (p.board.getGrid(i, j).isShade()) {
                        rectangle1.setFill(Color.BLACK);
                    }

                    if (p.board.getGrid(i, j).isTelePortal()) {
                        rectangle1.setFill(Color.BLACK);
                    }
                    if (p.board.getGrid(i, j).isViewed()) {
                        rectangle1.setFill(Color.BLACK);
                    }
//                    if (p.board.getGrid(i, j).isHeard()) {
//                        rectangle1.setFill(Color.YELLOW);
//                    }
                    if (p.board.getGrid(i, j).hasGuard()) {
                        rectangle1.setFill(Color.BLACK);
                    }
                    if (p.board.getGrid(i, j).hasIntruder()) {
                        rectangle1.setFill(Color.BLACK);

                    }


                    GridPane.setRowIndex(rectangle1, j);
                    GridPane.setColumnIndex(rectangle1, i);
                    grid.getChildren().add(rectangle1);
                }
            }
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(grid);
            VBox vBox = new VBox();
            vBox.getChildren().add(scrollPane);
            scene.setRoot(vBox);
            HBox hBox = new HBox();
            Button playButton = new Button("Play");
            Button pauseButton = new Button("Pause");
            Button resetButton = new Button("Reset");
            Button returnToMenu = new Button("Return to Menu");
            Button colorLegend = new Button("Color Legend");
            Label labelSpacing1 = new Label("   ");
            Label labelSpacing2 = new Label("          ");
            Label labelSpacing3 = new Label("   ");
            Label labelSpacing4 = new Label("   ");
            Label labelSpacing5 = new Label("    ");
            Label labelSpacing6 = new Label("    ");


            label = new Label("Currently discovered: " + discoveredTiles() + "/" + ((p.board.getBoardXY().length * p.board.getBoardXY()[0].length)));
            labelTimesteps = new Label("Time-steps taken: " + steps);
            hBox.getChildren().addAll(playButton, labelSpacing1, pauseButton, labelSpacing3, resetButton, labelSpacing4, returnToMenu, labelSpacing6, colorLegend, labelSpacing2, label, labelSpacing5, labelTimesteps);
            vBox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            vBox.getChildren().add(hBox);
            timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {
                for (int i = 0; i < p.board.getBoardXY().length; i++) {
                    for (int j = 0; j < p.board.getBoardXY()[0].length; j++) {
                        p.board.getBoardXY()[i][j].setViewed(false);
                        p.board.getBoardXY()[i][j].setHeard(false);
                        p.board.getBoardXY()[i][j].setNoise(false);
                    }
                }
                if (p.getIsExplorerGame()) {
                    if(p.explored(p.board)){
                        try {
                            completionScreen();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        p.gw.watchGame();
                        steps++;
                        rerenderUI(isExplorerGame);
                    }
                } else {
                    if(p.intruders.isEmpty()){
                        try {
                            completionScreen();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        p.gw.watchGame();
                        steps++;
                        rerenderUI(isExplorerGame);
                    }
                }

            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            mainStage.setOnCloseRequest(windowEvent -> {
                timeline.stop();
            });
            returnToMenu.setOnAction(event -> {
                mainStage.close();
                timeline.stop();
                try {
                    openingMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            colorLegend.setOnAction(event -> {
                try {
                    colorLegendScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            resetButton.setOnAction(event -> {
                timeline.stop();
                mainStage.close();
                try {
                    initUI(isExplorerGame, isRandomAgent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            playButton.setOnAction(event -> {
                timeline.play();
            });
            pauseButton.setOnAction(event -> {
                timeline.stop();
            });
            mainStage.setScene(scene);
            mainStage.show();
        }
        else{
            steps = 0;
            rectangles = new ArrayList<>();

            int SCENE_HEIGHT = RECTANGLE_SIZE * p.board.getBoardXY()[0].length + 25;
            int SCENE_WIDTH = RECTANGLE_SIZE * p.board.getBoardXY().length;
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
            mainStage = new Stage();
            Scene scene = new Scene(root, SCENE_WIDTH + SCENE_WIDTH / 100, SCENE_HEIGHT + SCENE_HEIGHT / 100);
            grid = new GridPane();

            for (int i = 0; i < p.board.getBoardXY().length; i++) {
                for (int j = 0; j < p.board.getBoardXY()[0].length; j++) {
                    Rectangle rectangle1 = new Rectangle(RECTANGLE_SIZE, RECTANGLE_SIZE);
                    rectangles.add(rectangle1);
                    rectangle1.setFill(Color.LIGHTGREEN);
                    if (p.board.getGrid(i, j).isGuardSpawn()) {
                        rectangle1.setFill(Color.LIGHTCORAL);
                    }
                    if (p.board.getGrid(i, j).isIntruderSpawn()) {
                        rectangle1.setFill(Color.LIGHTYELLOW);
                    }
                    if (p.board.getGrid(i, j).isTarget()) {
                        rectangle1.setFill(Color.GOLD);
                    }
                    if(p.board.getGrid(i,j).isBright()){
                        rectangle1.setFill(Color.YELLOW);
                    }
                    if(p.board.getGrid(i,j).isPosPheMarker()){
                        rectangle1.setFill(Color.PURPLE);
                    }
                    if(p.board.getGrid(i,j).isNegPheMarker()){
                        rectangle1.setFill(Color.CHOCOLATE);
                    }
                    if(p.board.getGrid(i,j).isVisionMarkerCorner()){
                        rectangle1.setFill(Color.ORANGE);
                    }
                    if(p.board.getGrid(i,j).isVisionMarkerTelep()){
                        rectangle1.setFill(Color.OLIVE);
                    }
                    if (p.board.getGrid(i, j).isWall()) {
                        rectangle1.setFill(Color.DARKGRAY);
                    }

                    if (p.board.getGrid(i, j).isShade()) {
                        rectangle1.setFill(Color.DARKGREEN);
                    }

                    if (p.board.getGrid(i, j).isTelePortal()) {
                        rectangle1.setFill(Color.ROYALBLUE);
                    }
                    if (p.board.getGrid(i, j).isViewed()) {
                        rectangle1.setFill(Color.LIGHTGRAY);
                    }
//                    if (p.board.getGrid(i, j).isHeard()) {
//                        rectangle1.setFill(Color.YELLOW);
//                    }
                    if (p.board.getGrid(i, j).hasGuard()) {
                        rectangle1.setFill(Color.WHITE);
                    }
                    if (p.board.getGrid(i, j).hasIntruder()) {
                        rectangle1.setFill(Color.RED);

                    }


                    GridPane.setRowIndex(rectangle1, j);
                    GridPane.setColumnIndex(rectangle1, i);
                    grid.getChildren().add(rectangle1);
                }
            }
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(grid);
            VBox vBox = new VBox();
            vBox.getChildren().add(scrollPane);
            scene.setRoot(vBox);
            HBox hBox = new HBox();
            Button playButton = new Button("Play");
            Button pauseButton = new Button("Pause");
            Button resetButton = new Button("Reset");
            Button returnToMenu = new Button("Return to Menu");
            Button colorLegend = new Button("Color Legend");
            Label labelSpacing1 = new Label("   ");
            Label labelSpacing2 = new Label("          ");
            Label labelSpacing3 = new Label("   ");
            Label labelSpacing4 = new Label("   ");
            Label labelSpacing5 = new Label("    ");
            Label labelSpacing6 = new Label("    ");


            label = new Label("Currently discovered: " + discoveredTiles() + "/" + ((p.board.getBoardXY().length * p.board.getBoardXY()[0].length)));
            labelTimesteps = new Label("Time-steps taken: " + steps);
            hBox.getChildren().addAll(playButton, labelSpacing1, pauseButton, labelSpacing3, resetButton, labelSpacing4, returnToMenu, labelSpacing6, colorLegend, labelSpacing2, label, labelSpacing5, labelTimesteps);
            vBox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            vBox.getChildren().add(hBox);
            timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
                for (int i = 0; i < p.board.getBoardXY().length; i++) {
                    for (int j = 0; j < p.board.getBoardXY()[0].length; j++) {
                        p.board.getBoardXY()[i][j].setViewed(false);
                        p.board.getBoardXY()[i][j].setHeard(false);
                        p.board.getBoardXY()[i][j].setNoise(false);

                    }
                }
                if (p.getIsExplorerGame()) {
                    if(p.explored(p.board)){
                        try {
                            completionScreen();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        p.gw.watchGame();
                        steps++;
                        rerenderUI(isExplorerGame);
                    }
                } else {
                    if(p.intruders.isEmpty()){
                        try {
                            completionScreen();
                            System.out.println("Guard caught: " + p.gw.getCaughtIntruderSize() + " Intruder escaped: " + p.gw.getEscapedIntruderSize());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        p.gw.watchGame();
                        steps++;
                        rerenderUI(isExplorerGame);
                    }
                }

            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            mainStage.setOnCloseRequest(windowEvent -> {
                timeline.stop();
            });
            returnToMenu.setOnAction(event -> {
                mainStage.close();
                timeline.stop();
                try {
                    openingMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            colorLegend.setOnAction(event -> {
                try {
                    colorLegendScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            resetButton.setOnAction(event -> {
                timeline.stop();
                mainStage.close();
                try {
                    initUI(isExplorerGame, isRandomAgent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            playButton.setOnAction(event -> {
                timeline.play();
            });
            pauseButton.setOnAction(event -> {
                timeline.stop();
            });
            mainStage.setScene(scene);
            mainStage.show();
        }
    }



    public void rerenderUI(boolean isExplorerGame){
        if(isExplorerGame) {
            for (int i = 0; i < p.board.getBoardXY().length; i++) {
                for (int j = 0; j < p.board.getBoardXY()[0].length; j++) {

                    if (p.board.getGrid(i, j).isExplored()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTGREEN);
                    } else {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.BLACK);
                    }
                    if (p.board.getGrid(i, j).isGuardSpawn()) {
                        if (p.board.getGrid(i, j).isExplored()) {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTCORAL);
                        } else {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.BLACK);
                        }
                    }

                    if (p.board.getGrid(i, j).isIntruderSpawn()) {
                        if (p.board.getGrid(i, j).isExplored()) {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTYELLOW);
                        } else {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.BLACK);
                        }
                    }
                    if (p.board.getGrid(i, j).isTarget()) {
                        if (p.board.getGrid(i, j).isExplored()) {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.GOLD);
                        } else {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.BLACK);
                        }
                    }
                    if (p.board.getGrid(i, j).isWall()) {
                        if (p.board.getGrid(i, j).isExplored()) {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.DARKGRAY);
                        } else {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.BLACK);
                        }
                    }

                    if (p.board.getGrid(i, j).isShade()) {
                        if (p.board.getGrid(i, j).isExplored()) {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.DARKGREEN);
                        } else {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.BLACK);
                        }
                    }

                    if (p.board.getGrid(i, j).isTelePortal()) {
                        if (p.board.getGrid(i, j).isExplored()) {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.ROYALBLUE);
                        } else {
                            rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.BLACK);
                        }
                    }
                    if (p.board.getGrid(i, j).isViewed()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTGRAY);
                    }
//                    if (p.board.getGrid(i, j).isHeard()) {
//                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.YELLOW);
//                    }
                    if (p.board.getGrid(i, j).hasGuard()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.WHITE);
                    }
                    if (p.board.getGrid(i, j).hasIntruder()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.RED);

                    }

                }
            }
            label.setText("Currently discovered: " + discoveredTiles() + "/" + ((p.board.getBoardXY().length * p.board.getBoardXY()[0].length)));
            labelTimesteps.setText("Time-steps taken: " + steps);
        }
        else{
            for (int i = 0; i < p.board.getBoardXY().length; i++) {
                for (int j = 0; j < p.board.getBoardXY()[0].length; j++) {

                    if (p.board.getGrid(i, j).isExplored()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTGREEN);
                    }
                    if (p.board.getGrid(i, j).isGuardSpawn()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTCORAL);
                    }

                    if (p.board.getGrid(i, j).isIntruderSpawn()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTYELLOW);
                    }
                    if (p.board.getGrid(i, j).isTarget()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.GOLD);
                    }
                    if(p.board.getGrid(i,j).isBright()){
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.YELLOW);
                    }
                    if(p.board.getGrid(i,j).isPosPheMarker()){
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.PURPLE);
                    }
                    if(p.board.getGrid(i,j).isNegPheMarker()){
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.CHOCOLATE);
                    }
                    if(p.board.getGrid(i,j).isVisionMarkerCorner()){
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.ORANGE);
                    }
                    if(p.board.getGrid(i,j).isVisionMarkerTelep()){
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.OLIVE);
                    }
                    if (p.board.getGrid(i, j).isWall()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.DARKGRAY);
                    }

                    if (p.board.getGrid(i, j).isShade()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.DARKGREEN);
                    }

                    if (p.board.getGrid(i, j).isTelePortal()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.ROYALBLUE);
                    }
                    if (p.board.getGrid(i, j).isViewed()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.LIGHTGRAY);
                    }
//                    if (p.board.getGrid(i, j).isHeard()) {
//                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.YELLOW);
//                    }
                    if (p.board.getGrid(i, j).hasGuard()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.WHITE);
                    }
                    if (p.board.getGrid(i, j).hasIntruder()) {
                        rectangles.get((i * p.board.getBoardXY()[0].length) + j).setFill(Color.RED);

                    }

                }
            }
            label.setText("Currently discovered: " + discoveredTiles() + "/" + ((p.board.getBoardXY().length * p.board.getBoardXY()[0].length)));
            labelTimesteps.setText("Time-steps taken: " + steps);
        }
    }

    public void openingMenu() throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Menu.fxml")));
        Scene scene = new Scene(root, 900,600);

        primaryStage.setScene(scene);
        root.requestFocus();
        primaryStage.show();
    }

     void agentChooser() throws IOException{
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("AgentChooser.fxml"));
        Scene scene = new Scene(root, 900, 400);

        primaryStage.setScene(scene);
        root.requestFocus();
        primaryStage.show();
    }

    public void colorLegendScreen() throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ColorLegend.fxml")));
        Scene scene = new Scene(root, 350, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void completionScreen() throws IOException{
        timeline.stop();
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
        Scene scene = new Scene(root, 400, 400);
        VBox vBox = new VBox();
        scene.setRoot(vBox);
        vBox.getChildren().add(new Label("Completed in " + steps + " time-steps!"));
        HBox hBox = new HBox();
        Button returnToMenu = new Button("Return to Menu");
        Button resetButton = new Button("Run again");
        returnToMenu.setOnAction(event -> {
            mainStage.close();
            timeline.stop();
            try {
                openingMenu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        resetButton.setOnAction(event -> {
            timeline.stop();
            mainStage.close();
            try {
                primaryStage.close();
                initUI(isExplorerGame, isRandomAgent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hBox.getChildren().addAll(returnToMenu,resetButton);
        vBox.getChildren().add(hBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void simulateMenu() throws IOException {
        //EDIT HOW MANY GUARDS OR INTRUDERS IN TXT FILE

        //SPECIFY SIMULATIONS TO RUN HERE
        Gson gson = new Gson();
        ArrayList<Results> results = new ArrayList<>();

        int SIMULATIONS_TO_RUN = 5;
        AtomicInteger guard_wins = new AtomicInteger();
        AtomicInteger intruder_wins = new AtomicInteger();

        //SPECIFY WHICH MAP HERE
//        p = new GamePlayer(new Scenario(mapD_easy));
//        p = new GamePlayer(new Scenario(mapD_medium));
        p = new GamePlayer(new Scenario(mapD_medium));

        for (int i = 0; i < SIMULATIONS_TO_RUN; i++) {
            p.setup(false);
            p.start(false, false);

            steps = 0;
            while (!p.intruders.isEmpty()) {
                p.gw.watchGame();
                steps++;
            }
            results.add(new Results(i, p.gw.getCaughtIntruderSize(), p.gw.getEscapedIntruderSize(), steps));
//            System.out.println("Guard caught: " + p.gw.getCaughtIntruderSize() + " Intruder escaped: " + p.gw.getEscapedIntruderSize() + " timesteps " + steps);
        }

            Writer writer = new FileWriter("results1.json");
            gson.toJson(results, writer);
            writer.flush();
            writer.close();

        System.out.println("File Generated");
    }

    @FXML
    void simulate(ActionEvent event) throws Exception{
        simulateMenu();

    }
    @FXML
    void guardsVersusIntruders(ActionEvent event) throws Exception {
        ((Node)(event.getSource())).getScene().getWindow().hide();
        mapChooser();
    }

    @FXML
    void explorationEvent(ActionEvent event) throws Exception {
        ((Node)(event.getSource())).getScene().getWindow().hide();
        agentChooser();
    }
    //TODO: add the random agent
    @FXML
    void pickRandomAgent(ActionEvent event) throws Exception {
        ((Node)(event.getSource())).getScene().getWindow().hide();
        scenario = new Scenario(mapD_easy);
        p = new GamePlayer(scenario);
        isExplorerGame=true;
        isRandomAgent=true;
        initUI(isExplorerGame, isRandomAgent);
    }

    @FXML
    void pickGoodAgent(ActionEvent event) throws Exception {
        ((Node)(event.getSource())).getScene().getWindow().hide();
        scenario = new Scenario(mapD_easy);
        p = new GamePlayer(scenario);
        isExplorerGame=true;
        isRandomAgent=false;
        initUI(isExplorerGame, isRandomAgent);
    }


    void mapChooser() throws IOException{
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("MapChooser.fxml"));
        Scene scene = new Scene(root, 900, 400);

        primaryStage.setScene(scene);
        root.requestFocus();
        primaryStage.show();
    }

    @FXML
    void pickEasyMap(ActionEvent event) throws Exception{
        ((Node)(event.getSource())).getScene().getWindow().hide();
        scenario = new Scenario(mapD_easy);
        p = new GamePlayer(scenario);
        isExplorerGame = false;
        isRandomAgent = false;
        initUI(isExplorerGame,isRandomAgent);


    }
    @FXML
    void pickMediumMap(ActionEvent event) throws Exception{
        ((Node)(event.getSource())).getScene().getWindow().hide();
        scenario = new Scenario(mapD_medium);
        p = new GamePlayer(scenario);
        isExplorerGame = false;
        isRandomAgent = false;
        initUI(isExplorerGame,isRandomAgent);

    }
    @FXML
    void pickHardMap(ActionEvent event) throws Exception{
        ((Node)(event.getSource())).getScene().getWindow().hide();
        scenario = new Scenario(mapD_hard);
        p = new GamePlayer(scenario);
        isExplorerGame = false;
        isRandomAgent = false;
        initUI(isExplorerGame,isRandomAgent);

    }

    private int discoveredTiles() {
        int count=0;
        for (int i = 0; i < p.board.getBoardXY().length; i++) {
            for (int j = 0; j < p.board.getBoardXY()[0].length; j++) {
                if(p.board.getGrid(i,j).isExplored()){
                    count++;
                }
            }
        }
        return count;
    }




}
