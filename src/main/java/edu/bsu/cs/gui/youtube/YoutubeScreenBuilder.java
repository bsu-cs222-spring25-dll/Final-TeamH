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

    private ApiContext context;
    private Stage stage;
    private TextField searchField;
    private Button searchButton;
    private Label resultLabel;
    private ImageView profileImageView;
    private Label liveStatusLabel;
    private TextArea descriptionArea;
    private ScrollPane descriptionScrollPane;
    private Button streamsButton;
    private Button uploadsButton;
    private Button scheduledButton;

    public BorderPane buildYoutubeScreen(ApiContext context, Stage stage) {
        this.context = context;
        this.stage = stage;

        Label titleLabel = createTitleLabel();
        HBox searchRow = buildSearchRow();
        HBox profileRow = buildProfileRow();
        VBox contentBox = buildContentBox(titleLabel, searchRow, profileRow);

        HBox topBar = buildTopBar(contentBox);
        BorderPane layout = new BorderPane();
        layout.setTop(topBar);

        YoutubeViewModel model = new YoutubeViewModel(
                resultLabel, profileImageView, descriptionArea, descriptionScrollPane,
                liveStatusLabel, streamsButton, uploadsButton, scheduledButton, layout
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

    private HBox buildSearchRow() {
        searchField = new TextField();
        searchField.setPromptText("Enter channel name...");
        searchField.setPrefWidth(250);

        searchButton = new Button("Search");

        HBox row = new HBox(10, searchField, searchButton);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildProfileRow() {
        profileImageView = new ImageView();
        profileImageView.setFitWidth(100);
        profileImageView.setFitHeight(100);
        profileImageView.setClip(new javafx.scene.shape.Circle(50, 50, 50));

        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setPrefHeight(100);
        descriptionArea.setPrefWidth(300);

        descriptionScrollPane = new ScrollPane(descriptionArea);
        descriptionScrollPane.setFitToWidth(true);
        descriptionScrollPane.setPrefHeight(100);
        descriptionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        descriptionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        descriptionScrollPane.setVisible(false);

        VBox profileLeft = new VBox(profileImageView);
        profileLeft.setAlignment(Pos.TOP_LEFT);

        HBox profileRow = new HBox(15, profileLeft, descriptionScrollPane);
        profileRow.setAlignment(Pos.CENTER_LEFT);
        return profileRow;
    }

    private VBox buildContentBox(Label titleLabel, HBox searchRow, HBox profileRow) {
        resultLabel = new Label("Current Channel:");
        resultLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
        resultLabel.setTextFill(Color.DARKSLATEGRAY);
        resultLabel.setVisible(false);

        liveStatusLabel = new Label();
        liveStatusLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        liveStatusLabel.setTextFill(Color.BLACK);
        liveStatusLabel.setVisible(false);

        streamsButton = createHiddenButton("Get Streams");
        uploadsButton = createHiddenButton("Get Uploads");
        scheduledButton = createHiddenButton("Get Scheduled");

        VBox box = new VBox(10, titleLabel, searchRow, resultLabel, profileRow,
                liveStatusLabel, streamsButton, uploadsButton, scheduledButton);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(0));
        return box;
    }

    private HBox buildTopBar(VBox contentBox) {
        Button backButton = createBackButton();
        Button homeButton = createHomeButton();

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setSpacing(20);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(contentBox, spacer, backButton, homeButton);
        return topBar;
    }

    private Button createHiddenButton(String text) {
        Button button = new Button(text);
        button.setVisible(false);
        return button;
    }

    private Button createHomeButton() {
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {
            GUIScreenBuilder builder = new GUIScreenBuilder();
            GUIScreenController controller = new GUIScreenController(stage);
            stage.getScene().setRoot(builder.buildMainScreen(controller));
        });
        return homeButton;
    }

    private Button createBackButton() {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            YoutubeModeScreenBuilder modeBuilder = new YoutubeModeScreenBuilder();
            stage.getScene().setRoot(modeBuilder.build(context, stage));
        });
        return backButton;
    }

    private Label createTitleLabel() {
        Label label = new Label("Search YouTube Channels!");
        label.setFont(Font.font("System", FontWeight.BOLD, 30));
        label.setTextFill(Color.DARKRED);
        return label;
    }
}