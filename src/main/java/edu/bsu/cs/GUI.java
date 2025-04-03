package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Streamer Tracker");

        ITwitchClient twitchClient = ApiInitializer.initializeTwitch();
        YouTube youtubeClient = ApiInitializer.initializeYoutube();

        StreamerSearchService searchService = new StreamerSearchService(
                twitchClient, youtubeClient, ApiInitializer.TwitchAuthToken, ApiInitializer.YoutubeAuthToken
        );
        guiSearchHandler = new GUISearchHandler(searchService);

        usernameInput = new TextField();
        usernameInput.setPromptText("Enter Streamer Username...");

        Label resultLabel = new Label();
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String username = usernameInput.getText().trim();
            if (username.isEmpty() || username.contains(" ")) {
                showErrorDialog("Invalid Username", "Please enter a valid username without spaces.");
                return;
            }

            boolean isFound = guiSearchHandler.GUISearchStreamer(username);
            if (isFound) {
                resultLabel.setText("Success!");
                outputArea.setText("Streamer Found: " + username);
            } else {
                showErrorDialog("Streamer Not Found", "The entered streamer does not exist.");
            }
        });

        VBox layout = new VBox(10, usernameInput, searchButton, resultLabel, outputArea);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
