package edu.bsu.cs.videos;

import edu.bsu.cs.ApiContext;
import edu.bsu.cs.channelid.YoutubeUserIdProvider;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YoutubeVideosProvider {
    private final ApiContext context;
    private final YoutubeUserIdProvider idProvider;

    public YoutubeVideosProvider(ApiContext context) {
        this.context = context;
        this.idProvider = new YoutubeUserIdProvider(context);
    }

    public List<SearchResult> fetchRecentVideos(String username) throws IOException {
        String userId = idProvider.getUserId(username);
        if (userId == null) {
            return Collections.emptyList();
        }
        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setOrder("date")
                .setMaxResults(10L);
        SearchListResponse response = request.execute();
        if (response.getItems() == null) {
            return Collections.emptyList();
        }
        return response.getItems();
    }
}
