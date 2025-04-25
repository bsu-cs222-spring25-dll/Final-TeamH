package edu.bsu.cs.services;

import com.github.twitch4j.helix.domain.User;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import edu.bsu.cs.api.ApiContext;

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
            try {
                return getTwitchProfilePicture(username);
            } catch (Exception e) {
                return null;
            }
        } else if (platform.equalsIgnoreCase("YouTube")) {
            try {
                return getYouTubeProfilePicture(username);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private String getTwitchProfilePicture(String username) {
        return context.twitchClient.getHelix()
                .getUsers(null, null, List.of(username))
                .execute()
                .getUsers()
                .stream()
                .findFirst()
                .map(User::getProfileImageUrl)
                .orElse(null);
    }

    private String getYouTubeProfilePicture(String username) throws IOException {
        String channelId = fetchYouTubeChannelId(username);
        if (channelId == null) return null;

        ChannelListResponse response = context.youtubeService.channels()
                .list(Collections.singletonList("snippet"))
                .setId(Collections.singletonList(channelId))
                .setKey(context.youtubeAuthToken)
                .execute();

        return extractHighThumbnailUrl(response);
    }

    private String fetchYouTubeChannelId(String username) throws IOException {
        var searchResponse = context.youtubeService.search()
                .list(Collections.singletonList("id"))
                .setQ(username)
                .setType(Collections.singletonList("channel"))
                .setMaxResults(1L)
                .setKey(context.youtubeAuthToken)
                .execute();

        return (searchResponse != null
                && searchResponse.getItems() != null
                && !searchResponse.getItems().isEmpty())
                ? searchResponse.getItems().get(0).getId().getChannelId()
                : null;
    }

    private String extractHighThumbnailUrl(ChannelListResponse response) {
        if (response == null
                || response.getItems() == null
                || response.getItems().isEmpty()) {
            return null;
        }
        return response.getItems().get(0)
                .getSnippet()
                .getThumbnails()
                .getHigh()
                .getUrl();
    }
}