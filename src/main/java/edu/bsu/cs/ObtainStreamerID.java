package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.helix.domain.User;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ObtainStreamerID {

    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final YouTube youtubeService;
    private final String youtubeApiKey;

    public ObtainStreamerID(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeService = youtubeService;
        this.youtubeApiKey = youtubeApiKey;
    }

    public String getTwitchUserId(String username) {
        UserList userList = twitchClient.getHelix()
                .getUsers(twitchAuthToken, null, List.of(username))
                .execute();

        Optional<User> user = userList.getUsers().stream().findFirst();
        String userId = user.map(User::getId).orElse(null);

        System.out.println("Retrieved Twitch User ID: " + userId); // Debugging line
        return userId;
    }

    public String getYoutubeUserId(String username) throws IOException {
        YouTube.Channels.List channelRequest = youtubeService.channels()
                .list(Collections.singletonList("snippet")) // Request only snippet field
                .setKey(youtubeApiKey)
                .setForHandle("@" + username);

        ChannelListResponse response = channelRequest.execute();

        if (response.getItems().isEmpty()) {
            return null; // No channel found
        }

        String userId = response.getItems().get(0).getId();
        System.out.println("Retrieved YouTube Channel ID: " + userId); // Debugging line
        return userId;
    }
}
