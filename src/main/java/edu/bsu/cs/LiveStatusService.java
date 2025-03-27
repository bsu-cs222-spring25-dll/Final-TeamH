package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
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

    public void getTwitchLiveStatus(String username) {
        try {
            // Obtain the TwitchHelix instance (ensure it's initialized)
            TwitchHelix helix = twitchClient.getHelix();
            if (helix == null) {
                System.out.println("Error: TwitchHelix API client not initialized.");
                return;
            }

            // Call the updated getStreams method with user_login
            var response = helix.getStreams(
                    twitchAuthToken, // OAuth token (ensure it has necessary scopes)
                    null, // Pagination cursor (after)
                    null, // Pagination cursor (before)
                    1,    // Limit results to 1
                    null, // Game IDs (not needed)
                    null, // Language (not needed)
                    null, // User IDs (not using IDs here)
                    Collections.singletonList(username) // Pass the login name
            ).execute();

            // Check if the response contains an active stream
            if (response != null && response.getStreams() != null && !response.getStreams().isEmpty()) {
                var stream = response.getStreams().get(0);
                System.out.println(username + " is LIVE on Twitch!");
                System.out.println("Stream Title: " + stream.getTitle());
                System.out.println("Watch: https://www.twitch.tv/" + username);
            } else {
                System.out.println(username + " is NOT live on Twitch.");
            }
        } catch (Exception e) {
            System.out.println("Error checking Twitch live status: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void getYoutubeLiveStatus(String username) throws IOException {
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