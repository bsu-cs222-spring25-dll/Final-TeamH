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
        TwitchStreamerScreenBuilder builder = new TwitchStreamerScreenBuilder();
        stage.getScene().setRoot(builder.buildTwitchScreen(context, stage));
    }

    public void handleCategoryClick() {
        TwitchTopCategoriesScreenBuilder builder = new TwitchTopCategoriesScreenBuilder();
        stage.getScene().setRoot(builder.buildTopCategoriesScreen(context, stage));
    }

}