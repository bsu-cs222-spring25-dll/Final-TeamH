package edu.bsu.cs.gui.twitch;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.TopTwitchStreams;
import javafx.stage.Stage;

public class TwitchTopCategoriesScreenController {

    private final ApiContext context;
    private final Stage stage;
    private final TopTwitchStreams topTwitchStreams;

    public TwitchTopCategoriesScreenController(ApiContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
        this.topTwitchStreams = new TopTwitchStreams(context);
    }

    public void handleCategoryClick(int categoryNumber) {
        TwitchTopStreamsForCategoryBuilder builder = new TwitchTopStreamsForCategoryBuilder();
        stage.getScene().setRoot(builder.buildTopStreamsForCategoryScreen(context, stage,
                getTopCategoryNames(categoryNumber), getTopCategoryIDs(categoryNumber),getTopCategoryURLs(categoryNumber)));
    }

    public String getTopCategoryIDs(int categoryNumber){
        String topCategoryIDsString = topTwitchStreams.getTopGamesInfo().getFirst();
        String[] topCategoryIDsArray = topCategoryIDsString.split("__");
        return topCategoryIDsArray[categoryNumber];
    }

    public String getTopCategoryNames(int categoryNumber){
        String topCategoryString = topTwitchStreams.getTopGamesInfo().get(1);
        String[] topCategoryArray = topCategoryString.split("__");
        return topCategoryArray[categoryNumber];
    }

    public String getTopCategoryURLs(int categoryNumber){
        String topCategoryURLsString = topTwitchStreams.getTopGamesInfo().get(2);
        String[] topCategoryURLsArray = topCategoryURLsString.split("__");
        return topCategoryURLsArray[categoryNumber];
    }
}