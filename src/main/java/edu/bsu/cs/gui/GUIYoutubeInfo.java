package edu.bsu.cs.gui;

import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeInfo {
    private final GUIYoutubeStreamsFetcher streamsFetcher;
    private final GUIYoutubeVideosFetcher videosFetcher;
    private final GUIYoutubeScheduledFetcher scheduledFetcher;

    public GUIYoutubeInfo(GUIYoutubeStreamsFetcher streamsFetcher,
                          GUIYoutubeVideosFetcher videosFetcher,
                          GUIYoutubeScheduledFetcher scheduledFetcher) {
        this.streamsFetcher = streamsFetcher;
        this.videosFetcher = videosFetcher;
        this.scheduledFetcher = scheduledFetcher;
    }

    public List<SearchResult> fetchYoutubeStreamDetails(String username) throws IOException {
        return streamsFetcher.fetchCompletedStreams(username);
    }

    public List<SearchResult> fetchYoutubeVideoDetails(String username) throws IOException {
        return videosFetcher.fetchRecentVideos(username);
    }

    public List<SearchResult> fetchYoutubeScheduledStreamsDetails(String username) throws IOException {
        return scheduledFetcher.fetchScheduledStreams(username);
    }
}
