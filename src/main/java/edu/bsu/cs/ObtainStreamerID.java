package edu.bsu.cs;

import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
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

        YouTube.Search.List searchRequest = context.youtubeService.search()
                .list(Collections.singletonList("snippet"))
                .setQ(username)
                .setType(Collections.singletonList("channel"))
                .setMaxResults(1L)
                .setKey(context.youtubeAuthToken);

        SearchListResponse searchResponse = searchRequest.execute();

        if (searchResponse == null || searchResponse.getItems() == null || searchResponse.getItems().isEmpty()) {
            System.err.println("No YouTube channel found for user: " + username);
            return null;
        }

        String userId = searchResponse.getItems().get(0).getSnippet().getChannelId();
        System.out.println("Retrieved YouTube Channel ID: " + userId);
        return userId;
    }
}