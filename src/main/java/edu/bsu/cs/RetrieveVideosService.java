package edu.bsu.cs;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrieveVideosService {
    private final String youtubeApiKey;
    private final YouTube youtubeService;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveVideosService(YouTube youtubeService, String youtubeApiKey) {
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeService = youtubeService;
        this.obtainStreamerID = new ObtainStreamerID(null, youtubeService, null, youtubeApiKey);

    }


    public String getYoutubeVideos(String username) throws IOException {
        String userId = getUserIdForVideos(username);
        if (userId == null) return "Error: Could not retrieve YouTube Channel ID for " + username;

        try {
            List<SearchResult> streams = fetchRecentVideos(userId);
            if (streams.isEmpty()) {
                return "No recent videos found for " + username;
            }

            return formatVideoList(streams);
        } catch (Exception e) {
            return "Error retrieving YouTube videos: " + e.getMessage();
        }
    }
    public List<SearchResult> fetchRecentVideos(String username) throws IOException {
        String userId = getUserIdForVideos(username); // or your own logic
        if (userId == null) return Collections.emptyList();

        YouTube.Search.List request = youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(youtubeApiKey)
                .setChannelId(userId)
                .setType(Collections.singletonList("video")) // Gets all videos
                .setOrder("date") // Sorted by newest
                .setMaxResults(10L); // Adjust as needed

        SearchListResponse response = request.execute();
        return response.getItems();
    }

    private String formatVideoList(List<SearchResult> streams) {
        StringBuilder builder = new StringBuilder("\nStart of list:\n");
        int index = 1;
        for (SearchResult stream : streams) {
            builder.append(index++).append(". ").append(stream.getSnippet().getTitle()).append("\n")
                    .append("Published At: ").append(stream.getSnippet().getPublishedAt()).append("\n")
                    .append("Watch here: https://www.youtube.com/watch?v=")
                    .append(stream.getId().getVideoId()).append("\n")
                    .append("----------------------\n");
        }
        return builder.toString();
    }

    private String getUserIdForVideos(String username) throws IOException {
        return obtainStreamerID.getYoutubeUserId(username);
    }
}
