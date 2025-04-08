package edu.bsu.cs;

import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeInfo {
    private final edu.bsu.cs.StreamsAggregator retrieveStreamsService;
    private final RetrieveVideosService retrieveVideosService;
    private final RetrieveScheduledStreams retrieveScheduledStreams;


    public GUIYoutubeInfo(RetrieveStreamsService retrieveStreamsService, RetrieveVideosService retrieveVideosService, RetrieveScheduledStreams retrieveScheduledStreams) {
        this.retrieveStreamsService = retrieveStreamsService;
        this.retrieveVideosService = retrieveVideosService;
        this.retrieveScheduledStreams = retrieveScheduledStreams;
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



}
