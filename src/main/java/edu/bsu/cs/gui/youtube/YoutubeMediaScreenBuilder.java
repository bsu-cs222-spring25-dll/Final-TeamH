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

    private Stage stage;
    private Pane previousRoot;

    public void display(Stage stage, List<String> youtubeVideoData, Pane previousRoot) {
        this.stage = stage;
        this.previousRoot = previousRoot;

        VBox videoList = createVideoList(youtubeVideoData);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.getScene().setRoot(previousRoot));

        VBox wrapper = new VBox(20, backButton, videoList);
        wrapper.setPadding(new Insets(20));

        ScrollPane scrollPane = createScrollLayout(wrapper);
        stage.getScene().setRoot(new BorderPane(scrollPane));
    }

    private VBox createVideoList(List<String> data) {
        VBox list = new VBox(20);
        list.setPadding(new Insets(20));
        for (String entry : data) {
            String[] parts = entry.split("__");
            if (parts.length < 3) continue;

            YoutubeVideoEntry videoEntry = new YoutubeVideoEntry(parts[0], parts[1], parts[2]);
            list.getChildren().add(createVideoRow(videoEntry, data));
        }
        return list;
    }

    private HBox createVideoRow(YoutubeVideoEntry entry, List<String> allVideos) {
        ImageView thumbnail = new ImageView(new Image(entry.thumbnailUrl, 120, 90, true, true));
        Text label = new Text(entry.title);
        label.setWrappingWidth(400);

        Button watchButton = new Button("Watch");
        watchButton.setOnAction(e -> displayVideo(entry.videoId, allVideos));

        VBox infoBox = new VBox(5, label, watchButton);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(15, thumbnail, infoBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private ScrollPane createScrollLayout(Pane content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void displayVideo(String videoId, List<String> allVideos) {
        WebView webView = new WebView();
        webView.setPrefSize(800, 450);
        webView.getEngine().load("https://www.youtube.com/embed/" + videoId + "?autoplay=1");

        Button backToList = new Button("Back to List");
        backToList.setOnAction(e -> {
            webView.getEngine().load(null);
            display(stage, allVideos, previousRoot);
        });

        VBox wrapper = new VBox(10, backToList, webView);
        wrapper.setPadding(new Insets(20));

        stage.getScene().setRoot(new BorderPane(wrapper));
    }
}