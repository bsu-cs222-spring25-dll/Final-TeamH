package edu.bsu.cs;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ClipList;

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
                    broadcasterId,
                    null,
                    null,
                    null,
                    null,
                    10,
                    Instant.now().minus(Duration.ofDays(7)),
                    null,
                    false
            ).execute();
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
            return twitchClipsInfo;
        } catch (Exception e) {
            return null;
        }
    }
}
