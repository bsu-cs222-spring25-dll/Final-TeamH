package edu.bsu.cs.livestatus;

import edu.bsu.cs.api.ApiContext;

public class LiveStatusAggregator {
    private final LiveStatusProvider twitchProvider;
    private final LiveStatusProvider youtubeProvider;

    public LiveStatusAggregator(ApiContext context) {
        this.twitchProvider = new TwitchLiveStatusProvider(context);
        this.youtubeProvider = new YoutubeLiveStatusProvider(context);
    }

    public String getLiveStatus(String platform, String username) throws Exception {
        if (platform.equalsIgnoreCase("Twitch")) {
            return twitchProvider.getLiveStatus(username);
        } else if (platform.equalsIgnoreCase("YouTube")) {
            return youtubeProvider.getLiveStatus(username);
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }
}
