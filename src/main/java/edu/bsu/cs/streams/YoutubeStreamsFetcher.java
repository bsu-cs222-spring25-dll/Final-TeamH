package edu.bsu.cs.streams;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.channelid.YoutubeUserIdProvider;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YoutubeStreamsFetcher {
    private final ApiContext context;
    private final YoutubeUserIdProvider idProvider;

    public YoutubeStreamsFetcher(ApiContext context) {
        this.context = context;
        this.idProvider = new YoutubeUserIdProvider(context);
    }

    public List<SearchResult> fetchCompletedStreams(String username) throws IOException {
        String userId = idProvider.getUserId(username);
        if (userId == null) return Collections.emptyList();

        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setEventType("completed")
                .setOrder("date")
                .setMaxResults(10L);

        SearchListResponse response = request.execute();
        return response.getItems() != null ? response.getItems() : Collections.emptyList();
    }
}
