package edu.bsu.cs;

import java.util.Scanner;
import java.util.List;

public class Main {

    private static StreamerSearchService searchService;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            int choice = displayMenu(scanner);

            switch(choice) {
                case 1 -> searchTwitch(scanner);
                case 2 -> searchYoutube(scanner);
                case 3 -> {
                    System.out.println("Exiting Program...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice! Please enter 1, 2, or 3.");
            }

        }


    }

    public static int displayMenu(Scanner scanner) {

        System.out.println("\n===== Streamer Search Service =====");
        System.out.println("1. Search for a Twitch Streamer");
        System.out.println("2. Search for a Youtuber");
        System.out.print("3. Exit\n>>");

        return scanner.nextInt();
    }

    private static void searchTwitch(Scanner scanner) {
        if(searchService == null) {
            searchService = new StreamerSearchService(ApiInitializer.initializeTwitch(), null, ApiInitializer.TwitchAuthToken, null);
        }

        scanner.nextLine();
        System.out.print("Enter Twitch Username: \n>>");
        String twitchUsername = scanner.nextLine();
        List<String> twitchResult = searchService.searchTwitchStreamer(twitchUsername);

        if(twitchResult == null || twitchResult.isEmpty()) {
            System.out.println("Twitch streamer not found.");
        } else {
            System.out.println("Twitch streamer found: " + twitchResult.get(0));
        }

    }

    private static void searchYoutube(Scanner scanner) {

    }
}