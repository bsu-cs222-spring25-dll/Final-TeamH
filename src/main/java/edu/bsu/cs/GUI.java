package edu.bsu.cs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application {
    private GUISearchHandler guiSearchHandler;
    private TextField usernameInput;
    private Button searchButton;
    private ComboBox<String> platformDropdown;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Streamer Tracker");

        usernameInput = new TextField();
        usernameInput.setPromptText("Enter Streamer Username...");

        platformDropdown = new ComboBox<>();
        platformDropdown.getItems().addAll("Twitch", "YouTube");
        platformDropdown.setValue("Twitch"); // Default to Twitch

        Label resultLabel = new Label();
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String username = usernameInput.getText().trim();
            String platform = platformDropdown.getValue();

            if (username.isEmpty() || username.contains(" ")) {
                showError("Invalid username! Please enter a single word without spaces.");
                return;
            }

            String result = guiSearchHandler.GUISearchStreamer(platform, username);

            if (!result.isEmpty()) {
                resultLabel.setText("Streamer Found: " + result);
                outputArea.setText("Success! Streamer found on " + platform + ".");
            } else {
                showError("Streamer not found on " + platform + ".");
            }
        });

        guiSearchHandler = new GUISearchHandler();

        VBox layout = new VBox(10, usernameInput, platformDropdown, searchButton, resultLabel, outputArea);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
