package edu.bsu.cs.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUISceneManager {
    private final Stage primaryStage;

    public GUISceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
