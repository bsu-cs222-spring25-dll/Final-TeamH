package edu.bsu.cs;

import java.io.IOException;
import java.util.ArrayList;

public class GUITwitchInfo {
    private final RetrieveStreamsService retrieveStreamsService;
    private final RetrieveClips retrieveClips;
    private final RetrieveScheduledStreams retrieveScheduledStreams;

    public GUITwitchInfo(RetrieveStreamsService retrieveStreamsService, RetrieveClips retrieveClips, RetrieveScheduledStreams retrieveScheduledStreams) {
        this.retrieveStreamsService = retrieveStreamsService;
        this.retrieveClips = retrieveClips;
        this.retrieveScheduledStreams = retrieveScheduledStreams;
    }

    public String fetchTwitchVODs(String username){
        return retrieveStreamsService.getTwitchStreams(username);
    }
    public ArrayList<String> fetchTwitchClips(String username){
        return retrieveClips.getTwitchClips(username);
    }
    public ArrayList<String> fetchStreamSchedule(String username) throws IOException {
        return retrieveScheduledStreams.fetchScheduledStreams(username);
    }
}