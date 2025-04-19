package edu.bsu.cs.gui;

import edu.bsu.cs.ChannelInfoService;
import edu.bsu.cs.LiveStatusService;
import edu.bsu.cs.ProfilePictureService;
import edu.bsu.cs.StreamerSearchService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class TwitchScreenController {

    private final StreamerSearchService searchService;
    private final ProfilePictureService pictureService;
    private final ChannelInfoService channelInfoService;
    private final LiveStatusService liveStatusService;
    private final Label resultLabel;
    private final ImageView profileImageView;
    private final TextArea bioTextArea;
    private final ScrollPane bioScrollPane;
    private final Label liveStatusLabel;

    public TwitchScreenController(ApiContext context, Label resultLabel, ImageView profileImageView, TextArea bioTextArea, ScrollPane bioScrollPane, Label liveStatusLabel) {
        this.searchService = new StreamerSearchService(context);
        this.pictureService = new ProfilePictureService(context);
        this.channelInfoService = new ChannelInfoService(context);
        this.liveStatusService = new LiveStatusService(context);
        this.resultLabel = resultLabel;
        this.profileImageView = profileImageView;
        this.bioTextArea = bioTextArea;
        this.bioScrollPane = bioScrollPane;
        this.liveStatusLabel = liveStatusLabel;
    }

    public void handleSearch(String username) {
        if (isInvalidUsername(username)) {
            showError("Invalid input", "Username cannot be empty or contain spaces.");
            return;
        }

        String displayName = findStreamer(username);
        if (displayName == null) {
            showError("Streamer Not Found", "No Twitch streamer found with the username: " + username);
            return;
        }

        updateStreamerDisplay(displayName);
    }

    private boolean isInvalidUsername(String username) {
        return username == null || username.trim().isEmpty() || username.contains(" ");
    }

    private String findStreamer(String username) {
        List<String> result = searchService.searchTwitchStreamer(username);
        return (result == null || result.isEmpty()) ? null : result.get(0);
    }

    private void updateStreamerDisplay(String displayName) {
        resultLabel.setText("Current Streamer: " + displayName);
        resultLabel.setVisible(true);
        loadProfileImage(displayName);
        loadBio(displayName);
        updateLiveStatus(displayName);
    }

    private void loadProfileImage(String displayName) {
        String imageUrl = pictureService.getProfilePicture(displayName, "Twitch");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            profileImageView.setImage(new Image(imageUrl, 100, 100, true, true));
        }
    }

    private void loadBio(String displayName) {
        String info = channelInfoService.getTwitchStreamerInfo(displayName);
        bioTextArea.setText(info);
        bioScrollPane.setVisible(true);
    }

    private void updateLiveStatus(String displayName) {
        String status = liveStatusService.getTwitchLiveStatus(displayName);
        liveStatusLabel.setText(status);
        liveStatusLabel.setVisible(true);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}