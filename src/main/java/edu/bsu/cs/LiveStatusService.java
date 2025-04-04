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

    public String getTwitchLiveStatus(String username) {
        try {
            TwitchHelix helix = twitchClient.getHelix();
            if (helix == null) {
                return "Error: TwitchHelix API client not initialized.";
            }

            var response = helix.getStreams(
                    twitchAuthToken,
                    null,
                    null,
                    1,
                    null,
                    null,
                    null,
                    Collections.singletonList(username)
            ).execute();

            if (response != null && response.getStreams() != null && !response.getStreams().isEmpty()) {
                var stream = response.getStreams().get(0);
                return username + " is LIVE on Twitch!\n" +
                        "Stream Title: " + stream.getTitle() + "\n" +
                        "Watch: https://www.twitch.tv/" + username;
            } else {
                return username + " is NOT live on Twitch.";
            }
        } catch (Exception e) {
            return "Error checking Twitch live status: " + e.getMessage();
        }
    }

    public String getYoutubeLiveStatus(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);

        if (userId == null) {
            return "Error: Could not retrieve YouTube Channel ID for " + username;
        }

        YouTube.Search.List searchRequest = youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(youtubeApiKey)
                .setChannelId(userId)
                .setEventType("live")
                .setType(Arrays.asList(("video")));

        SearchListResponse searchResponse = searchRequest.execute();
        List<SearchResult> results = searchResponse.getItems();

        if (!results.isEmpty()) {
            StringBuilder liveStreams = new StringBuilder("This Youtuber is live!\n");
            for (SearchResult result : results) {
                liveStreams.append("Live Stream Title: ").append(result.getSnippet().getTitle()).append("\n")
                        .append("Watch here: https://www.youtube.com/watch?v=").append(result.getId().getVideoId()).append("\n");
            }
            return liveStreams.toString();
        } else {
            return "This YouTuber is NOT live.";
        }
    }
}
