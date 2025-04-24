package edu.bsu.cs.gui.twitch;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.gui.GUIScreenBuilder;
import edu.bsu.cs.gui.GUIScreenController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        Label title = createTitle();
        title.setPadding(new Insets(0, 0, 10, 0));

        HBox categoryButtons = createCategoryButtons(controller);

        VBox centerContent = new VBox(10, title, categoryButtons);
        centerContent.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(centerContent);

        return layout;
    }


    private HBox createCategoryButtons(TwitchTopCategoriesScreenController controller) {
        VBox buttonGrid = new VBox(10); // Holds two rows
        buttonGrid.setAlignment(Pos.CENTER);
        int categoryNumber = 0;
        for (int row = 0; row < 2; row++) {
            HBox rowBox = new HBox(10);
            rowBox.setAlignment(Pos.CENTER);

            for (int column = 0; column < 5; column++) {
                ImageView imageView = new ImageView(controller.getTopCategoryURLs(categoryNumber));
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

                btn.setOnAction(e -> controller.handleCategoryClick());

                rowBox.getChildren().add(btn);
                categoryNumber++;
            }

            buttonGrid.getChildren().add(rowBox);
        }

        HBox wrapper = new HBox(buttonGrid);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
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

    private Label createTitle() {
        Label title = new Label("Top 10 Live Categories:");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        return title;
    }
}