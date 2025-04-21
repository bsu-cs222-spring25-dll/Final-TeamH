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

        return handleSearchResults(results, platform, username);
    }

    private String promptForValidUsername(String platform) {
        scanner.nextLine();
        while (true) {
            System.out.print("Enter " + platform + " Username (no spaces allowed): \n>> ");
            String input = scanner.nextLine().trim();
            if (!input.contains(" ")) return input;

            System.out.println("Error: Username cannot contain spaces. Please try again.");
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
            System.err.println(platform + " user not found (crash prevention)");
            return List.of();
        }
    }

    private String handleSearchResults(List<String> results, String platform, String username) {
        if (results.isEmpty()) {
            System.out.println(platform + " streamer not found.");
            return "";
        }
        System.out.println(platform + " streamer found: " + results.get(0));
        return username;
    }
}