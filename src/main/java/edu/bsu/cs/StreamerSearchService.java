package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.ChannelSearchResult;
import com.google.api.services.youtube.YouTube;
import com.github.twitch4j.helix.domain.ChannelSearchList;

import java.util.ArrayList;
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
    public List<String> searchTwitchStreamer(String name) {
        ChannelSearchList searchResults = twitchClient.getHelix().searchChannels(twitchAuthToken, name, null).execute();
        List<String> streamerNames = new ArrayList<>();

        for(ChannelSearchResult result : searchResults.getChannels()) {
            streamerNames.add(result.getDisplayName());
        }

        return streamerNames;
    }
}
