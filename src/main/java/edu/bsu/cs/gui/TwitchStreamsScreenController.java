package edu.bsu.cs.gui;

import edu.bsu.cs.RetrieveStreamsService;
import edu.bsu.cs.api.ApiContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TwitchStreamsScreenController {

    private final RetrieveStreamsService streamService;
    private final Stage stage;
    private final Pane twitchRoot;

    public TwitchStreamsScreenController(ApiContext context, Stage stage, Pane twitchRoot) {
        this.streamService = new RetrieveStreamsService(context);
        this.stage = stage;
        this.twitchRoot = twitchRoot;
    }

    public void showStreams(String username) {
        ArrayList<String> streamData = streamService.getTwitchStreamsInfo(username);
        if (streamData != null && !streamData.isEmpty()) {
            TwitchStreamsScreenBuilder builder = new TwitchStreamsScreenBuilder();
            builder.display(stage, streamData, twitchRoot);
        } else {
            System.out.println("No streams found.");
        }
    }
}