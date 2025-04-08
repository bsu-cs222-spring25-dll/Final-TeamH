package edu.bsu.cs.streams;

import edu.bsu.cs.ApiContext;
import edu.bsu.cs.channelid.YoutubeUserIdProvider;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YoutubeStreamsProvider {
    private final ApiContext context;

    public YoutubeStreamsProvider(ApiContext context) {
        this.context = context;
    }

    public String getYoutubeStreams(String username) throws IOException {
        String userId = getUserIdForStreams(username);
        if (userId == null) {
            return "Error: Could not retrieve YouTube Channel ID for " + username;
        }
        try {
            List<SearchResult> streams = fetchCompletedStreams(userId);
            if (streams.isEmpty()) {
                return "No recent streams found for " + username;
            }
            return formatStreamList(streams);
        } catch (Exception e) {
            return "Error retrieving YouTube streams: " + e.getMessage();
        }
    }

    private String getUserIdForStreams(String username) throws IOException {
        YoutubeUserIdProvider youtubeIdProvider = new YoutubeUserIdProvider(context);
        return youtubeIdProvider.getUserId(username);
    }

    private List<SearchResult> fetchCompletedStreams(String userId) throws IOException {
        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setEventType("completed")
                .setOrder("date")
                .setMaxResults(10L);

        SearchListResponse response = request.execute();
        if (response.getItems() == null) {
            return Collections.emptyList();
        }
        return response.getItems();
    }

    private String formatStreamList(List<SearchResult> streams) {
        StringBuilder builder = new StringBuilder("\nStart of list:\n");
        int index = 1;
        for (SearchResult stream : streams) {
            builder.append(index++).append(". ")
                    .append(stream.getSnippet().getTitle()).append("\n")
                    .append("Published At: ").append(stream.getSnippet().getPublishedAt()).append("\n")
                    .append("Watch here: https://www.youtube.com/watch?v=")
                    .append(stream.getId().getVideoId()).append("\n")
                    .append("----------------------\n");
        }
        return builder.toString();
    }
}
