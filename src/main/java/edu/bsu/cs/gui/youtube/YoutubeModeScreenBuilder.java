package edu.bsu.cs.gui.youtube;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.gui.GUIScreenBuilder;
import edu.bsu.cs.gui.GUIScreenController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class YoutubeModeScreenBuilder {

    private Stage stage;
    private ApiContext context;

    public BorderPane build(ApiContext context, Stage stage) {
        this.context = context;
        this.stage = stage;

        BorderPane layout = new BorderPane();
        layout.setTop(createTopBar());
        layout.setCenter(createMainContent());
        return layout;
    }

    private HBox createTopBar() {
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            GUIScreenBuilder builder = new GUIScreenBuilder();
            GUIScreenController controller = new GUIScreenController(stage);
            stage.getScene().setRoot(builder.buildMainScreen(controller));
        });

        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(20, 20, 0, 0));
        return topBar;
    }

    private VBox createMainContent() {
        Label titleLabel = createStyledLabel("YouTube Access", 32, true, "#ff0000");

        HBox searchSection = createSearchSection();
        HBox categorySection = createCategorySection();

        StackPane stackedSections = new StackPane(searchSection, categorySection);

        VBox pageLayout = new VBox(30, titleLabel, stackedSections);
        pageLayout.setAlignment(Pos.TOP_CENTER);
        pageLayout.setPadding(new Insets(40, 0, 0, 0));
        return pageLayout;
    }

    private HBox createSearchSection() {
        Button searchButton = createImageButton("/images/youtubeSearch.png", 220, 180);
        searchButton.setOnAction(e -> new YoutubeModeScreenController(context, stage).handleSearchClick());

        VBox info = new VBox(12,
                createStyledLabel("Search YouTube Channels!", 20, true),
                createStyledLabel("- Get recent streams", 16, false),
                createStyledLabel("- Get best uploads", 16, false),
                createStyledLabel("- Get scheduled streams", 16, false)
        );
        info.setAlignment(Pos.TOP_LEFT);

        HBox section = new HBox(20, searchButton, info);
        section.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(section, Pos.TOP_LEFT);
        StackPane.setMargin(section, new Insets(80, 0, 0, 100));
        return section;
    }

    private HBox createCategorySection() {
        Button categoryButton = createImageButton("/images/youtubeCategories.png", 220, 180);
        categoryButton.setOnAction(e -> {
            try {
                new YoutubeModeScreenController(context, stage).handleCategoryClick();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox info = new VBox(12,
                createStyledLabel("YouTube Categories!", 20, true),
                createStyledLabel("- Explore trending content", 16, false),
                createStyledLabel("- Discover Random Streams", 16, false)
        );
        info.setAlignment(Pos.TOP_RIGHT);

        HBox section = new HBox(20, info, categoryButton);
        section.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(section, Pos.TOP_RIGHT);
        StackPane.setMargin(section, new Insets(300, 100, 0, 0));
        return section;
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

    private Label createStyledLabel(String text, int size, boolean bold) {
        return createStyledLabel(text, size, bold, "black");
    }

    private Label createStyledLabel(String text, int size, boolean bold, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + size + "px;" +
                (bold ? " -fx-font-weight: bold;" : "") +
                " -fx-text-fill: " + color + ";");
        return label;
    }
}