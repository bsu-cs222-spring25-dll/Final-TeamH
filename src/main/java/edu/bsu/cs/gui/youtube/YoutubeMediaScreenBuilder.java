package edu.bsu.cs.gui.youtube;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;

public class YoutubeMediaScreenBuilder {

    public void display(Stage stage, List<String> youtubeVideoData, Pane previousRoot) {
        VBox videoList = new VBox(20);
        videoList.setPadding(new Insets(20));

        for (String entry : youtubeVideoData) {
            String[] parts = entry.split("__");
            if (parts.length < 3) continue;

            String title = parts[0];
            String videoId = parts[1];
            String thumbnailUrl = parts[2];

            ImageView thumbnail = new ImageView(new Image(thumbnailUrl, 120, 90, true, true));
            Text label = new Text(title);
            label.setWrappingWidth(400);

            Button watchButton = new Button("Watch");
            watchButton.setOnAction(e -> displayVideo(stage, videoId, youtubeVideoData, previousRoot));

            VBox infoBox = new VBox(5, label, watchButton);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            HBox row = new HBox(15, thumbnail, infoBox);
            row.setAlignment(Pos.CENTER_LEFT);

            videoList.getChildren().add(row);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.getScene().setRoot(previousRoot));

        VBox wrapper = new VBox(20, backButton, videoList);
        wrapper.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);

        BorderPane layout = new BorderPane(scrollPane);
        stage.getScene().setRoot(layout);
    }

    private void displayVideo(Stage stage, String videoId, List<String> videoListData, Pane previousRoot) {
        WebView webView = new WebView();
        webView.setPrefSize(800, 450);
        webView.getEngine().load("https://www.youtube.com/embed/" + videoId + "?autoplay=1");

        Button backToList = new Button("Back to List");
        backToList.setOnAction(e -> {
            webView.getEngine().load(null);
            display(stage, videoListData, previousRoot);
        });

        VBox wrapper = new VBox(10, backToList, webView);
        wrapper.setPadding(new Insets(20));

        BorderPane layout = new BorderPane(wrapper);
        stage.getScene().setRoot(layout);
    }

}