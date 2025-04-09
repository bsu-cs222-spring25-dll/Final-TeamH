package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.channelid.TwitchUserIdProvider;
import com.github.twitch4j.helix.domain.Video;
import com.github.twitch4j.helix.domain.VideoList;

import java.util.ArrayList;

public class TwitchStreamsFetcher {
    private final ApiContext context;
    private final TwitchUserIdProvider idProvider;

    public TwitchStreamsFetcher(ApiContext context) {
        this.context = context;
        this.idProvider = new TwitchUserIdProvider(context);
    }

    public ArrayList<String> fetchFormattedVODs(String username) {
        ArrayList<String> formatted = new ArrayList<>();
        try {
            String userId = idProvider.getUserId(username);
            if (userId == null) return null;

            VideoList resultList = context.twitchClient.getHelix()
                    .getVideos(context.twitchAuthToken, null, userId, null, null, null, null, null, 10, null, null)
                    .execute();

            for (Video video : resultList.getVideos()) {
                String info = video.getTitle() + "__" +
                        video.getId() + "__" +
                        video.getThumbnailUrl();
                formatted.add(info);
            }
        } catch (Exception e) {
            System.err.println("Error fetching Twitch VODs: " + e.getMessage());
            return null;
        }
        return formatted;
    }
}
