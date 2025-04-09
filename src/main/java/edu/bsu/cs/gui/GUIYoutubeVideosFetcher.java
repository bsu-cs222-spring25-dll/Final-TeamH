package edu.bsu.cs.gui;

import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.videos.YoutubeVideosProvider;

import java.io.IOException;
import java.util.List;

public class GUIYoutubeVideosFetcher {
    private final YoutubeVideosProvider videosProvider;

    public GUIYoutubeVideosFetcher(YoutubeVideosProvider videosProvider) {
        this.videosProvider = videosProvider;
    }

    public List<SearchResult> fetchRecentVideos(String username) throws IOException {
        return videosProvider.fetchRecentVideos(username);
    }
}
