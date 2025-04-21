package edu.bsu.cs.gui.twitch;

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

public class TwitchScreenBuilder {

    public BorderPane buildTwitchScreen(ApiContext context, Stage stage) {
        Label titleLabel = createTitleLabel();
        TextField searchField = createSearchField();
        Button searchButton = new Button("Search");

        Label resultLabel = createResultLabel();
        ImageView profileImageView = createProfileImageView();
        Label liveStatusLabel = createLiveStatusLabel();
        TextArea bioTextArea = createBioTextArea();
        ScrollPane bioScrollPane = wrapBioInScrollPane(bioTextArea);

        Button getStreamsButton = createGetStreamsButton();
        Button getClipsButton = createGetClipsButton();
        Button getScheduledButton = createGetScheduledButton();

        HBox searchRow = buildSearchRow(searchField, searchButton);
        VBox profileRow = buildProfileRow(profileImageView, bioScrollPane);
        VBox contentBox = buildContentBox(titleLabel, searchRow, resultLabel, profileRow,
                liveStatusLabel, getStreamsButton, getClipsButton, getScheduledButton);

        BorderPane layout = new BorderPane();

        TwitchViewModel model = new TwitchViewModel(
                resultLabel, profileImageView, bioTextArea, bioScrollPane, liveStatusLabel,
                getStreamsButton, getClipsButton, getScheduledButton, layout
        );

        Button homeButton = createHomeButton(stage, model);
        Button backButton = createBackButton(stage, context, model);
        HBox topBar = buildTopBar(contentBox, backButton, homeButton);

        layout.setTop(topBar);

        TwitchScreenController controller = new TwitchScreenController(context, model, stage);
        searchButton.setOnAction(e -> controller.handleSearch(searchField.getText()));

        return layout;
    }

    private Label createTitleLabel() {
        Label label = new Label("Search Streamers!");
        label.setFont(Font.font("System", FontWeight.BOLD, 30));
        label.setTextFill(Color.web("#007bff"));
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
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

    private Button createGetStreamsButton() {
        Button button = new Button("Get Streams");
        button.setVisible(false);
        return button;
    }

    private Button createGetClipsButton() {
        Button button = new Button("Get Clips");
        button.setVisible(false);
        return button;
    }

    private Button createGetScheduledButton() {
        Button button = new Button("Get Scheduled");
        button.setVisible(false);
        return button;
    }

    private HBox buildSearchRow(TextField searchField, Button searchButton) {
        HBox searchRow = new HBox(10, searchField, searchButton);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        return searchRow;
    }

    private VBox buildProfileRow(ImageView profileImageView, ScrollPane bioScrollPane) {
        VBox profileLeftColumn = new VBox(profileImageView);
        profileLeftColumn.setAlignment(Pos.TOP_LEFT);

        VBox row = new VBox(new HBox(15, profileLeftColumn, bioScrollPane));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildContentBox(Label titleLabel, HBox searchRow, Label resultLabel, VBox profileRow,
                                 Label liveStatusLabel, Button streams, Button clips, Button scheduled) {
        VBox contentBox = new VBox(10, titleLabel, searchRow, resultLabel, profileRow,
                liveStatusLabel, streams, clips, scheduled);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(0));
        return contentBox;
    }

    private HBox buildTopBar(VBox contentBox, Button backButton, Button homeButton) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setSpacing(20);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(contentBox, spacer, backButton, homeButton);
        return topBar;
    }

    private Button createHomeButton(Stage stage, TwitchViewModel model) {
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {
            model.resetView();
            GUIScreenBuilder guiBuilder = new GUIScreenBuilder();
            GUIScreenController guiController = new GUIScreenController(stage);
            stage.getScene().setRoot(guiBuilder.buildMainScreen(guiController));
        });
        return homeButton;
    }

    private Button createBackButton(Stage stage, ApiContext context, TwitchViewModel model) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            model.resetView();
            TwitchModeSelectionScreenBuilder modeBuilder = new TwitchModeSelectionScreenBuilder();
            stage.getScene().setRoot(modeBuilder.build(context, stage));
        });
        return backButton;
    }
}