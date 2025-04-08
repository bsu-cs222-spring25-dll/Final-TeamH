package edu.bsu.cs.streams;

import edu.bsu.cs.ApiContext;
import edu.bsu.cs.channelid.TwitchUserIdProvider;
import com.github.twitch4j.helix.domain.VideoList;

public class TwitchStreamsProvider {
    private final ApiContext context;

    public TwitchStreamsProvider(ApiContext context) {
        this.context = context;
    }

    public String getTwitchStreams(String username) {
        try {
            TwitchUserIdProvider twitchIdProvider = new TwitchUserIdProvider(context);
            String userId = twitchIdProvider.getUserId(username);
            if (userId == null) {
                return "Error: Could not retrieve user ID for " + username;
            }
            VideoList resultList = context.twitchClient.getHelix()
                    .getVideos(context.twitchAuthToken, null, userId, null, null, null, null, null, 10, null, null)
                    .execute();
            if (resultList == null || resultList.getVideos().isEmpty()) {
                return "No recent streams found for " + username;
            }
            StringBuilder streams = new StringBuilder("\nStart of list:\n");
            int index = 1;
            for (var video : resultList.getVideos()) {
                streams.append(index++).append(". ")
                        .append(video.getTitle())
                        .append(" - ")
                        .append(video.getUserName())
                        .append("\n");
            }
            return streams.toString();
        } catch (Exception e) {
            return "Error retrieving Twitch streams: " + e.getMessage();
        }
    }
}
