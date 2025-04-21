package edu.bsu.cs.services;

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
        if (isInvalidUsername(username)) return null;

        YouTube.Channels.List request = context.youtubeService.channels()
                .list(Collections.singletonList("snippet"))
                .setKey(context.youtubeAuthToken)
                .setForHandle("@" + username);

        ChannelListResponse response = request.execute();
        return (response != null && response.getItems() != null && !response.getItems().isEmpty())
                ? response.getItems().get(0).getId()
                : null;
    }

    private boolean isInvalidUsername(String username) {
        return username == null || username.trim().isEmpty();
    }
}