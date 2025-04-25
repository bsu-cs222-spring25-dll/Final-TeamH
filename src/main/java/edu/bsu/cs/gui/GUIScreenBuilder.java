package edu.bsu.cs.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class GUIScreenBuilder {

    public BorderPane buildMainScreen(GUIScreenController controller) {
        ImageView logoImageView = createLogoView();
        HBox buttonRow = createPlatformButtons(controller);
        Label instructionLabel = createInstructionLabel();

        VBox centerContent = new VBox(30, logoImageView, buttonRow, instructionLabel);
        centerContent.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setCenter(centerContent);
        return layout;
    }

    private ImageView createLogoView() {
        ImageView logoImageView = new ImageView(new Image(getClass().getResource("/images/projectLogo.png").toExternalForm()));
        logoImageView.setFitWidth(150);
        logoImageView.setPreserveRatio(true);
        logoImageView.setScaleX(7.0);
        logoImageView.setScaleY(7.0);
        return logoImageView;
    }

    private HBox createPlatformButtons(GUIScreenController controller) {
        Button twitchButton = createPlatformButton("/images/twitchLogo.jpg", controller::handleTwitchClick);
        Button youtubeButton = createPlatformButton("/images/youtubeLogo.png", controller::handleYouTubeClick);

        HBox buttonRow = new HBox(80, twitchButton, youtubeButton);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(40));
        return buttonRow;
    }

    private Button createPlatformButton(String imagePath, Runnable action) {
        ImageView imageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setMinSize(200, 200);
        button.setMaxSize(200, 200);
        button.setOnAction(e -> action.run());
        return button;
    }

    private Label createInstructionLabel() {
        Label instructionLabel = new Label("To begin, select one of the platforms on either side.");
        instructionLabel.setStyle("-fx-font-size: 16px;");
        instructionLabel.setAlignment(Pos.CENTER);
        return instructionLabel;
    }
}