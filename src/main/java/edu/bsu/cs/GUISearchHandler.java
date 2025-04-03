package edu.bsu.cs;

import java.util.List;

public class GUISearchHandler {
    private final StreamerSearchService searchService;

    public GUISearchHandler() {
        this.searchService = new StreamerSearchService(
                ApiInitializer.initializeTwitch(),
                ApiInitializer.initializeYoutube(),
                ApiInitializer.TwitchAuthToken,
                ApiInitializer.YoutubeAuthToken
        );
    }

    public String GUISearchStreamer(String platform, String username) {
        List<String> result;
        try {
            if (platform.equalsIgnoreCase("Twitch")) {
                result = searchService.searchTwitchStreamer(username);
            } else if (platform.equalsIgnoreCase("YouTube")) {
                result = searchService.searchYoutubeStreamer(username);
            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platform);
            }

            if (result.isEmpty()) {
                return "";
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            return "";
        }
    }
}
