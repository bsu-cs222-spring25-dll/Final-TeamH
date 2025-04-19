package edu.bsu.cs.gui;

import edu.bsu.cs.ProfilePictureService;
import edu.bsu.cs.StreamerSearchService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class TwitchScreenController {

    private final StreamerSearchService searchService;
    private final ProfilePictureService pictureService;
    private final Label resultLabel;
    private final ImageView profileImageView;

    public TwitchScreenController(ApiContext context, Label resultLabel, ImageView profileImageView) {
        this.searchService = new StreamerSearchService(context);
        this.pictureService = new ProfilePictureService(context);
        this.resultLabel = resultLabel;
        this.profileImageView = profileImageView;
    }

    public void handleSearch(String username) {
        if (username == null || username.trim().isEmpty() || username.contains(" ")) {
            showError("Invalid input", "Username cannot be empty or contain spaces.");
            return;
        }

        List<String> result = searchService.searchTwitchStreamer(username);
        if (result == null || result.isEmpty()) {
            showError("Streamer Not Found", "No Twitch streamer found with the username: " + username);
            return;
        }

        resultLabel.setText("Current Streamer: " + result.get(0));
        String url = pictureService.getProfilePicture(result.get(0), "Twitch");
        if (url != null && !url.isEmpty()) {
            profileImageView.setImage(new Image(url, 100, 100, true, true));
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
