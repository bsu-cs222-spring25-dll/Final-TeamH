package edu.bsu.cs;

import java.util.Scanner;
import java.util.List;

public class StreamerSearchHandler {

    private final Scanner scanner;
    private final StreamerSearchService searchService;

    public StreamerSearchHandler(Scanner scanner, StreamerSearchService searchService) {
        this.scanner = scanner;
        this.searchService = searchService;
    }

    public String getValidatedUsername(String platform) {
        scanner.nextLine();
        String username;

        while (true) {
            System.out.print("Enter " + platform + " Username (no spaces allowed): \n>> ");
            username = scanner.nextLine().trim();

            if (username.contains(" ")) {
                System.out.println("Error: Username cannot contain spaces. Please try again.");
            } else {
                return username;
            }
        }
    }

    public String searchStreamer(String platform) {
        String username = getValidatedUsername(platform);

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
                System.out.println(platform + " streamer not found.");
                return "";
            } else {
                System.out.println(platform + " streamer found: " + result.get(0));
                return username;
            }
        } catch (Exception e) {
            System.err.println(platform + " user not found (crash prevention)");
            return "";
        }
    }
}