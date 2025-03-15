package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Channel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StreamerSearchService {

    private final ITwitchClient twitchClient;
    private final YouTube youtubeService;
    private final String twitchAuthToken;
    private final String youtubeApiKey;

    public StreamerSearchService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.youtubeService = youtubeService;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
    }

    //Placeholder for implementing search for a twitch streamer
    public List<String> searchTwitchStreamer(String username) {
        //Get the user ID for the given username
        UserList userList = twitchClient.getHelix()
                .getUsers(twitchAuthToken, null, Collections.singletonList(username))
                .execute();

        if (userList.getUsers().isEmpty()) {
            return null; //No user found
        }
        //return only the broadcaster display name
        return Collections.singletonList(userList.getUsers().get(0).getDisplayName());
    }

    public List<String> searchYoutubeStreamer(String username) {
        try {
            YouTube.Channels.List channelRequest = youtubeService.channels()
                    .list(List.of("snippet")) //request ONLY snippet field (contains title and other channel details)
                    .setKey(youtubeApiKey)
                    .setForHandle("@" + username);

            ChannelListResponse response = channelRequest.execute();
            //returns an empty list if channel does not exist
            if (response.getItems().isEmpty()) {
                return Collections.emptyList();
            }

            Channel channel = response.getItems().get(0);
            return Collections.singletonList(channel.getSnippet().getTitle());

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
