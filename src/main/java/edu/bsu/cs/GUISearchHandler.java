package edu.bsu.cs;

import java.util.List;

public class GUISearchHandler {
    private final StreamerSearchService searchService;

    public GUISearchHandler(StreamerSearchService searchService) {
        this.searchService = searchService;
    }

    public boolean GUISearchStreamer(String username) {
        try {
            List<String> twitchResults = searchService.searchTwitchStreamer(username);
            if (!twitchResults.isEmpty()) {
                System.out.println("Twitch streamer found: " + username);
                return true;
            }

            List<String> youtubeResults = searchService.searchYoutubeStreamer(username);
            if (!youtubeResults.isEmpty()) {
                System.out.println("YouTube streamer found: " + username);
                return true;
            }

            System.out.println("Streamer not found.");
            return false;
        } catch (Exception e) {
            System.err.println("Error searching for streamer: " + e.getMessage());
            return false;
        }
    }
}
