package edu.bsu.cs;

import java.util.List;

public class GUISearchHandler {
    private final StreamerSearchService searchService;

    public GUISearchHandler(StreamerSearchService searchService) {
        this.searchService = searchService;
    }

    public String GUISearchStreamer(String username, String platform) {
        try {
            List<String> result;
            if (platform.equalsIgnoreCase("Twitch")) {
                result = searchService.searchTwitchStreamer(username);
            } else if (platform.equalsIgnoreCase("YouTube")) {
                result = searchService.searchYoutubeStreamer(username);
            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platform);
            }

            if (result == null || result.isEmpty()) {
                System.out.println(platform + " streamer not found.");
                return "";
            }

            System.out.println(platform + " streamer found: " + result.get(0));
            return result.get(0);

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid platform: " + e.getMessage());
            return "";
        } catch (Exception e) {
            System.err.println("Error searching for " + platform + " user: " + e.getMessage());
            return "";
        }
    }
}
