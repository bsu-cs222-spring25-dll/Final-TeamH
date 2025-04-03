package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.A;

public class GUI extends Application {
    private GUISearchHandler guiSearchHandler;
    private GUIStreamerInfo guiStreamerInfo;
    private TextField usernameInput;
    private Label resultLabel;
    private TextArea outputArea;
    private ChoiceBox<String> platformSelector;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Streamer Tracker");

        ChannelInfoService channelInfoService = new ChannelInfoService(ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube(), ApiInitializer.YoutubeAuthToken);
        guiStreamerInfo = new GUIStreamerInfo(channelInfoService);

        usernameInput = new TextField();
        usernameInput.setPromptText("Enter Streamer Username...");

        resultLabel = new Label();
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        platformSelector = new ChoiceBox<>();
        platformSelector.getItems().addAll("Twitch", "YouTube");
        platformSelector.setValue("Twitch");

        Button searchButton = new Button("Search");
        Button fetchInfoButton = new Button("Fetch Info");

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
                outputArea.setText("Displaying details for " + result);
            } else {
                resultLabel.setText("Streamer not found.");
                outputArea.clear();
            }
        });

        fetchInfoButton.setOnAction(e -> {
            String username = guiSearchHandler.getLastValidUsername();
            String platform = platformSelector.getValue();

            if (username == null || username.isEmpty()) {
                showError("No Streamer Selected", "Please search for a valid streamer first.");
                return;
            }

            String details = guiStreamerInfo.fetchStreamerDetails(username, platform);

            if (details == null || details.isEmpty()) {
                showError("Streamer Info Not Found", "Could not retrieve details for this user.");
                return;
            }

            resultLabel.setText("Streamer Info Retrieved");
            outputArea.setText(details);
        });

        StreamerSearchService searchService = new StreamerSearchService(
                ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube(), ApiInitializer.TwitchAuthToken, ApiInitializer.YoutubeAuthToken
        );

        guiSearchHandler = new GUISearchHandler(searchService);

        HBox buttonBox = new HBox(10, platformSelector, searchButton, fetchInfoButton);
        VBox layout = new VBox(10, usernameInput, buttonBox, resultLabel, outputArea);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 400, 300);
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
