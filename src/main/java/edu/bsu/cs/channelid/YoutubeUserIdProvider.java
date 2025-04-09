package edu.bsu.cs.channelid;

import edu.bsu.cs.api.ApiContext;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import java.io.IOException;
import java.util.Collections;

public class YoutubeUserIdProvider implements ChannelIdProvider {
    private final ApiContext context;

    public YoutubeUserIdProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public String getUserId(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Invalid YouTube username provided.");
            return null;
        }

        YouTube.Channels.List channelRequest = context.youtubeService.channels()
                .list(Collections.singletonList("snippet"))
                .setKey(context.youtubeAuthToken)
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
