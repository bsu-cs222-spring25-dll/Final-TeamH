package edu.bsu.cs.services;

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
    private final ObtainStreamerID obtainStreamerID;

    public ChannelInfoService(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public String getYoutuberInfo(String username) throws IOException {
        String channelId = obtainStreamerID.getYoutubeUserId(username);
        if (channelId == null) {
            return "YouTube channel not found for: " + username;
        }

        Channel channel = fetchYoutubeChannelById(channelId);
        if (channel == null) {
            return "YouTube channel not found for: " + username;
        }

        String bio = extractYoutubeBio(channel);
        String subscriberCount = extractYoutubeSubscriberCount(channel);

        return "Bio:\n" + bio + "\nSubscribers: " + subscriberCount;
    }

    public String getTwitchStreamerInfo(String username) {
        User user = fetchTwitchUser(username);
        if (user == null) {
            return "Twitch channel not found for: " + username;
        }

        String bio = extractTwitchBio(user);
        long followerCount = fetchTwitchFollowerCount(user.getId());

        return "Bio:\n" + bio + "\nFollowers: " + followerCount;
    }

    private Channel fetchYoutubeChannelById(String channelId) throws IOException {
        YouTube.Channels.List request = context.youtubeService.channels()
                .list(Arrays.asList("snippet", "statistics", "brandingSettings"))
                .setKey(context.youtubeAuthToken)
                .setId(Collections.singletonList(channelId));

        ChannelListResponse response = request.execute();

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return null;
        }
        return response.getItems().get(0);
    }

    private String extractYoutubeBio(Channel channel) {
        if (channel.getBrandingSettings() != null &&
                channel.getBrandingSettings().getChannel() != null) {
            String bio = channel.getBrandingSettings().getChannel().getDescription();
            return (bio != null && !bio.isEmpty()) ? bio : "No bio available.";
        }
        return "No bio available.";
    }

    private String extractYoutubeSubscriberCount(Channel channel) {
        if (channel.getStatistics() != null &&
                channel.getStatistics().getSubscriberCount() != null) {
            return String.valueOf(channel.getStatistics().getSubscriberCount());
        }
        return "Unknown";
    }

    private User fetchTwitchUser(String username) {
        UserList userList = context.twitchClient.getHelix()
                .getUsers(null, null, Collections.singletonList(username))
                .execute();

        if (userList.getUsers().isEmpty()) return null;
        return userList.getUsers().get(0);
    }

    private String extractTwitchBio(User user) {
        String bio = user.getDescription();
        return (bio != null && !bio.isEmpty()) ? bio : "No bio available.";
    }

    private long fetchTwitchFollowerCount(String userId) {
        return context.twitchClient.getHelix()
                .getChannelFollowers(null, userId, null, null, null)
                .execute()
                .getTotal();
    }
}