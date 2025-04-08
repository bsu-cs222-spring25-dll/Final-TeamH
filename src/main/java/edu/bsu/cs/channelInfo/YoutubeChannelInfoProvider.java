package edu.bsu.cs.channelInfo;

import edu.bsu.cs.ApiContext;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.util.Arrays;

public class YoutubeChannelInfoProvider implements ChannelInfoProvider {
    private final ApiContext context;

    public YoutubeChannelInfoProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public ChannelInfo getChannelInfo(String username) throws Exception {
        YouTube.Channels.List channelRequest = context.youtubeService.channels()
                .list(Arrays.asList("snippet", "statistics", "brandingSettings"))
                .setKey(context.youtubeAuthToken)
                .setForHandle("@" + username);
        ChannelListResponse response = channelRequest.execute();
        if (response.getItems() == null || response.getItems().isEmpty()) {
            return null;
        }
        Channel channel = response.getItems().get(0);
        String bio = channel.getBrandingSettings().getChannel().getDescription();
        if (bio == null || bio.isEmpty()) {
            bio = "No bio available.";
        }
        String subscriberCount = String.valueOf(channel.getStatistics().getSubscriberCount());
        return new ChannelInfo(bio, subscriberCount);
    }
}
