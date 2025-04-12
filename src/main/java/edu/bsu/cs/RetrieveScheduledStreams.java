package edu.bsu.cs;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrieveScheduledStreams {
    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveScheduledStreams(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public ArrayList<String> fetchScheduledStreams(String username) throws IOException {
        String userId = getUserIdForScheduled(username);
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
        return response.getItems();
    }

    private String formatScheduledStreamsList(List<SearchResult> ScheduledStreams) {
        StringBuilder builder = new StringBuilder("\nScheduledStreams:\n");
        int index = 1;
        for (SearchResult ScheduledStream : ScheduledStreams) {
            builder.append(index++).append(". ").append(ScheduledStream.getSnippet().getTitle()).append("\n")
                    .append("Published At: ").append(ScheduledStream.getSnippet().getPublishedAt()).append("\n")
                    .append("Watch here: https://www.youtube.com/watch?v=")
                    .append(ScheduledStream.getId().getVideoId()).append("\n")
                    .append("----------------------\n");
        }
        return builder.toString();
    }

    private String getUserIdForScheduled(String username) throws IOException {
        return obtainStreamerID.getYoutubeUserId(username);
    }
}
