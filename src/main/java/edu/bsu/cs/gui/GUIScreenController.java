package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.api.ApiInitializer;
import edu.bsu.cs.gui.twitch.TwitchModeSelectionScreenBuilder;
import javafx.stage.Stage;

public class GUIScreenController {

    private final Stage stage;

    public GUIScreenController(Stage stage) {
        this.stage = stage;
    }

    public void handleTwitchClick() {
        TwitchModeSelectionScreenBuilder modeBuilder = new TwitchModeSelectionScreenBuilder();
        ApiContext context = ApiInitializer.initializeApiContext();
        stage.getScene().setRoot(modeBuilder.build(context, stage));
    }

    public void handleYouTubeClick() {
        YoutubeModeScreenBuilder builder = new YoutubeModeScreenBuilder();
        ApiContext context = ApiInitializer.initializeApiContext();
        stage.getScene().setRoot(builder.build(context, stage));
    }
}
