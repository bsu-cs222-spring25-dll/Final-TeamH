package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class YoutubeModeScreenController {

    private final ApiContext context;
    private final Stage stage;

    public YoutubeModeScreenController(ApiContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    public void handleSearchClick() {
        showPlaceholderAlert("YouTube search screen not yet implemented.");
    }

    public void handleCategoryClick() {
        showPlaceholderAlert("YouTube category screen is coming soon.");
    }

    private void showPlaceholderAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}