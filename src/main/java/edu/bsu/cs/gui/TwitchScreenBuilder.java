package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TwitchScreenBuilder {

    public BorderPane buildTwitchScreen(ApiContext context, Stage stage) {
        Label titleLabel = createTitleLabel();
        TextField searchField = createSearchField();
        Label resultLabel = createResultLabel();
        ImageView profileImageView = createProfileImageView();
        Label liveStatusLabel = createLiveStatusLabel();
        TextArea bioTextArea = createBioTextArea();
        ScrollPane bioScrollPane = wrapBioInScrollPane(bioTextArea);
        VBox profileLeftColumn = new VBox(profileImageView);
        profileLeftColumn.setAlignment(Pos.TOP_LEFT);

        HBox profileRow = new HBox(15, profileLeftColumn, bioScrollPane);
        profileRow.setAlignment(Pos.CENTER_LEFT);

        TwitchScreenController controller = new TwitchScreenController(
                context, resultLabel, profileImageView, bioTextArea, bioScrollPane, liveStatusLabel
        );

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> controller.handleSearch(searchField.getText()));

        HBox searchRow = new HBox(10, searchField, searchButton);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        VBox contentBox = new VBox(10, titleLabel, searchRow, resultLabel, profileRow, liveStatusLabel);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(0));

        Button homeButton = createHomeButton(stage, resultLabel, profileImageView, bioTextArea, bioScrollPane, liveStatusLabel);

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setSpacing(20);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(contentBox, spacer, homeButton);

        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        return layout;
    }

    private Label createTitleLabel() {
        Label titleLabel = new Label("Search Streamers!");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
        titleLabel.setTextFill(Color.web("#007bff"));
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        return titleLabel;
    }

    private TextField createSearchField() {
        TextField field = new TextField();
        field.setPromptText("Enter username...");
        field.setPrefWidth(250);
        return field;
    }

    private Label createResultLabel() {
        Label label = new Label("Current Streamer:");
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

    private TextArea createBioTextArea() {
        TextArea area = new TextArea();
        area.setWrapText(true);
        area.setEditable(false);
        area.setPrefHeight(100);
        area.setPrefWidth(300);
        return area;
    }

    private ScrollPane wrapBioInScrollPane(TextArea area) {
        ScrollPane scrollPane = new ScrollPane(area);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(100);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVisible(false);
        return scrollPane;
    }

    private Button createHomeButton(Stage stage, Label resultLabel, ImageView profileImageView, TextArea bioTextArea, ScrollPane bioScrollPane, Label liveStatusLabel) {
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {
            resultLabel.setVisible(false);
            bioScrollPane.setVisible(false);
            liveStatusLabel.setVisible(false);
            profileImageView.setImage(null);
            bioTextArea.clear();
            GUIScreenBuilder guiBuilder = new GUIScreenBuilder();
            GUIScreenController guiController = new GUIScreenController(stage);
            stage.getScene().setRoot(guiBuilder.buildMainScreen(guiController));
        });
        return homeButton;
    }
}