package edu.bsu.cs.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GUIScreenBuilder {

    public BorderPane buildMainScreen(GUIScreenController controller) {
        ImageView logoImageView = new ImageView(new Image(getClass().getResource("/images/projectLogo.png").toExternalForm()));
        logoImageView.setFitWidth(150);
        logoImageView.setPreserveRatio(true);
        logoImageView.setScaleX(7.0);
        logoImageView.setScaleY(7.0);

        ImageView twitchImageView = new ImageView(new Image(getClass().getResource("/images/twitchLogo.jpg").toExternalForm()));
        twitchImageView.setFitWidth(150);
        twitchImageView.setFitHeight(150);
        twitchImageView.setPreserveRatio(true);

        Button twitchButton = new Button();
        twitchButton.setGraphic(twitchImageView);
        twitchButton.setMinSize(200, 200);
        twitchButton.setMaxSize(200, 200);
        twitchButton.setOnAction(e -> controller.handleTwitchClick());

        ImageView youtubeImageView = new ImageView(new Image(getClass().getResource("/images/youtubeLogo.png").toExternalForm()));
        youtubeImageView.setFitWidth(150);
        youtubeImageView.setFitHeight(150);
        youtubeImageView.setPreserveRatio(true);

        Button youtubeButton = new Button();
        youtubeButton.setGraphic(youtubeImageView);
        youtubeButton.setMinSize(200, 200);
        youtubeButton.setMaxSize(200, 200);
        youtubeButton.setOnAction(e -> controller.handleYouTubeClick());

        HBox buttonRow = new HBox(80, twitchButton, youtubeButton);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(40));

        Label instructionLabel = new Label("To begin, select one of the platforms on either side.");
        instructionLabel.setStyle("-fx-font-size: 16px;");
        instructionLabel.setAlignment(Pos.CENTER);

        VBox centerContent = new VBox(30, logoImageView, buttonRow, instructionLabel);
        centerContent.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setCenter(centerContent);
        return layout;
    }
}
