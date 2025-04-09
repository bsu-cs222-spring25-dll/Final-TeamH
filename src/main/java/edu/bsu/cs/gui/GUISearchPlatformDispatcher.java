package edu.bsu.cs.gui;

import edu.bsu.cs.streamersearch.StreamerSearchAggregator;
import java.util.Collections;
import java.util.List;

public class GUISearchPlatformDispatcher {
    private final StreamerSearchAggregator searchService;

    public GUISearchPlatformDispatcher(StreamerSearchAggregator searchService) {
        this.searchService = searchService;
    }

    public List<String> dispatchSearch(String username, String platform) {
        return switch (platform.toLowerCase()) {
            case "twitch" -> searchService.search("twitch", username);
            case "youtube" -> searchService.search("youtube", username);
            default -> {
                System.err.println("Unsupported platform: " + platform);
                yield Collections.emptyList();
            }
        };
    }
}
