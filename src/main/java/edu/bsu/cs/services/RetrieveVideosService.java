package edu.bsu.cs.services;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrieveVideosService {
    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveVideosService(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public String getYoutubeVideos(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
        if (userId == null)
            return "Error: Could not retrieve YouTube Channel ID for " + username;

        try {
            List<SearchResult> videos = fetchRecentVideosById(userId);
            if (videos.isEmpty()) {
                return "No recent videos found for " + username;
            }
            return formatVideoList(videos);
        } catch (Exception e) {
            return "Error retrieving YouTube videos: " + e.getMessage();
        }
    }

    public List<SearchResult> fetchRecentVideosById(String userId) throws IOException {
        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setOrder("date")
                .setMaxResults(10L);

        SearchListResponse response = request.execute();
        return response.getItems() != null ? response.getItems() : Collections.emptyList();
    }

    private String formatVideoList(List<SearchResult> videos) {
        StringBuilder builder = new StringBuilder("\nRecent Videos:\n");
        int index = 1;
        for (SearchResult video : videos) {
            builder.append(index++).append(". ").append(video.getSnippet().getTitle()).append("\n")
                    .append("Published At: ").append(video.getSnippet().getPublishedAt()).append("\n")
                    .append("Watch here: https://www.youtube.com/watch?v=")
                    .append(video.getId().getVideoId()).append("\n")
                    .append("----------------------\n");
        }
        return builder.toString();
    }

    public List<SearchResult> fetchRecentVideos(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
        if (userId == null) return Collections.emptyList();
        return fetchRecentVideosById(userId);
    }

    public ObtainStreamerID getObtainStreamerID() {
        return obtainStreamerID;
    }
}