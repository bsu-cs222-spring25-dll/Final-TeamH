package edu.bsu.cs.gui.twitch;

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

public class TwitchMediaScreenBuilder {

    public void display(Stage stage, List<String> mediaData, Pane previousRoot, String type) {
        VBox mediaList = buildMediaList(mediaData, type);
        Button backButton = createBackButton(stage, previousRoot);
        VBox wrapper = buildWrapper(mediaList, backButton);

        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);

        BorderPane layout = new BorderPane(scrollPane);
        stage.getScene().setRoot(layout);
    }

    private VBox buildMediaList(List<String> mediaData, String type) {
        VBox list = new VBox(20);
        list.setPadding(new Insets(20));

        for (String entry : mediaData) {
            String[] parts = entry.split("__");
            if (parts.length < 3) continue;

            String title = parts[0];
            String id = parts[1];
            String thumbnailUrl = parts[2];

            HBox row = createMediaRow(title, id, thumbnailUrl, type);
            list.getChildren().add(row);
        }
        return list;
    }

    private HBox createMediaRow(String title, String id, String thumbnailUrl, String type) {
        ImageView thumbnail = new ImageView(new Image(thumbnailUrl, 120, 90, true, true));

        Text label = new Text(title);
        label.setWrappingWidth(400);

        Button watchButton = new Button("Watch");
        watchButton.setOnAction(_ -> openInBrowser(type, id));

        VBox infoBox = new VBox(5, label, watchButton);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(15, thumbnail, infoBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildWrapper(VBox mediaList, Button backButton) {
        VBox wrapper = new VBox(20, backButton, mediaList);
        wrapper.setPadding(new Insets(20));
        return wrapper;
    }

    private Button createBackButton(Stage stage, Pane previousRoot) {
        Button backButton = new Button("Back");
        backButton.setOnAction(_ -> stage.getScene().setRoot(previousRoot));
        return backButton;
    }

    private void openInBrowser(String type, String id) {
        String url = switch (type) {
            case "clip" -> "https://clips.twitch.tv/" + id;
            case "stream", "scheduled" -> "https://www.twitch.tv/videos/" + id;
            default -> "https://www.twitch.tv";
        };

        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception ignored) {}
    }
}