package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TwitchModeSelectionScreenBuilder {

    public BorderPane build(ApiContext context, Stage stage) {
        Label titleLabel = createTitleLabel();
        HBox searchSection = buildSearchSection(context, stage);
        HBox categorySection = buildCategorySection();

        StackPane stack = new StackPane(searchSection, categorySection);
        VBox pageLayout = buildPageLayout(titleLabel, stack);

        BorderPane layout = new BorderPane();
        layout.setCenter(pageLayout);
        return layout;
    }

    private Label createTitleLabel() {
        Label titleLabel = new Label("Twitch Access");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: #9147ff; -fx-font-weight: bold;");
        return titleLabel;
    }

    private HBox buildSearchSection(ApiContext context, Stage stage) {
        Button searchButton = createImageButton("/images/twitchSearch.png", 220, 180);
        searchButton.setOnAction(e -> {
            TwitchScreenBuilder searchBuilder = new TwitchScreenBuilder();
            stage.getScene().setRoot(searchBuilder.buildTwitchScreen(context, stage));
        });

        VBox info = buildSearchInfoText();

        HBox section = new HBox(20, searchButton, info);
        section.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(section, Pos.TOP_LEFT);
        StackPane.setMargin(section, new Insets(80, 0, 0, 100));
        return section;
    }

    private HBox buildCategorySection() {
        Button categoryButton = createImageButton("/images/twitchCategories.png", 220, 180);
        categoryButton.setOnAction(e -> showPlaceholderAlert());

        VBox info = buildCategoryInfoText();

        HBox section = new HBox(20, info, categoryButton);
        section.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(section, Pos.TOP_RIGHT);
        StackPane.setMargin(section, new Insets(300, 100, 0, 0));
        return section;
    }

    private VBox buildSearchInfoText() {
        VBox info = new VBox(12);
        info.setAlignment(Pos.TOP_LEFT);
        info.getChildren().addAll(
                styledLabel("Search for Twitch Streamers!", 20, true),
                styledLabel("- Get Twitch Streams", 16, false),
                styledLabel("- Get Twitch Clips", 16, false),
                styledLabel("- Get Scheduled Streams", 16, false)
        );
        return info;
    }

    private VBox buildCategoryInfoText() {
        VBox info = new VBox(12);
        info.setAlignment(Pos.TOP_RIGHT);
        info.getChildren().addAll(
                styledLabel("Twitch Categories!", 20, true),
                styledLabel("- Access top streams from different categories", 16, false),
                styledLabel("- Access random streams", 16, false)
        );
        return info;
    }

    private Label styledLabel(String text, int fontSize, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px;" +
                (bold ? " -fx-font-weight: bold;" : "") +
                " -fx-text-fill: black;");
        return label;
    }

    private VBox buildPageLayout(Label titleLabel, StackPane stack) {
        VBox layout = new VBox(30, titleLabel, stack);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40, 0, 0, 0));
        return layout;
    }

    private Button createImageButton(String imagePath, int width, int height) {
        ImageView imageView = new ImageView(
                new Image(getClass().getResource(imagePath).toExternalForm(), width, height, false, true)
        );
        imageView.setPreserveRatio(false);

        Button button = new Button();
        button.setPrefSize(width, height);
        button.setGraphic(imageView);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #9147ff;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0;"
        );
        return button;
    }

    private void showPlaceholderAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText("This feature is currently in development.");
        alert.showAndWait();
    }
}