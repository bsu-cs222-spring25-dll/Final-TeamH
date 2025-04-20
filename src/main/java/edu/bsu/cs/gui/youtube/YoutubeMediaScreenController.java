package edu.bsu.cs.gui.youtube;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.RetrieveStreamsService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class YoutubeMediaScreenController {

    private final Stage stage;
    private final Pane previousRoot;
    private final RetrieveStreamsService streamsService;

    public YoutubeMediaScreenController(Stage stage, Pane previousRoot, ApiContext context) {
        this.stage = stage;
        this.previousRoot = previousRoot;
        this.streamsService = new RetrieveStreamsService(context);
    }

    public void showUploads(String channelName) {
        try {
            List<SearchResult> streams = streamsService.fetchCompletedStreams(channelName);
            if (streams == null || streams.isEmpty()) {
                showError("No Uploads Found", "No recent uploads available for this channel.");
                return;
            }

            List<String> displayList = new ArrayList<>();
            for (SearchResult result : streams) {
                String title = result.getSnippet().getTitle();
                String videoId = result.getId().getVideoId();
                String thumbnail = result.getSnippet().getThumbnails().getDefault().getUrl();
                displayList.add(title + "__" + videoId + "__" + thumbnail);
            }

            YoutubeMediaScreenBuilder builder = new YoutubeMediaScreenBuilder();
            builder.display(stage, displayList, previousRoot);

        } catch (Exception e) {
            showError("Error", "Failed to load YouTube uploads: " + e.getMessage());
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