package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.ChannelSearchResult;
import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.github.twitch4j.helix.domain.ChannelSearchList;

import java.util.ArrayList;
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
}
