package edu.bsu.cs;

import com.github.twitch4j.helix.domain.VideoList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrieveStreamsService {

    private final ApiContext context;

    public RetrieveStreamsService(ApiContext context) {
        this.context = context;
    }

    public String getTwitchStreams(String username) {
        ObtainStreamerID getID = new ObtainStreamerID(context);
        String userId = getID.getTwitchUserId(username);

        if (userId == null) {
            return "Error: Could not retrieve user ID for " + username;
        }

        try {
            VideoList resultList = context.twitchClient.getHelix()
                    .getVideos(context.twitchAuthToken, null, userId, null, null, null, null, null, 10, null, null)
                    .execute();

            if (resultList == null || resultList.getVideos().isEmpty()) {
                return "No recent streams found for " + username;
            }

            StringBuilder streams = new StringBuilder("\nStart of list:\n");
            int index = 1;
            for (var video : resultList.getVideos()) {
                streams.append(index++).append(". ").append(video.getTitle())
                        .append(" - ").append(video.getUserName()).append("\n");
            }
            return streams.toString();
        } catch (Exception e) {
            return "Error retrieving Twitch streams: " + e.getMessage();
        }
    }

    public String getYoutubeStreams(String username) throws IOException {
        String userId = getUserIdForStreams(username);
        if (userId == null) return "Error: Could not retrieve YouTube Channel ID for " + username;

        try {
            List<SearchResult> streams = fetchCompletedStreams(userId);
            if (streams == null || streams.isEmpty()) {
                return "No recent streams found for " + username;
            }
            return formatStreamList(streams);
        } catch (Exception e) {
            return "Error retrieving YouTube streams: " + e.getMessage();
        }
    }

    private String getUserIdForStreams(String username) throws IOException {
        ObtainStreamerID youtubeIdProvider = new ObtainStreamerID(context);
        return youtubeIdProvider.getYoutubeUserId(username);
    }

    public List<SearchResult> fetchCompletedStreams(String userId) throws IOException {
        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setEventType("completed")
                .setOrder("date")
                .setMaxResults(10L);

        SearchListResponse response = request.execute();

        if (response == null || response.getItems() == null) {
            System.err.println("ERROR: YouTube API returned null or no items for completed streams.");
            return Collections.emptyList();
        }

        return response.getItems();
    }

    private String formatStreamList(List<SearchResult> streams) {
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
}