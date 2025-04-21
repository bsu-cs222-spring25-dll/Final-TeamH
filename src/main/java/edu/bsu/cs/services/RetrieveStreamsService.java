package edu.bsu.cs.services;

import com.github.twitch4j.helix.domain.VideoList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrieveStreamsService {

    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveStreamsService(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public ArrayList<String> getTwitchStreamsInfo(String username){
        ArrayList<String> twitchStreamsInfo = new ArrayList<>();
        String userId = obtainStreamerID.getTwitchUserId(username);
        try {
            VideoList resultList = context.twitchClient.getHelix()
                    .getVideos(context.twitchAuthToken, null, userId, null, null, null, null, null, 10, null, null)
                    .execute();
            if (resultList == null || resultList.getVideos().isEmpty()) {
                return null;
            }
            StringBuilder streamInfo = new StringBuilder();
            for (var video : resultList.getVideos()) {
                streamInfo.append(video.getTitle())
                        .append("__")
                        .append(video.getId())
                        .append("__")
                        .append(video.getThumbnailUrl(120,90));
                twitchStreamsInfo.add(String.valueOf(streamInfo));
                streamInfo.delete(0,1000);
            }
            return twitchStreamsInfo;
        } catch (Exception e){
            return null;
        }
    }

    public String getYoutubeStreams(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
        if (userId == null) return "Error: Could not retrieve YouTube Channel ID for " + username;

        try {
            List<SearchResult> streams = fetchCompletedStreamsById(userId);
            if (streams.isEmpty()) {
                return "No recent streams found for " + username;
            }
            return formatStreamList(streams);
        } catch (Exception e) {
            return "Error retrieving YouTube streams: " + e.getMessage();
        }
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
        return response.getItems() != null ? response.getItems() : Collections.emptyList();
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

    public List<SearchResult> fetchCompletedStreams(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
        if (userId == null) return Collections.emptyList();
        return fetchCompletedStreamsById(userId);
    }

    public String getFormattedTwitchVODs(String username) {
        ArrayList<String> vods = getTwitchStreamsInfo(username);
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

}