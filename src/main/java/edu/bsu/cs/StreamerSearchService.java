package edu.bsu.cs;

import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Channel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StreamerSearchService {

    private final ApiContext context;

    public StreamerSearchService(ApiContext context) {
        this.context = context;
    }

    public List<String> searchTwitchStreamer(String username) {
        try {
            UserList userList = context.twitchClient.getHelix()
                    .getUsers(context.twitchAuthToken, null, Collections.singletonList(username))
                    .execute();

            if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.singletonList(userList.getUsers().get(0).getDisplayName());
        } catch (Exception e) {
            System.out.println("Error while searching Twitch: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> searchYoutubeStreamer(String username) {
        try {
            YouTube.Channels.List channelRequest = context.youtubeService.channels()
                    .list(Collections.singletonList("snippet"))
                    .setKey(context.youtubeAuthToken)
                    .setForHandle("@" + username);

            ChannelListResponse response = channelRequest.execute();
            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                return Collections.emptyList();
            }

            Channel channel = response.getItems().get(0);
            return Collections.singletonList(channel.getSnippet().getTitle());
        } catch (IOException e) {
            System.out.println("Error while searching YouTube: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
