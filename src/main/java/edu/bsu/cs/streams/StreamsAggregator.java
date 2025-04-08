package edu.bsu.cs.streams;

import edu.bsu.cs.ApiContext;
import java.io.IOException;

public class StreamsAggregator {

    private final TwitchStreamsProvider twitchProvider;
    private final YoutubeStreamsProvider youtubeProvider;

    public StreamsAggregator(ApiContext context) {
        this.twitchProvider = new TwitchStreamsProvider(context);
        this.youtubeProvider = new YoutubeStreamsProvider(context);
    }

    public String getTwitchStreams(String username) {
        return twitchProvider.getTwitchStreams(username);
    }

    public String getYoutubeStreams(String username) throws IOException {
        return youtubeProvider.getYoutubeStreams(username);
    }
}
