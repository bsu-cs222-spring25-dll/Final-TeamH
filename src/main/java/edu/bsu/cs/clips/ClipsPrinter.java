package edu.bsu.cs.clips;

import com.github.twitch4j.helix.domain.Clip;
import java.util.List;

public class ClipsPrinter {
    public static void printClips(List<Clip> clips, String username) {
        if (clips == null || clips.isEmpty()) {
            System.out.println("No clips found for " + username);
            return;
        }
        System.out.println("\nTop Clips for " + username + ":");
        int index = 1;
        for (Clip clip : clips) {
            System.out.println(index++ + ". " + clip.getTitle());
            System.out.println("    URL: " + clip.getUrl());
            System.out.println("    Created by: " + clip.getCreatorName());
            System.out.println("    Views: " + clip.getViewCount());
            System.out.println();
        }
    }
}
