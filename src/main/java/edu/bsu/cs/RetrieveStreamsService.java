package edu.bsu.cs;

import com.github.twitch4j.helix.domain.VideoList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.channelid.TwitchUserIdProvider;
import edu.bsu.cs.channelid.YoutubeUserIdProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrieveStreamsService {

    private final ApiContext context;

    public RetrieveStreamsService(ApiContext context) {
        this.context = context;
    }

    public ArrayList<String> getTwitchStreamsInfo(String username){
        ArrayList<String> twitchStreamsInfo = new ArrayList<>();
        TwitchUserIdProvider twitchIdProvider = new TwitchUserIdProvider(context);
        String userId = twitchIdProvider.getUserId(username);
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
        String userId = getUserIdForStreams(username);
        if (userId == null) return "Error: Could not retrieve YouTube Channel ID for " + username;

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

    public List<SearchResult> fetchCompletedStreams(String username) throws IOException {
        String userId = getUserIdForStreams(username);
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
