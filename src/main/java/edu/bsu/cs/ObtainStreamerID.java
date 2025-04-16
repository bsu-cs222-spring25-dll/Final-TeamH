package edu.bsu.cs;

import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ObtainStreamerID {

    private final ApiContext context;

    public ObtainStreamerID(ApiContext context) {
        this.context = context;
    }

    public String getTwitchUserId(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                System.out.println("Invalid username provided.");
                return null;
            }

            UserList userList = context.twitchClient.getHelix()
                    .getUsers(context.twitchAuthToken, null, List.of(username))
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
            return null;
        }
    }

    public String getYoutubeUserId(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Invalid YouTube username provided.");
            return null;
        }

        YouTube.Channels.List channelRequest = context.youtubeService.channels()
                .list(Collections.singletonList("snippet"))
                .setKey(context.youtubeAuthToken)
                .setForHandle("@" + username);

        ChannelListResponse response = channelRequest.execute();

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            System.out.println("No YouTube channel found for username: " + username);
            return null;
        }

        String userId = response.getItems().get(0).getId();
        System.out.println("Retrieved YouTube Channel ID: " + userId);
        return userId;
    }
}