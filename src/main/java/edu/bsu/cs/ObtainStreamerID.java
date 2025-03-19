package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.helix.domain.User;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
        try {
            // Ensure username is not null or empty
            if (username == null || username.trim().isEmpty()) {
                System.out.println("Invalid username provided.");
                return null;
            }

            UserList userList = twitchClient.getHelix()
                    .getUsers(twitchAuthToken, null, List.of(username))
                    .execute();

            if (userList == null || userList.getUsers().isEmpty()) {
                System.out.println("No user found with username: " + username);
                return null;
            }

            String userId = userList.getUsers().get(0).getId();
            System.out.println("Retrieved Twitch User ID: " + userId);
            return userId;

        } catch (Exception e) {
            System.out.println("Failed to retrieve Twitch user ID: " + e.getMessage());
            e.printStackTrace();
            return null; // Return null if the user is not found or an error occurs
        }
    }

    public String getYoutubeUserId(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Invalid YouTube username provided.");
            return null;
        }

        YouTube.Channels.List channelRequest = youtubeService.channels()
                .list(Collections.singletonList("snippet"))
                .setKey(youtubeApiKey)
                .setForHandle("@" + username);

        ChannelListResponse response = channelRequest.execute();

        if (response.getItems().isEmpty()) {
            System.out.println("No YouTube channel found for username: " + username);
            return null;
        }

        String userId = response.getItems().get(0).getId();
        System.out.println("Retrieved YouTube Channel ID: " + userId);
        return userId;
    }
}
