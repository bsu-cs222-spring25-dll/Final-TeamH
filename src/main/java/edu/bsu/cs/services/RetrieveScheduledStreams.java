package edu.bsu.cs.services;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ScheduledSegment;
import com.github.twitch4j.helix.domain.StreamSchedule;
import com.github.twitch4j.helix.domain.StreamScheduleResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;

import java.io.IOException;
import java.util.*;

public class RetrieveScheduledStreams {

    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveScheduledStreams(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public List<String> getTwitchScheduledStreams(String username) {
        String broadcasterId = obtainStreamerID.getTwitchUserId(username);
        if (broadcasterId == null || broadcasterId.isEmpty()) return null;

        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            StreamScheduleResponse response = helix.getChannelStreamSchedule(
                    context.twitchAuthToken, broadcasterId,
                    null, null, null, null, 5
            ).execute();

            StreamSchedule schedule = response.getSchedule();
            List<ScheduledSegment> segments = schedule != null ? schedule.getSegments() : Collections.emptyList();
            return formatTwitchSegments(segments);
        } catch (Exception e) {
            return null;
        }
    }

    public List<SearchResult> fetchYoutubeScheduledStreams(String username) throws IOException {
        String userId = obtainStreamerID.getYoutubeUserId(username);
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
        return (response != null && response.getItems() != null) ? response.getItems() : Collections.emptyList();
    }

    private List<String> formatTwitchSegments(List<ScheduledSegment> segments) {
        if (segments == null || segments.isEmpty()) return null;

        List<String> formatted = new ArrayList<>();
        for (ScheduledSegment segment : segments) {
            String entry = String.join("__",
                    segment.getTitle(),
                    segment.getId(),
                    String.valueOf(segment.getStartTime()),
                    String.valueOf(segment.getEndTime()));
            formatted.add(entry);
        }
        return formatted;
    }
}