package edu.bsu.cs.streamersearch;

import java.util.List;
import java.util.Scanner;

public class StreamerSearchHandler {

    private final Scanner scanner;
    private final StreamerSearchAggregator searchAggregator;

    public StreamerSearchHandler(Scanner scanner, StreamerSearchAggregator searchAggregator) {
        this.scanner = scanner;
        this.searchAggregator = searchAggregator;
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

        try {
            List<String> result = searchAggregator.search(platform, username);
            if (result.isEmpty()) {
                System.out.println(platform + " streamer not found.");
                return "";
            } else {
                System.out.println(platform + " streamer found: " + result.get(0));
                return username;
            }
        } catch (Exception e) {
            System.err.println(platform + " user not found (crash prevention): " + e.getMessage());
            return "";
        }
    }
}
