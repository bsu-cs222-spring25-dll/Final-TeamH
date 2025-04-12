package edu.bsu.cs;

import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class ChannelInfoService {
    private final ApiContext context;

    public ChannelInfoService(ApiContext context) {
        this.context = context;
    }

    public String getYoutuberInfo(String username) throws IOException {
        YouTube.Channels.List channelRequest = context.youtubeService.channels()
                .list(Arrays.asList("snippet", "statistics", "brandingSettings"))
                .setKey(context.youtubeAuthToken)
                .setForHandle("@" + username);

        ChannelListResponse response = channelRequest.execute();
        if (response.getItems().isEmpty()) {
            return "YouTube channel not found for: " + username;
        }

        Channel channel = response.getItems().get(0);
        String bio = channel.getBrandingSettings().getChannel().getDescription();
        String subscriberCount = String.valueOf(channel.getStatistics().getSubscriberCount());

        if (bio.isEmpty()) {
            bio = "No bio available.";
        }

        return "Bio:\n" + bio + "\nSubscribers: " + subscriberCount;
    }

    public String getTwitchStreamerInfo(String username) {
        UserList userList = context.twitchClient.getHelix().getUsers(null, null, Collections.singletonList(username)).execute();
        if (userList.getUsers().isEmpty()) {
            return "Twitch channel not found for: " + username;
        }

        User user = userList.getUsers().get(0);
        String userId = user.getId();
        String bio = user.getDescription();
        if (bio == null || bio.isEmpty()) {
            bio = "No bio available.";
        }

        long followerCount = context.twitchClient.getHelix().getChannelFollowers(null, userId, null, null, null).execute().getTotal();
        return "Bio:\n" + bio + "\nFollowers: " + followerCount;
    }
}