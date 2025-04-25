
package edu.bsu.cs.gui.youtube;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
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

    public void display(Stage stage, List<YoutubeVideoEntry> entries, Pane previousRoot) {
        this.stage = stage;
        this.previousRoot = previousRoot;

        VBox videoList = createVideoList(entries);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.getScene().setRoot(previousRoot));

        VBox wrapper = new VBox(20, backButton, videoList);
        wrapper.setPadding(new Insets(20));

        ScrollPane scrollPane = createScrollLayout(wrapper);
        stage.getScene().setRoot(new BorderPane(scrollPane));
    }

    private VBox createVideoList(List<YoutubeVideoEntry> entries) {
        VBox list = new VBox(20);
        list.setPadding(new Insets(20));
        for (YoutubeVideoEntry entry : entries) {
            list.getChildren().add(createVideoRow(entry, entries));
        }
        return list;
    }

    private HBox createVideoRow(YoutubeVideoEntry entry, List<YoutubeVideoEntry> allEntries) {
        ImageView thumbnail = new ImageView(new Image(entry.thumbnailUrl, 120, 90, true, true));
        Text label = new Text(entry.title);
        label.setWrappingWidth(400);

        Button watchButton = new Button("Watch");
        watchButton.setOnAction(e -> displayVideo(entry.videoId, allEntries));

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

    private void displayVideo(String videoId, List<YoutubeVideoEntry> allEntries) {
        Node fallbackPlayer = createFallPlayer(videoId);
        WebView webView = new WebView();
        webView.setPrefSize(800, 450);
        webView.getEngine().load("https://www.youtube.com/embed/" + videoId + "?autoplay=1");

        Button backToList = new Button("Back to List");
        backToList.setOnAction(e -> {
            webView.getEngine().load(null);
            display(stage, allEntries, previousRoot);
        });


        VBox wrapper = new VBox(10, webView, fallbackPlayer, backToList);
        wrapper.setPadding(new Insets(20));
        wrapper.setAlignment(Pos.CENTER);

        stage.getScene().setRoot(new BorderPane(wrapper));
    }

    private Node createFallPlayer(String videoId) {
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
        Label fallbackLabel = new Label("If the video doesn't work, ");
        Hyperlink openLink = new Hyperlink("click here to watch it on YouTube.");

        openLink.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(videoUrl));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox fallbackBox = new HBox(5, fallbackLabel, openLink);
        fallbackBox.setAlignment(Pos.CENTER);
        fallbackBox.setPadding(new Insets(10, 0, 0, 0));
        return fallbackBox;
    }

}
