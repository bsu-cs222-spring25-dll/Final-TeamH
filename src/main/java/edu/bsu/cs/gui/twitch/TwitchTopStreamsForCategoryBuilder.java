package edu.bsu.cs.gui.twitch;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.gui.GUIScreenBuilder;
import edu.bsu.cs.gui.GUIScreenController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class TwitchTopStreamsForCategoryBuilder {

    public Parent buildTopStreamsForCategoryScreen(ApiContext context, Stage stage, String topCategoryName, String topCategoryID, String topCategoryURL) {
        TwitchTopStreamsForCategoryController controller = new TwitchTopStreamsForCategoryController(context, stage);
        Button homeButton = createHomeButton(stage);
        Button backButton = createBackButton(stage, context);
        HBox rightButtons = new HBox(10, backButton, homeButton);
        rightButtons.setAlignment(Pos.TOP_RIGHT);
        rightButtons.setPadding(new Insets(5, 10, 5, 10));

        ImageView categoryBoxImage = new ImageView(new Image(topCategoryURL));
        categoryBoxImage.setFitWidth(180);
        categoryBoxImage.setFitHeight(240);
        categoryBoxImage.setPreserveRatio(false);

        Label titleLabel = new Label("Top Streams for "+ topCategoryName + ":");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setPadding(new Insets(10, 0, 0, 0));

        VBox leftContent = new VBox(categoryBoxImage, titleLabel);
        leftContent.setAlignment(Pos.TOP_LEFT);
        leftContent.setPadding(new Insets(10));
        leftContent.setSpacing(5);

        BorderPane topBar = new BorderPane();
        topBar.setLeft(leftContent);
        topBar.setRight(rightButtons);

        VBox centerContent = createStreamButtons(controller, topCategoryID);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(10, 0, 0, 0));

        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(centerContent);

        return layout;
    }

    private VBox createStreamButtons(TwitchTopStreamsForCategoryController controller, String topCategoryID) {
        List<String> topStreamsInfo = controller.getTopStreamsForCategoryInfo(topCategoryID);
        VBox buttonGrid = new VBox(15);
        buttonGrid.setAlignment(Pos.CENTER);
        int streamNumber = 0;

        for (int row = 0; row < 2; row++) {
            HBox rowBox = new HBox(20);
            rowBox.setAlignment(Pos.CENTER);

            for (int col = 0; col < 5; col++) {
                String imageUrl = controller.getTopStreamThumbnailURL(streamNumber, topStreamsInfo);
                ImageView thumbnail;

                try {
                    thumbnail = new ImageView(new Image(imageUrl, 170, 96, true, true));
                } catch (IllegalArgumentException ex) {
                    System.err.println("Invalid stream thumbnail URL: " + imageUrl);
                    thumbnail = new ImageView();
                    thumbnail.setFitWidth(170);
                    thumbnail.setFitHeight(96);
                }

                thumbnail.setFitWidth(170);
                thumbnail.setFitHeight(96);
                thumbnail.setPreserveRatio(true);

                Label streamLabel = new Label(controller.getTopStreamTitle(streamNumber, topStreamsInfo));
                streamLabel.setFont(Font.font(12));
                streamLabel.setAlignment(Pos.CENTER);
                streamLabel.setMaxWidth(170);

                Label streamerNameLabel = new Label(controller.getTopStreamerUsername(streamNumber, topStreamsInfo));
                streamerNameLabel.setFont(Font.font(12));
                streamerNameLabel.setAlignment(Pos.CENTER);
                streamerNameLabel.setMaxWidth(170);

                VBox buttonContent = new VBox(thumbnail, streamLabel, streamerNameLabel);
                buttonContent.setAlignment(Pos.TOP_CENTER);

                Button playStreamButton = new Button();
                playStreamButton.setGraphic(buttonContent);
                playStreamButton.setMinSize(100, 100);

                int finalCategoryNumber = streamNumber;
                playStreamButton.setOnAction(e -> controller.handlePlayButtonClick(finalCategoryNumber, topStreamsInfo));

                rowBox.getChildren().add(playStreamButton);
                streamNumber++;
            }

            buttonGrid.getChildren().add(rowBox);
            streamNumber = 5;
        }

        return buttonGrid;
    }

    private Button createHomeButton(Stage stage) {
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {
            GUIScreenBuilder guiBuilder = new GUIScreenBuilder();
            GUIScreenController guiController = new GUIScreenController(stage);
            stage.getScene().setRoot(guiBuilder.buildMainScreen(guiController));
        });
        return homeButton;
    }

    private Button createBackButton(Stage stage, ApiContext context) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            TwitchTopCategoriesScreenBuilder builder = new TwitchTopCategoriesScreenBuilder();
            stage.getScene().setRoot(builder.buildTopCategoriesScreen(context, stage));
        });
        return backButton;
    }
}