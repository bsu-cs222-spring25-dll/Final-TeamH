package edu.bsu.cs.profilepicture;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Collections;

public class YoutubeProfilePictureProvider implements ProfilePictureProvider {
    private final ApiContext context;

    public YoutubeProfilePictureProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public String getProfilePicture(String username) {
        try {
            var searchRequest = context.youtubeService.search()
                    .list(Collections.singletonList("id"))
                    .setQ(username)
                    .setType(Collections.singletonList("channel"))
                    .setMaxResults(1L)
                    .setKey(context.youtubeAuthToken);

            var searchResponse = searchRequest.execute();
            if (searchResponse.getItems() == null || searchResponse.getItems().isEmpty()) return null;

            String channelId = searchResponse.getItems().get(0).getId().getChannelId();

            YouTube.Channels.List channelRequest = context.youtubeService.channels()
                    .list(Collections.singletonList("snippet"))
                    .setId(Collections.singletonList(channelId))
                    .setKey(context.youtubeAuthToken);

            ChannelListResponse channelResponse = channelRequest.execute();
            if (channelResponse.getItems() == null || channelResponse.getItems().isEmpty()) return null;

            return channelResponse.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
        } catch (IOException e) {
            System.err.println("Error fetching YouTube profile picture for " + username + ": " + e.getMessage());
            return null;
        }
    }
}
