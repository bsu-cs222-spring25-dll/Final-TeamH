package edu.bsu.cs;

import com.github.twitch4j.helix.domain.ChannelSearchResult;

import java.util.ArrayList;
import java.util.List;

public class StreamerSearchService {

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
