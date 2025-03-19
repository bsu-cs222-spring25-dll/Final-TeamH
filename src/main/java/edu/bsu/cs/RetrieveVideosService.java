package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrieveVideosService {
    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final String youtubeApiKey;
    private final YouTube youtubeService;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveVideosService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeService = youtubeService;
        this.obtainStreamerID = new ObtainStreamerID(twitchClient, youtubeService, twitchAuthToken, youtubeApiKey);

    }

    public void getYoutubeVideos(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);

        if (userId == null) {
            System.out.println("Error: Could not retrieve YouTube Channel ID for " + username);
            return;
        }


        YouTube.Search.List pastVideosRequest = youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(youtubeApiKey)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setOrder("date") // Order by date
                .setMaxResults(10L); // Fetch up to 10 results

        SearchListResponse pastVideosInformation = pastVideosRequest.execute();
        List<SearchResult> results = pastVideosInformation.getItems();

        if (results.isEmpty()) {
            System.out.println("No recent videos found for " + username);
            return;
        }

        System.out.println("\nStart of list:");
        final int[] i = {0};
        results.forEach(result -> {
            i[0]++;
            System.out.println(i[0] + ". " + result.getSnippet().getTitle());
        });




    }
}
