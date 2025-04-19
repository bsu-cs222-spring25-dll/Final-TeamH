package edu.bsu.cs.gui;

import edu.bsu.cs.RetrieveClips;
import edu.bsu.cs.RetrieveScheduledStreams;
import edu.bsu.cs.RetrieveStreamsService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class TwitchMediaScreenController {

    private final RetrieveStreamsService streamService;
    private final RetrieveClips clipsService;
    private final RetrieveScheduledStreams scheduledService;
    private final Stage stage;
    private final Pane twitchRoot;

    public TwitchMediaScreenController(ApiContext context, Stage stage, Pane twitchRoot) {
        this.streamService = new RetrieveStreamsService(context);
        this.clipsService = new RetrieveClips(context);
        this.scheduledService = new RetrieveScheduledStreams(context);
        this.stage = stage;
        this.twitchRoot = twitchRoot;
    }

    public void showStreams(String username) {
        List<String> streamData = streamService.getTwitchStreamsInfo(username);
        if (streamData != null && !streamData.isEmpty()) {
            new TwitchMediaScreenBuilder().display(stage, streamData, twitchRoot, "stream");
        } else {
            showError("No streams found.");
        }
    }

    public void showClips(String username) {
        List<String> clipData = clipsService.getTwitchClipsInfo(username);
        if (clipData != null && !clipData.isEmpty()) {
            new TwitchMediaScreenBuilder().display(stage, clipData, twitchRoot, "clip");
        } else {
            showError("No recent clips found for this user.");
        }
    }

    public void showScheduled(String username) {
        List<String> scheduledData = scheduledService.getStreamSchedule(username);
        if (scheduledData != null && !scheduledData.isEmpty()) {
            new TwitchMediaScreenBuilder().display(stage, scheduledData, twitchRoot, "scheduled");
        } else {
            showError("No scheduled streams found for this user.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Results");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}