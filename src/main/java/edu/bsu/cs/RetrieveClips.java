package edu.bsu.cs;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Clip;
import com.github.twitch4j.helix.domain.ClipList;
import edu.bsu.cs.api.ApiContext;


import java.util.ArrayList;
import java.util.List;

public class RetrieveClips {

    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public RetrieveClips(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    public ArrayList<String> getTwitchClips(String username) {
        String broadcasterId = obtainStreamerID.getTwitchUserId(username);

        if (broadcasterId == null || broadcasterId.isEmpty()) {
            System.out.println("Error: Could not retrieve user ID for " + username);
            return null;
        }

        System.out.println("Fetched Broadcaster ID: " + broadcasterId);

        try {
            TwitchHelix helix = context.twitchClient.getHelix();

            ClipList clipList = helix.getClips(
                    context.twitchAuthToken,
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
                return null;
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
        return null;
    }
}