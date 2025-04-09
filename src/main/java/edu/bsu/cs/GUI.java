package edu.bsu.cs;

import com.github.twitch4j.helix.domain.*;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.clips.TwitchClipsProvider;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Application {
    private GUISearchHandler guiSearchHandler;
    private GUIStreamerInfo guiStreamerInfo;
    private TextField usernameInput;
    private Label resultLabel;
    private ChoiceBox<String> platformSelector;
    private GUIYoutubeInfo guiYoutubeInfo;
    private GUITwitchInfo guiTwitchInfo;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Streamer Tracker");

        final ApiContext context = ApiInitializer.initializeApiContext();

        StreamerSearchService searchService = new StreamerSearchService(context);

        guiSearchHandler = new GUISearchHandler(searchService);
        guiStreamerInfo = new GUIStreamerInfo(new ChannelInfoService(context), new ProfilePictureService(context), new LiveStatusService(context));
        guiYoutubeInfo = new GUIYoutubeInfo(new RetrieveStreamsService(context), new RetrieveVideosService(context), new RetrieveScheduledStreams(context));
        guiTwitchInfo = new GUITwitchInfo(new RetrieveStreamsService(context),new RetrieveClips(context),new RetrieveScheduledStreams(context));
        Label titleLabel = new Label("Streamer Tracker");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usernameInput = new TextField();
        usernameInput.setPromptText("Enter Streamer Username...");
        usernameInput.setMaxWidth(250);

        platformSelector = new ChoiceBox<>();
        platformSelector.getItems().addAll("Twitch", "YouTube");
        platformSelector.setValue("Twitch");

        HBox inputRow = new HBox(10, usernameInput, platformSelector);
        inputRow.setAlignment(Pos.CENTER);

        Button searchButton = new Button("Search");
        searchButton.setMinWidth(100);

        resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px;");

        searchButton.setOnAction(e -> {
            String platform = platformSelector.getValue();
            String username = usernameInput.getText().trim();

            if (username.isEmpty()) {
                showError("Invalid Username", "Username cannot be empty.");
                return;
            }
            if (username.contains(" ")) {
                showError("Invalid Username", "Username cannot contain a space.");
                return;
            }
            String result = guiSearchHandler.GUISearchStreamer(username, platform);

            if (!result.isEmpty()) {
                showStreamerScene(primaryStage, username, platform);
            } else {
                resultLabel.setText("Streamer not found.");
            }
        });

        VBox inputSection = new VBox(10, titleLabel, inputRow, searchButton, resultLabel);
        inputSection.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, inputSection);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene searchScene = new Scene(layout, 600, 300);
        primaryStage.setScene(searchScene);
        primaryStage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showStreamerScene(Stage primaryStage, String username, String platform) {
        Label titleLabel = new Label("Streamer Tracker");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        String recentVideosOrClips;
        if (platform.equals("Youtube")){
            recentVideosOrClips = "Recent Uploads";
        }
        else{
            recentVideosOrClips = "Recent Clips";
        }
        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> start(primaryStage));

        Button recentStreamsButton = new Button("Recent Streams");
        Button recentVideosOrClipsButton = new Button(recentVideosOrClips);
        Button scheduledStreamsButton = new Button("Scheduled Streams");

        if (platform.equalsIgnoreCase("YouTube")) {
            recentStreamsButton.setOnAction(e -> showYoutubeContent(primaryStage, username, platform, "stream"));
            recentVideosOrClipsButton.setOnAction(e -> showYoutubeContent(primaryStage, username, platform, "video"));
            scheduledStreamsButton.setOnAction(e -> showYoutubeContent(primaryStage, username, platform, "scheduled"));
        } else if (platform.equalsIgnoreCase("Twitch")) {
            recentStreamsButton.setOnAction(e -> showTwitchContent(primaryStage, username, platform, "Recent Streams"));
            recentVideosOrClipsButton.setOnAction(e -> showTwitchContent(primaryStage, username, platform, "Recent Clips"));
            scheduledStreamsButton.setOnAction(e -> showTwitchContent(primaryStage,username,platform,"Scheduled Streams"));
        }

        HBox topBar = new HBox(10, titleLabel, returnButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));

        Label streamerInfo = new Label("Now viewing: " + username + " on " + platform);
        streamerInfo.setStyle("-fx-font-size: 16px;");

        String profilePictureUrl = guiStreamerInfo.fetchProfilePicture(username, platform);
        String liveStatus = guiStreamerInfo.fetchLiveStatus(username, platform);
        String channelInfo = guiStreamerInfo.fetchStreamerDetails(username, platform);

        ImageView profileImageView = new ImageView();
        if (profilePictureUrl != null) {
            Image profileImage = new Image(profilePictureUrl, 100, 100, true, true);
            profileImageView.setImage(profileImage);
        } else {
            profileImageView.setImage(new Image("default-profile.png"));
        }

        HBox profileBox = new HBox(20);
        VBox channelInfoBox = new VBox(10);
        channelInfoBox.getChildren().add(new Label(channelInfo));

        profileBox.getChildren().addAll(profileImageView, channelInfoBox);

        Label liveStatusLabel = new Label("Status: " + liveStatus);
        liveStatusLabel.setStyle("-fx-font-size: 16px;");

        VBox layout = new VBox(20, topBar, streamerInfo, profileBox, liveStatusLabel, recentStreamsButton, recentVideosOrClipsButton, scheduledStreamsButton);

        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Scene streamerScene = new Scene(layout, 800, 600);
        primaryStage.setScene(streamerScene);
    }

    public void showTwitchContent(Stage primaryStage, String username, String platform, String uploadType){
        Label titleLabel = new Label("Streamer Tracker " + "- " + uploadType);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showStreamerScene(primaryStage, username, platform));

        HBox topBar = new HBox(10, titleLabel, returnButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));
        try {
            if (uploadType.equals("Recent Streams")) {
                ArrayList<String> videos = guiTwitchInfo.fetchTwitchVODs(username);
                if (videos == null){
                    showError("Error","No VODs found.");
                }else{
                    VBox streamsBox = new VBox(10);
                    streamsBox.setAlignment(Pos.TOP_LEFT);
                    for (String video : videos) {
                        String[] info = video.split("__");
                        String title = info[0];
                        String VODId = info[1];
                        String thumbnailUrl = info[2];

                        HBox streamBox = new HBox(10);
                        ImageView thumbnailImageView = new ImageView(new Image(thumbnailUrl));
                        thumbnailImageView.setFitWidth(120);
                        thumbnailImageView.setFitHeight(90);

                        Label streamTitle = new Label(title);
                        streamTitle.setStyle("-fx-font-size: 14px;");

                        Button watchButton = new Button("Watch");
                        watchButton.setOnAction(e1 -> {
                            try {
                                showVOD(primaryStage, VODId, username, platform);
                            } catch (URISyntaxException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        streamBox.getChildren().addAll(thumbnailImageView, streamTitle, watchButton);
                        streamsBox.getChildren().add(streamBox);
                    }

                    ScrollPane scrollPane = new ScrollPane(streamsBox);
                    scrollPane.setFitToWidth(true);
                    scrollPane.setPannable(true);
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                    VBox layout = new VBox(20, topBar, streamsBox, scrollPane);
                    layout.setPadding(new Insets(20));
                    layout.setAlignment(Pos.TOP_LEFT);

                    Scene streamsScene = new Scene(layout, 600, 500);
                    primaryStage.setScene(streamsScene);
                }
            } else if (uploadType.equals("Recent Clips")) {
                ArrayList<String> clips = guiTwitchInfo.fetchTwitchClips(username);
                if (clips == null){
                    showError("Error","No clips found.");
                }else{
                    VBox streamsBox = new VBox(10);
                    streamsBox.setAlignment(Pos.TOP_LEFT);

                    for (String clip : clips) {
                        String[] info = clip.split("__");
                        String title = info[0];
                        String embeddedURL = info[1];
                        String thumbnailUrl = info[2];

                        HBox streamBox = new HBox(10);
                        ImageView thumbnailImageView = new ImageView(new Image(thumbnailUrl));
                        thumbnailImageView.setFitWidth(120);
                        thumbnailImageView.setFitHeight(90);

                        Label clipTitle = new Label(title);
                        clipTitle.setStyle("-fx-font-size: 14px;");

                        Button watchButton = new Button("Watch");
                        watchButton.setOnAction(e1 -> {
                            showClip(primaryStage, embeddedURL, username, platform, uploadType);
                        });

                        streamBox.getChildren().addAll(thumbnailImageView, clipTitle, watchButton);
                        streamsBox.getChildren().add(streamBox);
                    }

                    ScrollPane scrollPane = new ScrollPane(streamsBox);
                    scrollPane.setFitToWidth(true);
                    scrollPane.setPannable(true);
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                    VBox layout = new VBox(20, topBar, streamsBox, scrollPane);
                    layout.setPadding(new Insets(20));
                    layout.setAlignment(Pos.TOP_LEFT);

                    Scene clipsScene = new Scene(layout, 600, 500);
                    primaryStage.setScene(clipsScene);
                }
            }else if (uploadType.equals("Scheduled Streams")){
                ArrayList<String> schedule = guiTwitchInfo.fetchStreamSchedule(username);
                if (schedule == null){
                    showError("Error","No Scheduled Streams found.");
                } else {
                    VBox streamsBox = new VBox(10);
                    streamsBox.setAlignment(Pos.TOP_CENTER);
                    for (String segment : schedule) {
                        String[] info = segment.split("__");
                        String title = info[0];
                        String VODId = info[1];
                        String startTime = info[2];
                        ZonedDateTime zdt = ZonedDateTime.parse(startTime);
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
                        startTime = zdt.format(format);
                        String endTime = info[3];
                        zdt = ZonedDateTime.parse(endTime);
                        endTime = zdt.format(format);

                        HBox streamBox = new HBox(10);

                        Label streamTitle = new Label(title);
                        streamTitle.setStyle("-fx-font-size: 16px;");

                        Label startTimeLabel = new Label(startTime);
                        streamTitle.setStyle("-fx-font-size: 16px;");

                        Label endTimeLabel = new Label(endTime);
                        streamTitle.setStyle("-fx-font-size: 16px;");

                        Button watchButton = new Button("Watch");
                        watchButton.setOnAction(e1 -> {
                            try {
                                showVOD(primaryStage, VODId, username, platform);
                            } catch (URISyntaxException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        streamBox.getChildren().addAll(streamTitle, startTimeLabel, endTimeLabel, watchButton);
                        streamsBox.getChildren().add(streamBox);
                    }

                    ScrollPane scrollPane = new ScrollPane(streamsBox);
                    scrollPane.setFitToWidth(true);
                    scrollPane.setPannable(true);
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                    VBox layout = new VBox(20, topBar, streamsBox, scrollPane);
                    layout.setPadding(new Insets(20));
                    layout.setAlignment(Pos.TOP_LEFT);

                    Scene streamsScene = new Scene(layout, 900, 600);
                    primaryStage.setScene(streamsScene);
                }
            }else {
                showError("Error", "Unsupported upload type: " + uploadType);
            }
        } catch (Exception e) {
            showError("Error", "Could not fetch streams: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void showClip(Stage primaryStage, String clipId, String username, String platform, String uploadType) {
        String embeddedURL = "https://clips.twitch.tv/embed?clip="+clipId+"&parent=localhost";
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.load(embeddedURL);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> {
            webView.getEngine().load(null);
            showStreamerScene(primaryStage, username, platform);
        });

        VBox layout = new VBox(10, returnButton, webView);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        Scene videoScene = new Scene(layout, 700, 500);

        primaryStage.setScene(videoScene);
        primaryStage.show();
    }

    private void showVOD(Stage primaryStage, String VODId, String username, String platform) throws URISyntaxException, IOException {
        String URL = "https://player.twitch.tv/?video=" + VODId + "&parent=localhost&parent=127.0.0.1";

        Desktop.getDesktop().browse(new URI(URL));

        Button returnButton = new Button("Return From Browser");
        returnButton.setOnAction(e -> {
            showStreamerScene(primaryStage, username, platform);
        });

        VBox layout = new VBox(10, returnButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene videoScene = new Scene(layout, 700, 500);

        primaryStage.setScene(videoScene);
        primaryStage.show();
    }

    public void showYoutubeContent(Stage primaryStage, String username, String platform, String uploadType) {
        Label titleLabel = new Label("Streamer Tracker - Recent Streams");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showStreamerScene(primaryStage, username, platform));

        HBox topBar = new HBox(10, titleLabel, returnButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));

        try {
            List<SearchResult> streams;

            if (uploadType.equals("stream")) {
                streams = guiYoutubeInfo.fetchYoutubeStreamDetails(username);
            } else if (uploadType.equals("video")) {
                streams = guiYoutubeInfo.fetchYoutubeVideoDetails(username);
            }else if (uploadType.equals("scheduled")){
                streams = guiYoutubeInfo.fetchYoutubeScheduledStreamsDetails(username);
            }else {
                showError("Error", "Unsupported upload type: " + uploadType);
                return;
            }

            VBox streamsBox = new VBox(10);
            streamsBox.setAlignment(Pos.TOP_LEFT);

            for (SearchResult stream : streams) {
                String title = stream.getSnippet().getTitle();
                String videoId = stream.getId().getVideoId();
                String thumbnailUrl = stream.getSnippet().getThumbnails().getHigh().getUrl();

                HBox streamBox = new HBox(10);
                ImageView thumbnailImageView = new ImageView(new Image(thumbnailUrl));
                thumbnailImageView.setFitWidth(120);
                thumbnailImageView.setFitHeight(90);

                Label streamTitle = new Label(title);
                streamTitle.setStyle("-fx-font-size: 14px;");

                Button watchButton = new Button("Watch");
                watchButton.setOnAction(e1 -> {
                    showEmbeddedVideo(primaryStage, videoId, username, platform);
                });

                streamBox.getChildren().addAll(thumbnailImageView, streamTitle, watchButton);
                streamsBox.getChildren().add(streamBox);
            }

            ScrollPane scrollPane = new ScrollPane(streamsBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            VBox layout = new VBox(20, topBar, streamsBox, scrollPane);
            layout.setPadding(new Insets(20));
            layout.setAlignment(Pos.TOP_LEFT);

            Scene streamsScene = new Scene(layout, 600, 500);
            primaryStage.setScene(streamsScene);

        } catch (Exception e) {
            showError("Error", "Could not fetch streams: " + e.getMessage());
        }
    }

    private void showEmbeddedVideo(Stage primaryStage, String videoId, String username, String platform) {
        String embedUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1";

        WebView webView = new WebView();
        webView.getEngine().load(embedUrl);
        webView.setPrefSize(640, 390);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> {
            webView.getEngine().load(null);
            showStreamerScene(primaryStage, username, platform);
        });

        VBox layout = new VBox(10, returnButton, webView);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        Scene videoScene = new Scene(layout, 700, 500);
        primaryStage.setScene(videoScene);
    }

}
