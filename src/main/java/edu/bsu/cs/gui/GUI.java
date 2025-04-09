package edu.bsu.cs.gui;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.api.ApiInitializer;
import edu.bsu.cs.channelInfo.ChannelInfoAggregator;
import edu.bsu.cs.channelInfo.StreamerDetailsProvider;
import edu.bsu.cs.livestatus.LiveStatusFetcher;
import edu.bsu.cs.livestatus.TwitchLiveStatusProvider;
import edu.bsu.cs.livestatus.YoutubeLiveStatusProvider;
import edu.bsu.cs.profilepicture.ProfilePictureAggregator;
import edu.bsu.cs.profilepicture.TwitchProfilePictureProvider;
import edu.bsu.cs.profilepicture.YoutubeProfilePictureProvider;
import edu.bsu.cs.scheduledstreams.TwitchScheduleProvider;
import edu.bsu.cs.scheduledstreams.YoutubeScheduleProvider;
import edu.bsu.cs.streamersearch.StreamerSearchAggregator;
import edu.bsu.cs.streams.YoutubeStreamsFetcher;
import edu.bsu.cs.videos.YoutubeVideosProvider;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private GUIYoutubeInfo guiYoutubeInfo;
    private GUITwitchInfo guiTwitchInfo;
    private GUIContentPresenter contentPresenter;
    private GUIMediaRenderer mediaRenderer;
    private GUIErrorHandler errorHandler;
    private GUISceneManager sceneManager;

    private TextField usernameInput;
    private Label resultLabel;
    private ChoiceBox<String> platformSelector;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Streamer Tracker");

        this.sceneManager = new GUISceneManager(primaryStage);
        this.contentPresenter = new GUIContentPresenter();
        this.mediaRenderer = new GUIMediaRenderer();
        this.errorHandler = new GUIErrorHandler();

        final ApiContext context = ApiInitializer.initializeApiContext();

        StreamerSearchAggregator searchAggregator = new StreamerSearchAggregator(context);
        GUISearchPlatformDispatcher dispatcher = new GUISearchPlatformDispatcher(searchAggregator);
        GUISearchResultProcessor resultProcessor = new GUISearchResultProcessor();
        guiSearchHandler = new GUISearchHandler(dispatcher, resultProcessor);

        StreamerDetailsProvider detailsProvider = new StreamerDetailsProvider(new ChannelInfoAggregator(context));
        ProfilePictureAggregator pictureAggregator = new ProfilePictureAggregator(
                new TwitchProfilePictureProvider(context),
                new YoutubeProfilePictureProvider(context)
        );
        LiveStatusFetcher liveStatusFetcher = new LiveStatusFetcher(
                new TwitchLiveStatusProvider(context),
                new YoutubeLiveStatusProvider(context)
        );
        guiStreamerInfo = new GUIStreamerInfo(detailsProvider, pictureAggregator, liveStatusFetcher);

        GUIYoutubeStreamsFetcher youtubeStreamsFetcher = new GUIYoutubeStreamsFetcher(new YoutubeStreamsFetcher(context));
        GUIYoutubeVideosFetcher youtubeVideosFetcher = new GUIYoutubeVideosFetcher(new YoutubeVideosProvider(context));
        GUIYoutubeScheduledFetcher youtubeScheduledFetcher = new GUIYoutubeScheduledFetcher(new YoutubeScheduleProvider(context));
        guiYoutubeInfo = new GUIYoutubeInfo(youtubeStreamsFetcher, youtubeVideosFetcher, youtubeScheduledFetcher);

        TwitchStreamsFetcher twitchStreamsFetcher = new TwitchStreamsFetcher(context);
        TwitchClipsFetcher twitchClipsFetcher = new TwitchClipsFetcher(context);
        TwitchScheduleProvider twitchScheduleProvider = new TwitchScheduleProvider(context);
        guiTwitchInfo = new GUITwitchInfo(twitchStreamsFetcher, twitchClipsFetcher, twitchScheduleProvider);

        showInitialSearchScreen();
        primaryStage.show();
    }

    private void showInitialSearchScreen() {
        Label titleLabel = new Label("Streamer Tracker");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usernameInput = new TextField();
        usernameInput.setPromptText("Enter Streamer Username...");
        usernameInput.setMaxWidth(250);

        platformSelector = new ChoiceBox<>();
        platformSelector.getItems().addAll("Twitch", "YouTube");
        platformSelector.setValue("Twitch");

        Button searchButton = new Button("Search");
        resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px;");

        searchButton.setOnAction(e -> {
            String platform = platformSelector.getValue();
            String username = usernameInput.getText().trim();
            if (username.isBlank() || username.contains(" ")) {
                errorHandler.showError("Invalid Username", "Username cannot be empty or contain spaces.");
                return;
            }
            String result = guiSearchHandler.searchStreamerByPlatform(username, platform);
            if (!result.isEmpty()) {
                showStreamerScene(username, platform);
            } else {
                resultLabel.setText("Streamer not found.");
            }
        });

        VBox inputSection = new VBox(10, titleLabel, new HBox(10, usernameInput, platformSelector), searchButton, resultLabel);
        inputSection.setAlignment(Pos.CENTER);
        VBox layout = new VBox(20, inputSection);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        sceneManager.switchScene(new Scene(layout, 600, 300));
    }

    private void showStreamerScene(String username, String platform) {
        Label titleLabel = new Label("Streamer Tracker");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showInitialSearchScreen());

        HBox topBar = new HBox(10, titleLabel, returnButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));

        Label streamerInfo = new Label("Now viewing: " + username + " on " + platform);
        streamerInfo.setStyle("-fx-font-size: 16px;");

        String profilePictureUrl = guiStreamerInfo.fetchProfilePicture(username, platform);
        String liveStatus = guiStreamerInfo.fetchLiveStatus(username, platform);
        String channelInfo = String.valueOf(guiStreamerInfo.fetchStreamerDetails(username, platform));

        ImageView profileImageView = new ImageView(profilePictureUrl != null ?
                new Image(profilePictureUrl, 100, 100, true, true) :
                new Image("default-profile.png"));

        VBox channelInfoBox = new VBox(10, new Label(channelInfo));
        HBox profileBox = new HBox(20, profileImageView, channelInfoBox);

        Button recentStreamsButton = new Button("Recent Streams");
        Button recentVideosOrClipsButton = new Button(platform.equalsIgnoreCase("YouTube") ? "Recent Uploads" : "Recent Clips");
        Button scheduledStreamsButton = new Button("Scheduled Streams");

        if (platform.equalsIgnoreCase("YouTube")) {
            recentStreamsButton.setOnAction(e -> {
                try {
                    showYoutubeContent(username, "stream");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            recentVideosOrClipsButton.setOnAction(e -> {
                try {
                    showYoutubeContent(username, "video");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            scheduledStreamsButton.setOnAction(e -> {
                try {
                    showYoutubeContent(username, "scheduled");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } else {
            recentStreamsButton.setOnAction(e -> showTwitchVODs(username));
            recentVideosOrClipsButton.setOnAction(e -> showTwitchClips(username));
            scheduledStreamsButton.setOnAction(e -> showTwitchSchedule(username));
        }

        VBox layout = new VBox(20, topBar, streamerInfo, profileBox,
                new Label("Status: " + liveStatus),
                recentStreamsButton, recentVideosOrClipsButton, scheduledStreamsButton);

        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);
        sceneManager.switchScene(new Scene(layout, 800, 600));
    }

    private void showTwitchVODs(String username) {
        ArrayList<String> videos = guiTwitchInfo.fetchTwitchVODs(username);
        if (videos == null || videos.isEmpty()) {
            errorHandler.showError("Error", "No VODs found.");
            return;
        }

        VBox videoBox = new VBox(10);
        for (String vod : videos) {
            String[] parts = vod.split("__");
            videoBox.getChildren().add(contentPresenter.createContentBox(parts[0], parts[2], () -> openInBrowser("https://www.twitch.tv/videos/" + parts[1])));
        }

        ScrollPane scroll = new ScrollPane(videoBox);
        scroll.setFitToWidth(true);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showStreamerScene(username, "Twitch"));
        VBox layout = new VBox(10, returnButton, scroll);        layout.setPadding(new Insets(20));
        sceneManager.switchScene(new Scene(layout, 700, 500));
    }

    private void showTwitchClips(String username) {
        ArrayList<String> clips = guiTwitchInfo.fetchTwitchClips(username);
        if (clips == null || clips.isEmpty()) {
            errorHandler.showError("Error", "No clips found.");
            return;
        }

        VBox clipBox = new VBox(10);
        for (String clip : clips) {
            String[] parts = clip.split("__");
            String embedUrl = "https://clips.twitch.tv/embed?clip=" + parts[1] + "&parent=localhost";
            clipBox.getChildren().add(contentPresenter.createContentBox(parts[0], parts[2], () -> mediaRenderer.showEmbeddedMedia(sceneManager.getPrimaryStage(), embedUrl, () -> showStreamerScene(username, "Twitch"))));
        }

        ScrollPane scroll = new ScrollPane(clipBox);
        scroll.setFitToWidth(true);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showStreamerScene(username, "Twitch"));
        VBox layout = new VBox(10, returnButton, scroll);        layout.setPadding(new Insets(20));
        sceneManager.switchScene(new Scene(layout, 700, 500));
    }

    private void showTwitchSchedule(String username) {
        ArrayList<String> schedule = (ArrayList<String>) guiTwitchInfo.fetchStreamSchedule(username);
        if (schedule == null || schedule.isEmpty()) {
            errorHandler.showError("Error", "No scheduled streams found.");
            return;
        }

        VBox scheduleBox = new VBox(10);
        for (String segment : schedule) {
            String[] parts = segment.split("__");
            String startTime = ZonedDateTime.parse(parts[2]).format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
            String endTime = ZonedDateTime.parse(parts[3]).format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
            Label label = new Label(parts[0] + "\nFrom: " + startTime + " To: " + endTime);
            Button watchButton = new Button("Watch");
            watchButton.setOnAction(e -> openInBrowser("https://www.twitch.tv/videos/" + parts[1]));
            scheduleBox.getChildren().add(new VBox(5, label, watchButton));
        }

        ScrollPane scroll = new ScrollPane(scheduleBox);
        scroll.setFitToWidth(true);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showStreamerScene(username, "Twitch"));
        VBox layout = new VBox(10, returnButton, scroll);        layout.setPadding(new Insets(20));
        sceneManager.switchScene(new Scene(layout, 900, 600));
    }

    private void showYoutubeContent(String username, String type) throws IOException {
        List<SearchResult> results = switch (type) {
            case "stream" -> guiYoutubeInfo.fetchYoutubeStreamDetails(username);
            case "video" -> guiYoutubeInfo.fetchYoutubeVideoDetails(username);
            case "scheduled" -> guiYoutubeInfo.fetchYoutubeScheduledStreamsDetails(username);
            default -> null;
        };

        if (results == null || results.isEmpty()) {
            errorHandler.showError("Error", "No YouTube content found.");
            return;
        }

        VBox contentBox = new VBox(10);
        for (SearchResult result : results) {
            String title = result.getSnippet().getTitle();
            String videoId = result.getId().getVideoId();
            String thumbnailUrl = result.getSnippet().getThumbnails().getHigh().getUrl();
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            contentBox.getChildren().add(contentPresenter.createContentBox(title, thumbnailUrl, () ->
                    mediaRenderer.showEmbeddedMedia(sceneManager.getPrimaryStage(), embedUrl, () -> showStreamerScene(username, "YouTube"))));
        }

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> showStreamerScene(username, "YouTube"));
        VBox layout = new VBox(10, returnButton, scroll);        layout.setPadding(new Insets(20));
        sceneManager.switchScene(new Scene(layout, 700, 500));
    }

    private void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            errorHandler.showError("Browser Error", "Failed to open link.");
        }
    }
}
