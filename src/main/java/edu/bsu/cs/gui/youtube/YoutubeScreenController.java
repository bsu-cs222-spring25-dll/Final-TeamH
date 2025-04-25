package edu.bsu.cs.gui.youtube;

import edu.bsu.cs.services.ChannelInfoService;
import edu.bsu.cs.services.LiveStatusService;
import edu.bsu.cs.services.ProfilePictureService;
import edu.bsu.cs.services.StreamerSearchService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class YoutubeScreenController {

    private final StreamerSearchService searchService;
    private final ProfilePictureService pictureService;
    private final ChannelInfoService channelInfoService;
    private final LiveStatusService liveStatusService;
    private final YoutubeViewModel model;
    private final Stage stage;
    private final ApiContext context;

    public YoutubeScreenController(ApiContext context, YoutubeViewModel model, Stage stage) {
        this.context = context;
        this.searchService = new StreamerSearchService(context);
        this.pictureService = new ProfilePictureService(context);
        this.channelInfoService = new ChannelInfoService(context);
        this.liveStatusService = new LiveStatusService(context);
        this.model = model;
        this.stage = stage;
    }

    public void handleSearch(String username) throws IOException {
        if (isInvalidUsername(username)) {
            showError("Invalid input", "Username cannot be empty or contain spaces.");
            return;
        }

        String channelName = findChannel(username);
        if (channelName == null) {
            showError("Channel Not Found", "No YouTube channel found with the username: " + username);
            return;
        }

        model.resetView();
        updateChannelDisplay(channelName);
    }

    private boolean isInvalidUsername(String username) {
        return username == null || username.trim().isEmpty() || username.contains(" ");
    }

    private String findChannel(String username) {
        List<String> result = searchService.searchYoutubeStreamer(username);
        return (result == null || result.isEmpty()) ? null : result.getFirst();
    }

    private void updateChannelDisplay(String channelName) throws IOException {
        model.resultLabel().setText("Current Channel: " + channelName);
        model.resultLabel().setVisible(true);
        loadProfileImage(channelName);
        loadChannelDescription(channelName);
        updateLiveStatus(channelName);
        model.getStreamsButton().setVisible(true);
        model.getUploadsButton().setVisible(true);
        model.getScheduledButton().setVisible(true);

        YoutubeMediaScreenController mediaController = new YoutubeMediaScreenController(stage, model.rootLayout(), context);
        model.getStreamsButton().setOnAction(_ -> mediaController.showStreams(channelName));
        model.getUploadsButton().setOnAction(_ -> mediaController.showRecentVideos(channelName));
        model.getScheduledButton().setOnAction(_ -> mediaController.showScheduledStreams(channelName));
    }

    private void loadProfileImage(String channelName) {
        String imageUrl = pictureService.getProfilePicture(channelName, "YouTube");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            model.profileImageView().setImage(new Image(imageUrl, 100, 100, true, true));
        }
    }

    private void loadChannelDescription(String channelName) throws IOException {
        String description = channelInfoService.getYoutuberInfo(channelName);

        if (description == null || description.isBlank()) {
            model.channelDescriptionArea().setText("No channel description available.");
        } else {
            model.channelDescriptionArea().setText(description);
        }

        model.descriptionScrollPane().setVisible(true);
    }

    private void updateLiveStatus(String channelName) throws IOException {
        String status = liveStatusService.getYoutubeLiveStatus(channelName);
        model.liveStatusLabel().setText(status);
        model.liveStatusLabel().setVisible(true);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}