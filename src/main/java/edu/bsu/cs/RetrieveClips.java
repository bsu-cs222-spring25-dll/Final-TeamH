package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Clip;
import com.github.twitch4j.helix.domain.ClipList;

import java.util.List;

public class RetrieveClips {

    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveClips(ITwitchClient twitchClient, String twitchAuthToken) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.obtainStreamerID = new ObtainStreamerID(twitchClient, null, twitchAuthToken, null);
    }

    public void getTwitchClips(String username) {
        String broadcasterId = obtainStreamerID.getTwitchUserId(username);

        if (broadcasterId == null || broadcasterId.isEmpty()) {
            System.out.println("Error: Could not retrieve user ID for " + username);
            return;
        }

        System.out.println("Fetched Broadcaster ID: " + broadcasterId);

        try {
            TwitchHelix helix = twitchClient.getHelix();

            ClipList clipList = helix.getClips(
                    twitchAuthToken,
                    broadcasterId,
                    null,
                    null,
                    null,
                    null,
                    10,
                    null,
                    null,
                    null
            ).execute();

            List<Clip> clips = clipList.getData();
            if (clips.isEmpty()) {
                System.out.println("No clips found for " + username);
                return;
            }

            System.out.println("\nTop Clips for " + username + ":");
            int index = 0;
            for (Clip clip : clips) {
                index++;
                System.out.println(index + ". " + clip.getTitle());
                System.out.println("    URL: " + clip.getUrl());
                System.out.println("    Created by: " + clip.getCreatorName());
                System.out.println("    Views: " + clip.getViewCount());
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve clips: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
