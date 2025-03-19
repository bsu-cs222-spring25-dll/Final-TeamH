package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class ChannelInfoService {
    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final String youtubeApiKey;
    private final YouTube youtubeService;
    private final ObtainStreamerID obtainStreamerID;

    public ChannelInfoService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeService = youtubeService;
        this.obtainStreamerID = new ObtainStreamerID(twitchClient, null, twitchAuthToken, null);
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

    public void getTwitchStreamerInfo(String username){
        UserList userList = twitchClient.getHelix().getUsers(null, null, Collections.singletonList(username)).execute();
        if (userList.getUsers().isEmpty()) {
            System.out.println("Channel not found!");
            return;
        }

        User user = userList.getUsers().get(0);
        String userId = user.getId();
        String bio = user.getDescription();

        if (bio == null || bio.isEmpty()) {
            System.out.println("Bio is empty for: " + username);
            return;
        }

        System.out.println("Bio:\n" + bio);

        if (userId == null || userId.isEmpty()) {
            System.out.println("Error: Could not retrieve the user ID for " + username);
            return;
        }

        long followerCount = twitchClient.getHelix().getChannelFollowers(null, userId, null, null, null).execute().getTotal();
        System.out.println("\nFollower count: " + followerCount);
    }


}
