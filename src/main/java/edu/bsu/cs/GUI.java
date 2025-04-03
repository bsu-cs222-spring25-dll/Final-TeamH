package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application {
    private GUISearchHandler guiSearchHandler;
    private GUIStreamerInfo guiStreamerInfo;
    private TextField usernameInput;
    private Label resultLabel;
    private ChoiceBox<String> platformSelector;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Streamer Tracker");

        ITwitchClient twitchClient = ApiInitializer.initializeTwitch();
        YouTube youtubeService = ApiInitializer.initializeYoutube();
        String youtubeApiKey = ApiInitializer.YoutubeAuthToken;

        ChannelInfoService channelInfoService = new ChannelInfoService(twitchClient, youtubeService, youtubeApiKey);
        guiStreamerInfo = new GUIStreamerInfo(channelInfoService);

        Label titleLabel = new Label("Streamer Tracker");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usernameInput = new TextField();
        usernameInput.setPromptText("Enter Streamer Username...");
        usernameInput.setMaxWidth(250);

        platformSelector = new ChoiceBox<>();
        platformSelector.getItems().addAll("Twitch", "YouTube");
        platformSelector.setValue("Twitch");

        HBox inputRow = new HBox(10, usernameInput, platformSelector);
        inputRow.setAlignment(Pos.CENTER);

        Button searchButton = new Button("Search");
        searchButton.setMinWidth(100);

        resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px;");

        searchButton.setOnAction(e -> {
            String platform = platformSelector.getValue();
            String username = usernameInput.getText().trim();

            if (username.isEmpty() || username.contains(" ")) {
                showError("Invalid Username", "Usernames cannot contain spaces.");
                return;
            }

            String result = guiSearchHandler.GUISearchStreamer(username, platform);

            if (!result.isEmpty()) {
                resultLabel.setText("Streamer Found: " + result);
            } else {
                resultLabel.setText("Streamer not found.");
            }
        });

        VBox inputSection = new VBox(10, titleLabel, inputRow, searchButton, resultLabel);
        inputSection.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, inputSection);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
