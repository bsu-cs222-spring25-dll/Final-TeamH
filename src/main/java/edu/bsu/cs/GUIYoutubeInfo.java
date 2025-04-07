package edu.bsu.cs;

import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeInfo {
    private final RetrieveStreamsService retrieveStreamsService;

    public GUIYoutubeInfo(RetrieveStreamsService retrieveStreamsService) {
        this.retrieveStreamsService = retrieveStreamsService;
    }

    public List<SearchResult> fetchYoutubeStreamDetails(String username) throws IOException {
        return retrieveStreamsService.fetchCompletedStreams(username);
    }



}
