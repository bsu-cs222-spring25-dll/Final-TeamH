package edu.bsu.cs.services;

import com.github.twitch4j.helix.TwitchHelix;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class LiveStatusService {
    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public LiveStatusService(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public String getTwitchLiveStatus(String username) {
        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            if (helix == null) return "Error: TwitchHelix API client not initialized.";

            var response = helix.getStreams(
                    context.twitchAuthToken, null, null, 1, null, null, null,
                    Collections.singletonList(username)
            ).execute();

            if (response == null || response.getStreams() == null || response.getStreams().isEmpty()) {
                return username + " is NOT live on Twitch.";
            }

            return formatTwitchLiveStream(username, response.getStreams().getFirst());
        } catch (Exception e) {
            return "Error checking Twitch live status: " + e.getMessage();
        }
    }

    public String getYoutubeLiveStatus(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
        if (userId == null) return "Error: Could not retrieve YouTube Channel ID for " + username;

        YouTube.Search.List request = context.youtubeService.search()
                .list(List.of("id,snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setEventType("live")
                .setType(Collections.singletonList("video"));

        SearchListResponse response = request.execute();
        List<SearchResult> results = safeGetItems(response);

        if (results.isEmpty()) return "This YouTuber is NOT live.";

        return formatYoutubeLiveResults(results);
    }

    private List<SearchResult> safeGetItems(SearchListResponse response) {
        return (response == null || response.getItems() == null)
                ? Collections.emptyList()
                : response.getItems();
    }

    private String formatTwitchLiveStream(String username, com.github.twitch4j.helix.domain.Stream stream) {
        return username + " is LIVE on Twitch!\n" +
                "Stream Title: " + stream.getTitle() + "\n" +
                "Watch: https://www.twitch.tv/" + username;
    }

    private String formatYoutubeLiveResults(List<SearchResult> results) {
        StringBuilder liveStreams = new StringBuilder("This YouTuber is live!\n");
        for (SearchResult result : results) {
            liveStreams.append("Live Stream Title: ")
                    .append(result.getSnippet().getTitle()).append("\n")
                    .append("Watch here: https://www.youtube.com/watch?v=")
                    .append(result.getId().getVideoId()).append("\n");
        }
        return liveStreams.toString();
    }
}