package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.Arrays;

public class ChannelInfoService {
    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final String youtubeApiKey;
    private final YouTube youtubeService;

    public ChannelInfoService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeService = youtubeService;
    }

    public void getYoutuberInfo(String username) throws IOException {
        YouTube.Channels.List channelRequest = youtubeService.channels()
                .list(Arrays.asList("snippet", "statistics", "brandingSettings"))
                .setKey(youtubeApiKey)
                .setForHandle("@" + username);

        ChannelListResponse response = channelRequest.execute();
        Channel channel = response.getItems().get(0);


        String bio = channel.getBrandingSettings().getChannel().getDescription();
        System.out.println("Bio:\n"+bio);
        if (bio.isEmpty()) {
            System.out.println("There is no bio for this channel " + username);
            return;
        }

        String subscriberCount = String.valueOf(channel.getStatistics().getSubscriberCount());
        System.out.println("Current Subscriber Count: "+subscriberCount);


    }

}
