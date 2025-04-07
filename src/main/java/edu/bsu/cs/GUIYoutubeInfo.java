package edu.bsu.cs;

import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeInfo {
    private final RetrieveStreamsService retrieveStreamsService;
    private final RetrieveVideosService retrieveVideosService;

    public GUIYoutubeInfo(RetrieveStreamsService retrieveStreamsService, RetrieveVideosService retrieveVideosService) {
        this.retrieveStreamsService = retrieveStreamsService;
        this.retrieveVideosService = retrieveVideosService;
    }

    public List<SearchResult> fetchYoutubeStreamDetails(String username) throws IOException {
        return retrieveStreamsService.fetchCompletedStreams(username);
    }

    public List<SearchResult> fetchYoutubeVideoDetails(String username) throws IOException {
        return retrieveVideosService.fetchRecentVideos(username);
    }


}
