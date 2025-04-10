package edu.bsu.cs.livestatus;

import edu.bsu.cs.api.ApiContext;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.channelid.YoutubeUserIdProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YoutubeLiveStatusProvider implements LiveStatusProvider {
    private final ApiContext context;

    public YoutubeLiveStatusProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public String getLiveStatus(String username) throws IOException {
        YoutubeUserIdProvider idProvider = new YoutubeUserIdProvider(context);
        String userId = idProvider.getUserId(username);
        if (userId == null) {
            return "Error: Could not retrieve YouTube Channel ID for " + username;
        }

        YouTube.Search.List searchRequest = context.youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setEventType("live")
                .setType(Arrays.asList("video"));

        SearchListResponse searchResponse = searchRequest.execute();
        List<SearchResult> results = (searchResponse == null || searchResponse.getItems() == null)
                ? Collections.emptyList() : searchResponse.getItems();

        if (!results.isEmpty()) {
            StringBuilder liveStreams = new StringBuilder("This YouTuber is live!\n");
            for (SearchResult result : results) {
                liveStreams.append("Live Stream Title: ").append(result.getSnippet().getTitle()).append("\n")
                        .append("Watch here: https://www.youtube.com/watch?v=")
                        .append(result.getId().getVideoId()).append("\n");
            }
            return liveStreams.toString();
        } else {
            return "This YouTuber is NOT live.";
        }
    }
}
