package edu.bsu.cs.gui.youtube;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.services.RetrieveScheduledStreams;
import edu.bsu.cs.services.RetrieveStreamsService;
import edu.bsu.cs.services.RetrieveVideosService;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.api.ApiInitializer;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class YoutubeMediaScreenController {

    private final Stage stage;
    private final Pane previousRoot;
    private final RetrieveStreamsService streamsService;
    private final RetrieveVideosService videosService;

    public YoutubeMediaScreenController(Stage stage, Pane previousRoot, ApiContext context) {
        this.stage = stage;
        this.previousRoot = previousRoot;
        this.streamsService = new RetrieveStreamsService(context);
        this.videosService = new RetrieveVideosService(context);
    }

    public void showStreams(String channelName) {
        try {
            List<SearchResult> streams = streamsService.fetchCompletedStreams(channelName);
            handleDisplayResults(streams, "No Streams Found", "No recent streams available for this channel.");
        } catch (Exception e) {
            showError("Error", "Failed to load YouTube streams: " + e.getMessage());
        }
    }

    public void showRecentVideos(String channelName) {
        try {
            List<SearchResult> videos = videosService.fetchRecentVideos(channelName);
            handleDisplayResults(videos, "No Videos Found", "No recent uploads available for this channel.");
        } catch (Exception e) {
            showError("Error", "Failed to load YouTube videos: " + e.getMessage());
        }
    }

    public void showScheduledStreams(String username) {
        RetrieveScheduledStreams scheduler = new RetrieveScheduledStreams(ApiInitializer.initializeApiContext());

        try {
            List<SearchResult> scheduled = scheduler.fetchScheduledStreams(username);
            handleDisplayResults(scheduled, "No Scheduled Streams Found", "There are no upcoming YouTube streams.");
        } catch (Exception e) {
            showError("Error", "Failed to load scheduled streams: " + e.getMessage());
        }
    }

    private void handleDisplayResults(List<SearchResult> results, String emptyTitle, String emptyMessage) {
        if (results == null || results.isEmpty()) {
            showError(emptyTitle, emptyMessage);
            return;
        }

        List<String> displayList = convertToDisplayList(results);
        if (displayList.isEmpty()) {
            showError(emptyTitle, emptyMessage);
            return;
        }

        new YoutubeMediaScreenBuilder().display(stage, displayList, previousRoot);
    }

    private List<String> convertToDisplayList(List<SearchResult> results) {
        List<String> displayList = new ArrayList<>();
        for (SearchResult result : results) {
            if (result.getId() == null || result.getSnippet() == null) continue;

            String title = result.getSnippet().getTitle();
            String videoId = result.getId().getVideoId();
            String thumbnail = result.getSnippet().getThumbnails().getDefault().getUrl();

            if (videoId != null && thumbnail != null) {
                displayList.add(title + "__" + videoId + "__" + thumbnail);
            }
        }
        return displayList;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}