package edu.bsu.cs;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ScheduledSegment;
import com.github.twitch4j.helix.domain.StreamSchedule;
import com.github.twitch4j.helix.domain.StreamScheduleResponse;
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
    public ArrayList<String> getStreamSchedule (String username){
        ArrayList<String> scheduleInfo = new ArrayList<>();
        String broadcasterId = obtainStreamerID.getTwitchUserId(username);

        if (broadcasterId == null || broadcasterId.isEmpty()) {
            System.out.println("Error: Could not retrieve user ID for " + username);
            return null;
        }
        System.out.println("Fetched Broadcaster ID: " + broadcasterId);

        try{
            TwitchHelix helix = context.twitchClient.getHelix();
            StreamScheduleResponse scheduleResponse = helix.getChannelStreamSchedule(
                            context.twitchAuthToken,
                            broadcasterId,
                            null,
                            null,
                            null,
                            null,
                            5)
                    .execute();
            StreamSchedule schedule = scheduleResponse.getSchedule();
            List<ScheduledSegment> segments = schedule.getSegments();
            if (segments.isEmpty()){
                return null;
            }
            StringBuilder segmentInfo = new StringBuilder();
            for (ScheduledSegment segment: segments){
                segmentInfo.append(segment.getTitle())
                        .append("__")
                        .append(segment.getId())
                        .append("__")
                        .append(segment.getStartTime())
                        .append("__")
                        .append(segment.getEndTime());
                scheduleInfo.add(String.valueOf(segmentInfo));
                segmentInfo.delete(0,1000);
            }
            return scheduleInfo;
        }catch (Exception e) {
            return null;
        }
    }

    public List<SearchResult> fetchScheduledStreams(String username) throws IOException {
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

    private String getUserIdForScheduled(String username) throws IOException {
        return obtainStreamerID.getYoutubeUserId(username);
    }
}