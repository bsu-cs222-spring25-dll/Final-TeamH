package edu.bsu.cs.gui;

import edu.bsu.cs.ChannelInfoService;
import edu.bsu.cs.LiveStatusService;
import edu.bsu.cs.ProfilePictureService;
import edu.bsu.cs.StreamerSearchService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class TwitchScreenController {

    private final ApiContext context;
    private final Stage stage;
    private final TwitchViewModel model;
    private final StreamerSearchService searchService;
    private final ProfilePictureService pictureService;
    private final ChannelInfoService channelInfoService;
    private final LiveStatusService liveStatusService;

    public TwitchScreenController(ApiContext context, TwitchViewModel model, Stage stage) {
        this.context = context;
        this.model = model;
        this.stage = stage;
        this.searchService = new StreamerSearchService(context);
        this.pictureService = new ProfilePictureService(context);
        this.channelInfoService = new ChannelInfoService(context);
        this.liveStatusService = new LiveStatusService(context);
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
        model.resultLabel.setText("Current Streamer: " + displayName);
        model.resultLabel.setVisible(true);

        String imageUrl = pictureService.getProfilePicture(displayName, "Twitch");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            model.profileImageView.setImage(new Image(imageUrl, 100, 100, true, true));
        }

        String info = channelInfoService.getTwitchStreamerInfo(displayName);
        model.bioTextArea.setText(info);
        model.bioScrollPane.setVisible(true);

        String status = liveStatusService.getTwitchLiveStatus(displayName);
        model.liveStatusLabel.setText(status);
        model.liveStatusLabel.setVisible(true);

        model.getStreamsButton.setVisible(true);
        model.getStreamsButton.setOnAction(e -> openStreamsScreen(displayName));
    }

    private void openStreamsScreen(String displayName) {
        TwitchStreamsScreenController controller = new TwitchStreamsScreenController(context, stage, model.twitchRoot);
        controller.showStreams(displayName);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}