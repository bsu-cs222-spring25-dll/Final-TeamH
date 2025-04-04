package edu.bsu.cs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

        StreamerSearchService searchService = new StreamerSearchService(ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube(), ApiInitializer.TwitchAuthToken, ApiInitializer.YoutubeAuthToken);

        guiSearchHandler = new GUISearchHandler(searchService);
        guiStreamerInfo = new GUIStreamerInfo(new ChannelInfoService(ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube(), ApiInitializer.YoutubeAuthToken), new ProfilePictureService(ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube()));

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
                showStreamerScene(primaryStage, username, platform);
            } else {
                resultLabel.setText("Streamer not found.");
            }
        });

        VBox inputSection = new VBox(10, titleLabel, inputRow, searchButton, resultLabel);
        inputSection.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, inputSection);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene searchScene = new Scene(layout, 600, 300);
        primaryStage.setScene(searchScene);
        primaryStage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showStreamerScene(Stage primaryStage, String username, String platform) {
        Label titleLabel = new Label("Streamer Tracker");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> start(primaryStage));

        HBox topBar = new HBox(10, titleLabel, returnButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));

        Label streamerInfo = new Label("Now viewing: " + username + " on " + platform);
        streamerInfo.setStyle("-fx-font-size: 16px;");

        String profilePictureUrl = guiStreamerInfo.fetchProfilePicture(username, platform);
        ImageView profileImageView = new ImageView();

        if (profilePictureUrl != null) {
            Image profileImage = new Image(profilePictureUrl, 100, 100, true, true);
            profileImageView.setImage(profileImage);
        } else {
            profileImageView.setImage(new Image("default-profile.png")); // Fallback image
        }

        VBox layout = new VBox(20, topBar, streamerInfo, profileImageView);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Scene streamerScene = new Scene(layout, 600, 400); // Increased height to fit image
        primaryStage.setScene(streamerScene);
    }

}
