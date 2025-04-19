package edu.bsu.cs.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class TwitchStreamsScreenBuilder {

    public void display(Stage stage, List<String> twitchStreamData, Pane previousRoot) {
        VBox streamList = new VBox(20);
        streamList.setPadding(new Insets(20));

        for (String stream : twitchStreamData) {
            String[] parts = stream.split("__");
            if (parts.length < 3) continue;

            String title = parts[0];
            String videoId = parts[1];
            String thumbnailUrl = parts[2];

            ImageView thumbnail = new ImageView(new Image(thumbnailUrl, 120, 90, true, true));
            Text streamTitle = new Text(title);
            streamTitle.setWrappingWidth(400);

            Button watchButton = new Button("Watch");
            watchButton.setOnAction(e -> openStreamInBrowser(videoId));

            VBox infoBox = new VBox(5, streamTitle, watchButton);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            HBox row = new HBox(15, thumbnail, infoBox);
            row.setAlignment(Pos.CENTER_LEFT);

            streamList.getChildren().add(row);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.getScene().setRoot(previousRoot));

        VBox layoutWrapper = new VBox(20, backButton, streamList);
        layoutWrapper.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(layoutWrapper);
        scrollPane.setFitToWidth(true);

        BorderPane layout = new BorderPane(scrollPane);
        stage.getScene().setRoot(layout);
    }

    private void openStreamInBrowser(String videoId) {
        String url = "https://www.twitch.tv/videos/" + videoId;
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception ignored) {}
    }
}