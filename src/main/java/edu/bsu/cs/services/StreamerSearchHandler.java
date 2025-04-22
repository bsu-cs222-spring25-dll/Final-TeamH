package edu.bsu.cs.services;

import java.util.List;
import java.util.Scanner;

public class StreamerSearchHandler {

    private final Scanner scanner;
    private final StreamerSearchService searchService;

    public StreamerSearchHandler(Scanner scanner, StreamerSearchService searchService) {
        this.scanner = scanner;
        this.searchService = searchService;
    }

    public String searchStreamer(String platform) {
        String username = promptForValidUsername(platform);
        List<String> results = fetchSearchResults(platform, username);
        return handleSearchResults(results, username);
    }

    private String promptForValidUsername(String platform) {
        scanner.nextLine();
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.contains(" ")) return input;
        }
    }

    private List<String> fetchSearchResults(String platform, String username) {
        try {
            return switch (platform.toLowerCase()) {
                case "twitch" -> searchService.searchTwitchStreamer(username);
                case "youtube" -> searchService.searchYoutubeStreamer(username);
                default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
            };
        } catch (Exception e) {
            return List.of();
        }
    }

    private String handleSearchResults(List<String> results, String username) {
        return results.isEmpty() ? "" : username;
    }
}