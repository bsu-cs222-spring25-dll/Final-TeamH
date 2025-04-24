package edu.bsu.cs.gui.youtube;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.services.RetrieveScheduledStreams;
import edu.bsu.cs.services.RetrieveStreamsService;
import edu.bsu.cs.services.RetrieveVideosService;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.api.ApiInitializer;
import edu.bsu.cs.services.TopYoutubeStreams;
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
            String userId = videosService.getObtainStreamerID().getYoutubeUserId(channelName);
            if (userId == null) {
                showError("Error", "Could not retrieve YouTube Channel ID for " + channelName);
                return;
            }

            List<SearchResult> videos = videosService.fetchRecentVideosById(userId);
            handleDisplayResults(videos, "No Videos Found", "No recent uploads available for this channel.");
        } catch (Exception e) {
            showError("Error", "Failed to load YouTube videos: " + e.getMessage());
        }
    }

    public void showScheduledStreams(String username) {
        RetrieveScheduledStreams scheduler = new RetrieveScheduledStreams(ApiInitializer.initializeApiContext());

        try {
            List<SearchResult> scheduled = scheduler.fetchYoutubeScheduledStreams(username);
            handleDisplayResults(scheduled, "No Scheduled Streams Found", "There are no upcoming YouTube streams.");
        } catch (Exception e) {
            showError("Error", "Failed to load scheduled streams: " + e.getMessage());
        }
    }
    public void showTopLiveStreamsByCategory(String categoryId) {
        TopYoutubeStreams category  = new TopYoutubeStreams(ApiInitializer.initializeApiContext());

        try {
            List<SearchResult> liveStreams = category.fetchTopLiveStreamsByCategory(categoryId);
            handleDisplayResults(
                    liveStreams,
                    "No Live Streams Found",
                    "There are currently no live streams for this category."
            );
        } catch (Exception e) {
            showError("Error", "Failed to load live streams: " + e.getMessage());
        }
    }

    public void showTopLiveStreams() {
        TopYoutubeStreams category  = new TopYoutubeStreams(ApiInitializer.initializeApiContext());

        try {
            List<SearchResult> liveStreams = category.fetchTopStreams();
            handleDisplayResults(
                    liveStreams,
                    "No Live Streams Found",
                    "There are currently no live streams for this category."
            );
        } catch (Exception e) {
            showError("Error", "Failed to load live streams: " + e.getMessage());
        }
    }

    private void handleDisplayResults(List<SearchResult> results, String emptyTitle, String emptyMessage) {
        if (results == null || results.isEmpty()) {
            showError(emptyTitle, emptyMessage);
            return;
        }

        List<YoutubeVideoEntry> displayList = convertToDisplayList(results);
        if (displayList.isEmpty()) {
            showError(emptyTitle, emptyMessage);
            return;
        }

        new YoutubeMediaScreenBuilder().display(stage, displayList, previousRoot);
    }

    private List<YoutubeVideoEntry> convertToDisplayList(List<SearchResult> results) {
        List<YoutubeVideoEntry> displayList = new ArrayList<>();
        for (SearchResult result : results) {
            if (result.getId() == null ||
                    result.getId().getKind() == null ||
                    !"youtube#video".equals(result.getId().getKind()) ||
                    result.getId().getVideoId() == null ||
                    result.getSnippet() == null ||
                    result.getSnippet().getTitle() == null ||
                    result.getSnippet().getThumbnails() == null ||
                    result.getSnippet().getThumbnails().getDefault() == null ||
                    result.getSnippet().getThumbnails().getDefault().getUrl() == null) {
                continue;
            }

            String title = result.getSnippet().getTitle();
            String videoId = result.getId().getVideoId();
            String thumbnail = result.getSnippet().getThumbnails().getDefault().getUrl();

            YoutubeVideoEntry entry = new YoutubeVideoEntry(title, videoId, thumbnail);
            displayList.add(entry);
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