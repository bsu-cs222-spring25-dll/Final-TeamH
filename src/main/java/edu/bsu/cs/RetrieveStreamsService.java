package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.VideoList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RetrieveStreamsService {

    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final String youtubeApiKey;
    private final YouTube youtubeService;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveStreamsService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeService = youtubeService;
        this.obtainStreamerID = new ObtainStreamerID(twitchClient, youtubeService, twitchAuthToken, youtubeApiKey);
    }

    public void getTwitchStreams(String username) {
        String userId = obtainStreamerID.getTwitchUserId(username);

        if (userId == null) {
            System.out.println("Error: Could not retrieve user ID for " + username);
            return;
        }

        VideoList resultList = twitchClient.getHelix()
                .getVideos(twitchAuthToken, null, userId, null, null, null, null, null, 10, null, null)
                .execute();

        if (resultList == null || resultList.getVideos().isEmpty()) {
            System.out.println("No recent streams found for " + username);
            return;
        }

        System.out.println("\nStart of list:");
        final int[] i = {0};
        resultList.getVideos().forEach(video -> {
            i[0]++;
            System.out.println(i[0] + ". " + video.getTitle() + " - " + video.getUserName());
        });
    }

    public void getYoutubeStreams(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);

        if (userId == null) {
            System.out.println("Error: Could not retrieve YouTube Channel ID for " + username);
            return;
        }

        YouTube.Search.List pastStreamsRequest = youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(youtubeApiKey)
                .setChannelId(userId)
                .setType(Arrays.asList("video"))
                .setEventType("completed")
                .setOrder("date") // Order by date
                .setMaxResults(10L); // Fetch up to 10 results

        SearchListResponse pastStreamsInformation = pastStreamsRequest.execute();
        List<SearchResult> results = pastStreamsInformation.getItems();

        if (results.isEmpty()) {
            System.out.println("No recent streams found for " + username);
            return;
        }

        System.out.println("\nStart of list:");
        final int[] i = {0};
        results.forEach(result -> {
            i[0]++;
            System.out.println(i[0] + ". " + result.getSnippet().getTitle()+"\n" + "Published At: " + result.getSnippet().getPublishedAt());
            System.out.println("Watch here: https://www.youtube.com/watch?v=" + result.getId().getVideoId());
            System.out.println("----------------------");
        });
    }
}
