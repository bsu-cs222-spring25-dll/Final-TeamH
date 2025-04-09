package edu.bsu.cs.gui;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.streams.YoutubeStreamsFetcher;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeStreamsFetcher {
    private final YoutubeStreamsFetcher fetcher;

    public GUIYoutubeStreamsFetcher(YoutubeStreamsFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public List<SearchResult> fetchCompletedStreams(String username) throws IOException {
        return fetcher.fetchCompletedStreams(username);
    }
}
