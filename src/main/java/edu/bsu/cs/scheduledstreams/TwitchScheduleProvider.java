package edu.bsu.cs.scheduledstreams;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ScheduledSegment;
import com.github.twitch4j.helix.domain.StreamSchedule;
import com.github.twitch4j.helix.domain.StreamScheduleResponse;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.channelid.TwitchUserIdProvider;

import java.util.ArrayList;
import java.util.List;

public class TwitchScheduleProvider {
    private final ApiContext context;
    private final TwitchUserIdProvider idProvider;

    public TwitchScheduleProvider(ApiContext context) {
        this.context = context;
        this.idProvider = new TwitchUserIdProvider(context);
    }

    public List<String> getStreamSchedule(String username) {
        List<String> scheduleInfo = new ArrayList<>();
        String broadcasterId = idProvider.getUserId(username);
        if (broadcasterId == null || broadcasterId.isEmpty()) return scheduleInfo;

        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            StreamScheduleResponse response = helix.getChannelStreamSchedule(
                    context.twitchAuthToken,
                    broadcasterId,
                    null, null, null, null, 5
            ).execute();

            StreamSchedule schedule = response.getSchedule();
            for (ScheduledSegment segment : schedule.getSegments()) {
                String segmentInfo = segment.getTitle() + "__"
                        + segment.getId() + "__"
                        + segment.getStartTime() + "__"
                        + segment.getEndTime();
                scheduleInfo.add(segmentInfo);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving Twitch schedule: " + e.getMessage());
        }

        return scheduleInfo;
    }
}
