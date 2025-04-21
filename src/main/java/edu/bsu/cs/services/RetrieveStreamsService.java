package edu.bsu.cs.services;

import com.github.twitch4j.helix.domain.VideoList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.*;

public class RetrieveStreamsService {

    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveStreamsService(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public List<String> getTwitchStreamsInfo(String username) {
        String userId = obtainStreamerID.getTwitchUserId(username);
        if (userId == null) return null;

        try {
            VideoList resultList = context.twitchClient.getHelix()
                    .getVideos(context.twitchAuthToken, null, userId,
                            null, null, null, null, null, 10, null, null)
                    .execute();

            if (resultList == null || resultList.getVideos().isEmpty()) return null;
            return formatTwitchVODList(resultList);
        } catch (Exception e) {
            return null;
        }
    }

    public String getFormattedTwitchVODs(String username) {
        List<String> vods = getTwitchStreamsInfo(username);
        if (vods == null || vods.isEmpty()) {
            return "No VODs found for " + username;
        }

        StringBuilder builder = new StringBuilder("Recent Twitch VODs:\n");
        int index = 1;
        for (String entry : vods) {
            String[] info = entry.split("__");
            if (info.length >= 3) {
                builder.append(index++).append(". Title: ").append(info[0]).append("\n")
                        .append("   Watch: https://www.twitch.tv/videos/").append(info[1]).append("\n")
                        .append("   Thumbnail: ").append(info[2]).append("\n\n");
            }
        }
        return builder.toString();
    }

    private List<String> formatTwitchVODList(VideoList resultList) {
        List<String> formatted = new ArrayList<>();
        for (var video : resultList.getVideos()) {
            String entry = String.join("__",
                    video.getTitle(),
                    video.getId(),
                    video.getThumbnailUrl(120, 90));
            formatted.add(entry);
        }
        return formatted;
    }

    public String getYoutubeStreams(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
        if (userId == null) return "Error: Could not retrieve YouTube Channel ID for " + username;

        try {
            List<SearchResult> streams = fetchCompletedStreamsById(userId);
            return streams.isEmpty()
                    ? "No recent streams found for " + username
                    : formatYoutubeStreams(streams);
        } catch (Exception e) {
            return "Error retrieving YouTube streams: " + e.getMessage();
        }
    }

    public List<SearchResult> fetchCompletedStreams(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
        return (userId == null) ? Collections.emptyList() : fetchCompletedStreamsById(userId);
    }

    public List<SearchResult> fetchCompletedStreamsById(String userId) throws IOException {
        YouTube.Search.List request = context.youtubeService.search()
                .list(Arrays.asList("id", "snippet"))
                .setKey(context.youtubeAuthToken)
                .setChannelId(userId)
                .setType(Collections.singletonList("video"))
                .setEventType("completed")
                .setOrder("date")
                .setMaxResults(10L);

        SearchListResponse response = request.execute();
        return (response != null && response.getItems() != null)
                ? response.getItems()
                : Collections.emptyList();
    }

    private String formatYoutubeStreams(List<SearchResult> streams) {
        StringBuilder builder = new StringBuilder("\nRecent YouTube Streams:\n");
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