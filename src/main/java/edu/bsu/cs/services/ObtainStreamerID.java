
package edu.bsu.cs.services;

import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ObtainStreamerID {

    private final ApiContext context;

    public ObtainStreamerID(ApiContext context) {
        this.context = context;
    }

    public String getTwitchUserId(String username) {
        if (isInvalidUsername(username)) return null;
        try {
            UserList userList = context.twitchClient.getHelix()
                    .getUsers(context.twitchAuthToken, null, List.of(username))
                    .execute();

            return userList != null && !userList.getUsers().isEmpty()
                    ? userList.getUsers().get(0).getId()
                    : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getYoutubeUserId(String username) throws IOException {
        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setQ(username)
                .setType(Collections.singletonList("channel"))
                .setMaxResults(1L)
                .setKey(context.youtubeAuthToken);

        SearchListResponse response = request.execute();
        if (response.getItems() != null && !response.getItems().isEmpty()) {
            return response.getItems().get(0).getSnippet().getChannelId();
        }

        YouTube.Channels.List handleRequest = context.youtubeService.channels()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setForUsername(username);

        ChannelListResponse handleResponse = handleRequest.execute();
        if (handleResponse.getItems() != null && !handleResponse.getItems().isEmpty()) {
            return handleResponse.getItems().get(0).getId();
        }

        return null;
    }

    private boolean isInvalidUsername(String username) {
        return username == null || username.trim().isEmpty();
    }
}
