package edu.bsu.cs.streams;

import edu.bsu.cs.api.ApiContext;
import com.google.api.services.youtube.model.SearchResult;
import java.io.IOException;
import java.util.List;

public class YoutubeStreamsProvider {
    private final YoutubeStreamsFetcher fetcher;

    public YoutubeStreamsProvider(ApiContext context) {
        this.fetcher = new YoutubeStreamsFetcher(context);
    }

    public String getFormattedYoutubeStreams(String username) throws IOException {
        List<SearchResult> streams = fetcher.fetchCompletedStreams(username);
        if (streams.isEmpty()) {
            return "No recent streams found for " + username;
        }
        return YoutubeStreamsFormatter.format(streams);
    }
}
