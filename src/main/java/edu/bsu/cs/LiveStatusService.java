package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LiveStatusService {
    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final String youtubeApiKey;
    private final YouTube youtubeService;
    private final ObtainStreamerID obtainStreamerID;

    public LiveStatusService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeService = youtubeService;
        this.obtainStreamerID = new ObtainStreamerID(twitchClient, youtubeService, twitchAuthToken, youtubeApiKey);
    }

    public void getLiveStatus(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);

        if (userId == null) {
            System.out.println("Error: Could not retrieve YouTube Channel ID for " + username);
            return;
        }

        YouTube.Search.List searchRequest = youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(youtubeApiKey)
                .setChannelId(userId)
                .setEventType("live")
                .setType(Arrays.asList(("video")));

        SearchListResponse searchResponse = searchRequest.execute();
        List<SearchResult> Results = searchResponse.getItems();

        if (!Results.isEmpty()) {
            System.out.println("This Youtuber is live!");
            for (SearchResult result : Results) {
                System.out.println("Live Stream Title: " + result.getSnippet().getTitle());
                System.out.println("Watch here: https://www.youtube.com/watch?v=" + result.getId().getVideoId());
            }
        } else {
            System.out.println("This YouTuber is NOT live.");
        }
    }
}