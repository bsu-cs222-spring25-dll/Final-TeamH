package edu.bsu.cs.gui.twitch;

import edu.bsu.cs.api.ApiContext;
import javafx.stage.Stage;

public class TwitchModeSelectionScreenController {

    private final ApiContext context;
    private final Stage stage;

    public TwitchModeSelectionScreenController(ApiContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    public void handleSearchClick() {
        TwitchScreenBuilder builder = new TwitchScreenBuilder();
        stage.getScene().setRoot(builder.buildTwitchScreen(context, stage));
    }

    public void handleCategoryClick() {
        showPlaceholderAlert("Twitch category screen is coming soon.");
    }

    private void showPlaceholderAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}