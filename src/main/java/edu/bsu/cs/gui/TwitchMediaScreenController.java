package edu.bsu.cs.gui;

import edu.bsu.cs.RetrieveClips;
import edu.bsu.cs.RetrieveStreamsService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TwitchMediaScreenController {

    private final RetrieveStreamsService streamService;
    private final RetrieveClips clipsService;
    private final Stage stage;
    private final Pane twitchRoot;

    public TwitchMediaScreenController(ApiContext context, Stage stage, Pane twitchRoot) {
        this.streamService = new RetrieveStreamsService(context);
        this.clipsService = new RetrieveClips(context);
        this.stage = stage;
        this.twitchRoot = twitchRoot;
    }

    public void showStreams(String username) {
        ArrayList<String> streamData = streamService.getTwitchStreamsInfo(username);
        if (streamData != null && !streamData.isEmpty()) {
            TwitchMediaScreenBuilder builder = new TwitchMediaScreenBuilder();
            builder.display(stage, streamData, twitchRoot, "stream");
        } else {
            System.out.println("No streams found.");
        }
    }

    public void showClips(String username) {
        List<String> clipData = clipsService.getTwitchClipsInfo(username);
        if (clipData != null && !clipData.isEmpty()) {
            TwitchMediaScreenBuilder builder = new TwitchMediaScreenBuilder();
            builder.display(stage, clipData, twitchRoot, "clip");
        } else {
            System.out.println("No clips found.");
        }
    }
}