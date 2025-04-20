package edu.bsu.cs.gui.youtube;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.gui.GUIScreenBuilder;
import edu.bsu.cs.gui.GUIScreenController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class YoutubeScreenBuilder {

    public BorderPane buildYoutubeScreen(ApiContext context, Stage stage) {
        Label titleLabel = createTitleLabel();
        TextField searchField = createSearchField();
        Button searchButton = new Button("Search");

        Label resultLabel = createResultLabel();
        ImageView profileImageView = createProfileImageView();
        Label liveStatusLabel = createLiveStatusLabel();
        TextArea descriptionArea = createDescriptionArea();
        ScrollPane descriptionScrollPane = wrapInScrollPane(descriptionArea);

        Button uploadsButton = createHiddenButton("Get Streams");
        Button scheduledButton = createHiddenButton("Get Uploads");

        HBox searchRow = new HBox(10, searchField, searchButton);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        VBox profileLeft = new VBox(profileImageView);
        profileLeft.setAlignment(Pos.TOP_LEFT);

        HBox profileRow = new HBox(15, profileLeft, descriptionScrollPane);
        profileRow.setAlignment(Pos.CENTER_LEFT);

        VBox contentBox = new VBox(10, titleLabel, searchRow, resultLabel, profileRow,
                liveStatusLabel, uploadsButton, scheduledButton);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(0));

        Button backButton = createBackButton(context, stage);
        Button homeButton = createHomeButton(stage);

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setSpacing(20);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(contentBox, spacer, backButton, homeButton);

        BorderPane layout = new BorderPane();
        layout.setTop(topBar);

        YoutubeViewModel model = new YoutubeViewModel(
                resultLabel, profileImageView, descriptionArea, descriptionScrollPane,
                liveStatusLabel, uploadsButton, scheduledButton, layout
        );

        YoutubeScreenController controller = new YoutubeScreenController(context, model, stage);
        searchButton.setOnAction(e -> {
            try {
                controller.handleSearch(searchField.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return layout;
    }

    private Label createTitleLabel() {
        Label label = new Label("Search YouTube Channels!");
        label.setFont(Font.font("System", FontWeight.BOLD, 30));
        label.setTextFill(Color.DARKRED);
        return label;
    }

    private TextField createSearchField() {
        TextField field = new TextField();
        field.setPromptText("Enter channel name...");
        field.setPrefWidth(250);
        return field;
    }

    private Label createResultLabel() {
        Label label = new Label("Current Channel:");
        label.setFont(Font.font("System", FontWeight.NORMAL, 16));
        label.setTextFill(Color.DARKSLATEGRAY);
        label.setVisible(false);
        return label;
    }

    private ImageView createProfileImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setClip(new javafx.scene.shape.Circle(50, 50, 50));
        return imageView;
    }

    private Label createLiveStatusLabel() {
        Label label = new Label();
        label.setFont(Font.font("System", FontWeight.NORMAL, 14));
        label.setTextFill(Color.BLACK);
        label.setVisible(false);
        return label;
    }

    private TextArea createDescriptionArea() {
        TextArea area = new TextArea();
        area.setWrapText(true);
        area.setEditable(false);
        area.setPrefHeight(100);
        area.setPrefWidth(300);
        return area;
    }

    private ScrollPane wrapInScrollPane(TextArea area) {
        ScrollPane scrollPane = new ScrollPane(area);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(100);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVisible(false);
        return scrollPane;
    }

    private Button createHiddenButton(String text) {
        Button button = new Button(text);
        button.setVisible(false);
        return button;
    }

    private Button createHomeButton(Stage stage) {
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {
            GUIScreenBuilder builder = new GUIScreenBuilder();
            GUIScreenController controller = new GUIScreenController(stage);
            stage.getScene().setRoot(builder.buildMainScreen(controller));
        });
        return homeButton;
    }

    private Button createBackButton(ApiContext context, Stage stage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            YoutubeModeScreenBuilder modeBuilder = new YoutubeModeScreenBuilder();
            stage.getScene().setRoot(modeBuilder.build(context, stage));
        });
        return backButton;
    }
}