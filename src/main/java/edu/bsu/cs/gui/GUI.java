package edu.bsu.cs.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        GUIScreenBuilder builder = new GUIScreenBuilder();
        GUIScreenController controller = new GUIScreenController(primaryStage);
        Scene scene = new Scene(builder.buildMainScreen(controller), 1000, 700);

        primaryStage.setTitle("Streamer Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}