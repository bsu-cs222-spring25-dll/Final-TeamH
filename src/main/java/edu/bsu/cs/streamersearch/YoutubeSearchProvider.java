package edu.bsu.cs.streamersearch;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Channel;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class YoutubeSearchProvider implements StreamerSearchProvider {
    private final ApiContext context;

    public YoutubeSearchProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public List<String> searchStreamer(String username) {
        try {
            YouTube.Channels.List channelRequest = context.youtubeService.channels()
                    .list(Collections.singletonList("snippet"))
                    .setKey(context.youtubeAuthToken)
                    .setForHandle("@" + username);

            ChannelListResponse response = channelRequest.execute();
            if (response.getItems().isEmpty()) {
                return Collections.emptyList();
            }

            Channel channel = response.getItems().get(0);
            return Collections.singletonList(channel.getSnippet().getTitle());
        } catch (IOException e) {
            System.out.println("YouTube Search Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
