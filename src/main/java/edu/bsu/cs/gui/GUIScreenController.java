package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.api.ApiInitializer;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class GUIScreenController {

    private final Stage stage;

    public GUIScreenController(Stage stage) {
        this.stage = stage;
    }

    public void handleTwitchClick() {
        ApiContext context = ApiInitializer.initializeApiContext();
        TwitchScreenBuilder twitchBuilder = new TwitchScreenBuilder();
        stage.getScene().setRoot(twitchBuilder.buildTwitchScreen(context, stage));
    }

    public void handleYouTubeClick() {
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
