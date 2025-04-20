package edu.bsu.cs.gui.youtube;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.RetrieveScheduledStreams;
import edu.bsu.cs.RetrieveStreamsService;
import edu.bsu.cs.RetrieveVideosService;
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
            if (streams == null || streams.isEmpty()) {
                showError("No Streams Found", "No recent streams available for this channel.");
                return;
            }

            List<String> displayList = new ArrayList<>();
            for (SearchResult result : streams) {
                if (result.getId() == null || result.getSnippet() == null) continue;
                String title = result.getSnippet().getTitle();
                String videoId = result.getId().getVideoId();
                String thumbnail = result.getSnippet().getThumbnails().getDefault().getUrl();
                if (videoId != null && thumbnail != null) {
                    displayList.add(title + "__" + videoId + "__" + thumbnail);
                }
            }

            new YoutubeMediaScreenBuilder().display(stage, displayList, previousRoot);
        } catch (Exception e) {
            showError("Error", "Failed to load YouTube streams: " + e.getMessage());
        }
    }

    public void showRecentVideos(String channelName) {
        try {
            List<SearchResult> videos = videosService.fetchRecentVideos(channelName);
            if (videos == null || videos.isEmpty()) {
                showError("No Videos Found", "No recent uploads available for this channel.");
                return;
            }

            List<String> displayList = new ArrayList<>();
            for (SearchResult result : videos) {
                if (result.getId() == null || result.getSnippet() == null) continue;
                String title = result.getSnippet().getTitle();
                String videoId = result.getId().getVideoId();
                String thumbnail = result.getSnippet().getThumbnails().getDefault().getUrl();
                if (videoId != null && thumbnail != null) {
                    displayList.add(title + "__" + videoId + "__" + thumbnail);
                }
            }

            new YoutubeMediaScreenBuilder().display(stage, displayList, previousRoot);
        } catch (Exception e) {
            showError("Error", "Failed to load YouTube videos: " + e.getMessage());
        }
    }

    public void showScheduledStreams(String username) {
        RetrieveScheduledStreams scheduler = new RetrieveScheduledStreams(ApiInitializer.initializeApiContext());

        try {
            List<SearchResult> scheduled = scheduler.fetchScheduledStreams(username);
            if (scheduled == null || scheduled.isEmpty()) {
                showError("No Scheduled Streams Found", "There are no upcoming YouTube streams.");
                return;
            }

            List<String> displayList = new ArrayList<>();
            for (SearchResult result : scheduled) {
                if (result.getId() == null || result.getSnippet() == null) continue;

                String title = result.getSnippet().getTitle();
                String videoId = result.getId().getVideoId();
                String thumbnail = result.getSnippet().getThumbnails().getDefault().getUrl();

                if (videoId != null && thumbnail != null) {
                    displayList.add(title + "__" + videoId + "__" + thumbnail);
                }
            }

            if (displayList.isEmpty()) {
                showError("No Valid Scheduled Streams", "No upcoming streams with valid data.");
                return;
            }

            new YoutubeMediaScreenBuilder().display(stage, displayList, previousRoot);
        } catch (Exception e) {
            showError("Error", "Failed to load scheduled streams: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}