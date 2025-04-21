package edu.bsu.cs.services;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ClipList;
import edu.bsu.cs.api.ApiContext;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RetrieveClips {

    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveClips(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public List<String> getTwitchClipsInfo(String username) {
        String broadcasterId = obtainStreamerID.getTwitchUserId(username);
        if (broadcasterId == null) return null;

        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            ClipList clips = helix.getClips(
                    context.twitchAuthToken,
                    broadcasterId, null, null, null, null,
                    10, Instant.now().minus(Duration.ofDays(7)), null, false
            ).execute();

            return clips == null || clips.getData().isEmpty()
                    ? null
                    : formatClips(clips);
        } catch (Exception e) {
            return null;
        }
    }

    public String getFormattedTwitchClips(String username) {
        List<String> clips = getTwitchClipsInfo(username);
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

    private List<String> formatClips(ClipList clips) {
        List<String> formatted = new ArrayList<>();
        for (var clip : clips.getData()) {
            String entry = String.join("__",
                    clip.getTitle(),
                    clip.getId(),
                    clip.getThumbnailUrl());
            formatted.add(entry);
        }
        return formatted;
    }
}
