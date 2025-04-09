package edu.bsu.cs.streamersearch;

import edu.bsu.cs.api.ApiContext;

import java.util.List;

public class StreamerSearchAggregator {
    private final StreamerSearchProvider twitchProvider;
    private final StreamerSearchProvider youtubeProvider;

    public StreamerSearchAggregator(ApiContext context) {
        this.twitchProvider = new TwitchSearchProvider(context);
        this.youtubeProvider = new YoutubeSearchProvider(context);
    }

    public List<String> search(String platform, String username) {
        return switch (platform.toLowerCase()) {
            case "twitch" -> twitchProvider.searchStreamer(username);
            case "youtube" -> youtubeProvider.searchStreamer(username);
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        };
    }
}
