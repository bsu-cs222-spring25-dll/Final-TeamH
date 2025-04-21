package edu.bsu.cs.services;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TopYoutubeStreams {

    private final ApiContext context;

    public TopYoutubeStreams(ApiContext context) {
        this.context = context;
    }

    public List<SearchResult> fetchRandomStreamsById() throws IOException {
        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setType(Collections.singletonList("video"))
                .setEventType("live")
                .setOrder("viewCount")
                .setMaxResults(10L);

        SearchListResponse response = request.execute();
        return response.getItems() != null ? response.getItems() : Collections.emptyList();
    }
}