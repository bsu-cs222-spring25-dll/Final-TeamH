package edu.bsu.cs.scheduledstreams;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.channelid.YoutubeUserIdProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YoutubeScheduleProvider {
    private final ApiContext context;
    private final YoutubeUserIdProvider idProvider;

    public YoutubeScheduleProvider(ApiContext context) {
        this.context = context;
        this.idProvider = new YoutubeUserIdProvider(context);
    }

    public List<SearchResult> fetchScheduledStreams(String username) throws IOException {
        String userId = idProvider.getUserId(username);
        if (userId == null) return Collections.emptyList();

        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setEventType("upcoming")
                .setOrder("date")
                .setMaxResults(10L);

        SearchListResponse response = request.execute();
        return response.getItems() != null ? response.getItems() : Collections.emptyList();
    }
}
