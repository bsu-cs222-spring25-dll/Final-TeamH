package edu.bsu.cs.channelInfo;

import edu.bsu.cs.api.ApiContext;

public class ChannelInfoAggregator {
    private final ChannelInfoProvider twitchProvider;
    private final ChannelInfoProvider youtubeProvider;

    public ChannelInfoAggregator(ApiContext context) {
        this.twitchProvider = new TwitchChannelInfoProvider(context);
        this.youtubeProvider = new YoutubeChannelInfoProvider(context);
    }

    public ChannelInfo getChannelInfo(String platform, String username) throws Exception {
        if (platform.equalsIgnoreCase("Twitch")) {
            return twitchProvider.getChannelInfo(username);
        } else if (platform.equalsIgnoreCase("YouTube")) {
            return youtubeProvider.getChannelInfo(username);
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }
}
