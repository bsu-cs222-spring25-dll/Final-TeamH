package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TwitchScreenBuilder {

    public BorderPane buildTwitchScreen(ApiContext context) {
        Label titleLabel = new Label("Search Streamers!");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
        titleLabel.setTextFill(Color.web("#007bff"));
        titleLabel.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter username...");
        searchField.setPrefWidth(250);

        Label resultLabel = new Label("Current Streamer:");
        resultLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
        resultLabel.setTextFill(Color.DARKSLATEGRAY);

        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(100);
        profileImageView.setFitHeight(100);
        profileImageView.setClip(new javafx.scene.shape.Circle(50, 50, 50));

        TwitchScreenController controller = new TwitchScreenController(context, resultLabel, profileImageView);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> controller.handleSearch(searchField.getText()));

        HBox searchRow = new HBox(10, searchField, searchButton);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        VBox topLeftPanel = new VBox(10, titleLabel, searchRow, resultLabel, profileImageView);
        topLeftPanel.setAlignment(Pos.TOP_LEFT);
        topLeftPanel.setPadding(new Insets(20));

        BorderPane layout = new BorderPane();
        layout.setTop(topLeftPanel);
        return layout;
    }
}
