package edu.bsu.cs;

import com.github.twitch4j.helix.domain.User;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ProfilePictureService {

    private final ApiContext context;

    public ProfilePictureService(ApiContext context) {
        this.context = context;
    }

    public String getProfilePicture(String username, String platform) {
        if (platform.equalsIgnoreCase("Twitch")) {
            return getTwitchProfilePicture(username);
        } else if (platform.equalsIgnoreCase("YouTube")) {
            return getYouTubeProfilePicture(username);
        }
        return null;
    }

    private String getTwitchProfilePicture(String username) {
        return context.twitchClient.getHelix().getUsers(null, null, List.of(username))
                .execute()
                .getUsers()
                .stream()
                .findFirst()
                .map(User::getProfileImageUrl)
                .orElse(null);
    }

    private String getYouTubeProfilePicture(String username) {
        try {
            YouTube.Search.List searchRequest = context.youtubeService.search()
                    .list(Collections.singletonList("id"))
                    .setQ(username)
                    .setType(Collections.singletonList("channel"))
                    .setMaxResults(1L)
                    .setKey(ApiInitializer.YoutubeAuthToken);

            var searchResponse = searchRequest.execute();
            if (searchResponse == null || searchResponse.getItems() == null || searchResponse.getItems().isEmpty()) {
                System.err.println("Error: No YouTube channel found for user: " + username);
                return null;
            }

            String channelId = searchResponse.getItems().get(0).getId().getChannelId();

            YouTube.Channels.List channelRequest = context.youtubeService.channels()
                    .list(Collections.singletonList("snippet"))
                    .setId(Collections.singletonList(channelId))
                    .setKey(ApiInitializer.YoutubeAuthToken);

            ChannelListResponse channelResponse = channelRequest.execute();

            if (channelResponse == null || channelResponse.getItems() == null || channelResponse.getItems().isEmpty()) {
                System.err.println("Error: No channel details found for user: " + username);
                return null;
            }

            return channelResponse.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
        } catch (IOException e) {
            System.err.println("Error fetching YouTube profile picture for " + username + ": " + e.getMessage());
            return null;
        }
    }
}
