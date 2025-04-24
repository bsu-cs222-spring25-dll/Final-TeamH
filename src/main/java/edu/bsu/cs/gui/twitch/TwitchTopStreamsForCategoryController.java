package edu.bsu.cs.gui.twitch;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.TopTwitchStreams;
import javafx.stage.Stage;

import java.util.List;

public class TwitchTopStreamsForCategoryController {
    private final ApiContext context;
    private final Stage stage;
    private final TopTwitchStreams topTwitchStreams;

    public TwitchTopStreamsForCategoryController(ApiContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
        this.topTwitchStreams = new TopTwitchStreams(context);
    }

    public void handlePlayButtonClick(int streamNumber, List<String> topStreamsInfo) {
        openInBrowser(getTopStreamerUsername(streamNumber,topStreamsInfo));
    }
    private void openInBrowser(String id) {
        String url = "https://www.twitch.tv/" + id;
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception ignored) {}
    }

    public String getTopStreamTitle(int streamNumber, List<String> topStreamsInfo){
        String[] streamStrings = topStreamsInfo.get(streamNumber).split("__");
        return streamStrings[0];
    }

    public String getTopStreamerUsername(int streamNumber, List<String> topStreamsInfo){
        String[] streamStrings = topStreamsInfo.get(streamNumber).split("__");
        return streamStrings[1];
    }

    public String getTopStreamThumbnailURL(int streamNumber, List<String> topStreamsInfo) {
        String[] streamStrings = topStreamsInfo.get(streamNumber).split("__");
        return streamStrings[2];
    }

    public List<String> getTopStreamsForCategoryInfo(String categoryID){
        return topTwitchStreams.getTopStreamsForCategoryInfo(categoryID);
    }
}
