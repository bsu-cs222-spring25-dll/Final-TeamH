package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Scanner;

public class GUI extends Application {
    private StreamerSearchHandler searchHandler;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Streamer Tracker");

        // UI Elements
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Streamer Username");

        ComboBox<String> platformBox = new ComboBox<>();
        platformBox.getItems().addAll("Twitch", "YouTube");
        platformBox.setValue("Twitch");

        Button searchButton = new Button("Search");
        Label resultLabel = new Label();
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        // Initialize API clients using ApiInitializer
        ITwitchClient twitchClient = ApiInitializer.initializeTwitch();
        YouTube youtubeClient = ApiInitializer.initializeYoutube();

        // Initialize StreamerSearchHandler with API clients
        StreamerSearchService searchService = new StreamerSearchService(
                twitchClient,
                youtubeClient,
                ApiInitializer.TwitchAuthToken,
                ApiInitializer.YoutubeAuthToken
        );
        searchHandler = new StreamerSearchHandler(new Scanner(System.in), searchService);

        // Search button action
        searchButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String platform = platformBox.getValue();

            if (username.isEmpty()) {
                resultLabel.setText("Error: Please enter a valid username.");
                outputArea.clear();
                return;
            }

            String result = searchHandler.searchStreamer(platform);
            if (!result.isEmpty()) {
                resultLabel.setText("Streamer Found: " + result);
                outputArea.setText("Displaying details for " + result);
            } else {
                resultLabel.setText("Streamer not found.");
                outputArea.clear();
            }
        });

        // Layout
        VBox layout = new VBox(10, usernameField, platformBox, searchButton, resultLabel, outputArea);
        layout.setPadding(new Insets(15));

        primaryStage.setScene(new Scene(layout, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
