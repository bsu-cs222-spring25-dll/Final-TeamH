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

public class YoutubeModeScreenBuilder {

    public BorderPane build(ApiContext context, Stage stage) {
        Label titleLabel = new Label("YouTube Access");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: #ff0000; -fx-font-weight: bold;");

        Button searchButton = createImageButton("/images/youtubeSearch.png", 220, 180);
        Button categoryButton = createImageButton("/images/youtubeCategories.png", 220, 180);

        YoutubeModeScreenController controller = new YoutubeModeScreenController(context, stage);
        searchButton.setOnAction(e -> controller.handleSearchClick());
        categoryButton.setOnAction(e -> controller.handleCategoryClick());

        VBox searchInfo = new VBox(12,
                styledLabel("Search YouTube Channels!", 20, true),
                styledLabel("- Get recent uploads", 16, false),
                styledLabel("- Get scheduled streams", 16, false)
        );
        searchInfo.setAlignment(Pos.TOP_LEFT);

        VBox categoryInfo = new VBox(12,
                styledLabel("YouTube Categories!", 20, true),
                styledLabel("- Explore trending content", 16, false),
                styledLabel("- Discover live events", 16, false)
        );
        categoryInfo.setAlignment(Pos.TOP_RIGHT);

        HBox searchSection = new HBox(20, searchButton, searchInfo);
        searchSection.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(searchSection, Pos.TOP_LEFT);
        StackPane.setMargin(searchSection, new Insets(80, 0, 0, 100));

        HBox categorySection = new HBox(20, categoryInfo, categoryButton);
        categorySection.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(categorySection, Pos.TOP_RIGHT);
        StackPane.setMargin(categorySection, new Insets(300, 100, 0, 0));

        StackPane stack = new StackPane(searchSection, categorySection);

        VBox pageLayout = new VBox(30, titleLabel, stack);
        pageLayout.setAlignment(Pos.TOP_CENTER);
        pageLayout.setPadding(new Insets(40, 0, 0, 0));

        BorderPane layout = new BorderPane();
        layout.setCenter(pageLayout);
        return layout;
    }

    private Button createImageButton(String imagePath, int width, int height) {
        ImageView imageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm(), width, height, false, true));
        imageView.setPreserveRatio(false);

        Button button = new Button();
        button.setPrefSize(width, height);
        button.setGraphic(imageView);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #ff0000;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0;"
        );
        return button;
    }

    private Label styledLabel(String text, int size, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + size + "px;" +
                (bold ? " -fx-font-weight: bold;" : "") +
                " -fx-text-fill: black;");
        return label;
    }
}