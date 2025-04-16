package edu.bsu.cs;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ClipList;
import edu.bsu.cs.api.ApiContext;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;


public class RetrieveClips {

    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveClips(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public ArrayList<String> getTwitchClipsInfo(String username) {
        ArrayList<String> twitchClipsInfo = new ArrayList<>();
        String broadcasterId = obtainStreamerID.getTwitchUserId(username);
        System.out.println("Fetched Broadcaster ID: " + broadcasterId);
        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            ClipList clips = helix.getClips(
                    context.twitchAuthToken,
                    broadcasterId, null, null, null, null, 10, Instant.now().minus(Duration.ofDays(7)), null, false).execute();
            StringBuilder clipInfo = new StringBuilder();
            clips.getData().forEach(clip -> {
                clipInfo.append(clip.getTitle())
                        .append("__")
                        .append(clip.getId())
                        .append("__")
                        .append(clip.getThumbnailUrl());
                twitchClipsInfo.add(String.valueOf(clipInfo));
                clipInfo.delete(0,1000);
            });
            if(twitchClipsInfo.isEmpty()){
                return null;
            }
            return twitchClipsInfo;
        } catch (Exception e) {
            return null;
        }
    }

    public String getFormattedTwitchClips(String username) {
        ArrayList<String> clips = getTwitchClipsInfo(username);
        if (clips == null || clips.isEmpty()) {
            return "No clips found for " + username;
        }
        StringBuilder builder = new StringBuilder("Recent Twitch Clips:\n");
        int index = 1;
        for (String entry : clips) {
            String[] info = entry.split("__");
            if (info.length >= 3) {
                builder.append(index++).append(". Title: ").append(info[0]).append("\n")
                        .append("   Watch: https://clips.twitch.tv/").append(info[1]).append("\n")
                        .append("   Thumbnail: ").append(info[2]).append("\n\n");
            }
        }
        return builder.toString();
    }
}