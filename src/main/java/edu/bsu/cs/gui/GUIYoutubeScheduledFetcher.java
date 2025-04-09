package edu.bsu.cs.gui;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.scheduledstreams.YoutubeScheduleProvider;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeScheduledFetcher {
    private final YoutubeScheduleProvider scheduleProvider;

    public GUIYoutubeScheduledFetcher(YoutubeScheduleProvider scheduleProvider) {
        this.scheduleProvider = scheduleProvider;
    }

    public List<SearchResult> fetchScheduledStreams(String username) throws IOException {
        return scheduleProvider.fetchScheduledStreams(username);
    }
}
