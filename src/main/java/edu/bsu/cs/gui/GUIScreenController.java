package edu.bsu.cs.gui;

import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class GUIScreenController {

    private final Stage stage;

    public GUIScreenController(Stage stage) {
        this.stage = stage;
    }

    public void handleTwitchClick() {
        // Placeholder action
        showAlert("Twitch View", "This will open the Twitch GUI section.");
    }

    public void handleYouTubeClick() {
        // Placeholder action
        showAlert("YouTube View", "This will open the YouTube GUI section.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
