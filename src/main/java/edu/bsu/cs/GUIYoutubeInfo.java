package edu.bsu.cs;

import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeInfo {
    private final RetrieveStreamsService retrieveStreamsService;
    private final RetrieveVideosService retrieveVideosService;
    private final RetrieveScheduledStreams retrieveScheduledStreams;
    private final TopYoutubeStreams topYoutubeStreams;

    public GUIYoutubeInfo(RetrieveStreamsService retrieveStreamsService, RetrieveVideosService retrieveVideosService, RetrieveScheduledStreams retrieveScheduledStreams, TopYoutubeStreams topYoutubeStreams) {
        this.retrieveStreamsService = retrieveStreamsService;
        this.retrieveVideosService = retrieveVideosService;
        this.retrieveScheduledStreams = retrieveScheduledStreams;
        this.topYoutubeStreams = topYoutubeStreams;
    }

    public List<SearchResult> fetchYoutubeStreamDetails(String username) throws IOException {
        return retrieveStreamsService.fetchCompletedStreams(username);
    }

    public List<SearchResult> fetchYoutubeVideoDetails(String username) throws IOException {
        return retrieveVideosService.fetchRecentVideos(username);
    }

    public List<SearchResult> fetchYoutubeScheduledStreamsDetails(String username) throws IOException {
        return retrieveScheduledStreams.fetchScheduledStreams(username);
    }

    public List<SearchResult> fetchYoutubeTopStreamsDetails() throws IOException {
        return topYoutubeStreams.fetchRandomStreamsById();
    }


}