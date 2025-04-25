package edu.bsu.cs.gui.twitch;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.gui.GUIScreenBuilder;
import edu.bsu.cs.gui.GUIScreenController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class TwitchTopCategoriesScreenBuilder {
    public BorderPane buildTopCategoriesScreen(ApiContext context, Stage stage) {
        TwitchTopCategoriesScreenController controller = new TwitchTopCategoriesScreenController(context, stage);

        Button homeButton = createHomeButton(stage);
        Button backButton = createBackButton(stage, context);
        HBox rightButtons = new HBox(10, backButton, homeButton);
        rightButtons.setAlignment(Pos.TOP_RIGHT);
        rightButtons.setPadding(new Insets(5, 10, 5, 10));

        BorderPane topBar = new BorderPane();
        topBar.setRight(rightButtons);
        Label topCategoriesLabel = createTitle("Top 10 Live Categories:");
        topCategoriesLabel.setPadding(new Insets(0, 0, 10, 0));

        HBox categoryButtons = createCategoryButtons(controller);
        Label topStreamsLabel = createTitle("Top 10 Currently Live Streams:");

        VBox buttonGrid = createStreamButtons(new TwitchTopStreamsForCategoryController(context,stage), null);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setPadding(new Insets(10, 0, 0, 0));

        VBox centerContent = new VBox(10, topCategoriesLabel, categoryButtons, topStreamsLabel, buttonGrid);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(scrollPane);

        return layout;
    }


    private HBox createCategoryButtons(TwitchTopCategoriesScreenController controller) {
        VBox buttonGrid = new VBox(10);
        buttonGrid.setAlignment(Pos.CENTER);
        int categoryNumber = 0;

        for (int row = 0; row < 2; row++) {
            HBox rowBox = new HBox(10);
            rowBox.setAlignment(Pos.CENTER);

            for (int column = 0; column < 5; column++) {
                String imageUrl = controller.getTopCategoryURLs(categoryNumber);
                ImageView imageView;

                try {
                    imageView = new ImageView(new Image(imageUrl, 142, 189, false, true));
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid category image URL: " + imageUrl);
                    imageView = new ImageView();
                }

                imageView.setFitWidth(142);
                imageView.setFitHeight(189);
                imageView.setPreserveRatio(true);

                Label label = new Label(controller.getTopCategoryNames(categoryNumber));
                label.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");
                label.setAlignment(Pos.CENTER);
                label.setMaxWidth(142);

                VBox content = new VBox(5, imageView, label);
                content.setAlignment(Pos.CENTER);

                Button btn = new Button();
                btn.setGraphic(content);
                btn.setMinSize(100, 100);
                btn.setStyle("-fx-content-display: graphic-only;");

                int finalCategoryNumber = categoryNumber;
                btn.setOnAction(e -> controller.handleCategoryClick(finalCategoryNumber));

                rowBox.getChildren().add(btn);
                categoryNumber++;
            }

            buttonGrid.getChildren().add(rowBox);
        }

        HBox wrapper = new HBox(buttonGrid);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    private VBox createStreamButtons(TwitchTopStreamsForCategoryController controller, String topCategoryID){
        List<String> topStreamsInfo = controller.getTopStreamsForCategoryInfo(topCategoryID);
        VBox buttonGrid = new VBox(15);
        buttonGrid.setAlignment(Pos.CENTER);
        int streamNumber=0;
        for (int row = 0; row < 2; row++) {
            HBox rowBox = new HBox(20);
            rowBox.setAlignment(Pos.CENTER);

            for (int col = 0; col < 5; col++) {
                ImageView thumbnail = new ImageView(controller.getTopStreamThumbnailURL(streamNumber, topStreamsInfo));
                thumbnail.setFitWidth(170);
                thumbnail.setFitHeight(96);
                thumbnail.setPreserveRatio(true);

                Label streamLabel = new Label(controller.getTopStreamTitle(streamNumber,topStreamsInfo));
                streamLabel.setFont(Font.font(12));
                streamLabel.setAlignment(Pos.CENTER);
                streamLabel.setMaxWidth(170);

                Label streamerNameLabel = new Label(controller.getTopStreamerUsername(streamNumber,topStreamsInfo));
                streamerNameLabel.setFont(Font.font(12));
                streamerNameLabel.setAlignment(Pos.CENTER);
                streamerNameLabel.setMaxWidth(170);

                VBox buttonContent = new VBox(thumbnail, streamLabel, streamerNameLabel);
                buttonContent.setAlignment(Pos.TOP_CENTER);

                Button playStreamButton = new Button();
                playStreamButton.setGraphic(buttonContent);
                playStreamButton.setMinSize(100, 100);

                int finalCategoryNumber = streamNumber;
                playStreamButton.setOnAction(e -> controller.handlePlayButtonClick(finalCategoryNumber,topStreamsInfo));

                rowBox.getChildren().add(playStreamButton);
                streamNumber++;
            }
            buttonGrid.getChildren().add(rowBox);
            streamNumber=5;
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
            TwitchModeSelectionScreenBuilder modeBuilder = new TwitchModeSelectionScreenBuilder();
            stage.getScene().setRoot(modeBuilder.build(context, stage));
        });
        return backButton;
    }

    private Label createTitle(String text) {
        Label title = new Label(text);
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        return title;
    }
}