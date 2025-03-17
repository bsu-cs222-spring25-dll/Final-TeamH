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
        int choice = -1;
        while(true) {
            System.out.println("\n===== Streamer Search Service =====");
            System.out.println("1. Search for a Twitch Streamer");
            System.out.println("2. Search for a Youtuber");
            System.out.print("3. Exit\n>>");

            if(scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if(choice >= 1 && choice <= 3) {
                    break;
                } else {
                    System.out.println("Invalid choice! Please enter 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid choice! Please enter a number between 1 and 3.");
                scanner.next();
            }
        }
        return choice;
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
        if(searchService == null) {
            searchService = new StreamerSearchService(null, ApiInitializer.initializeYoutube(), null, ApiInitializer.YoutubeAuthToken);
        }

        scanner.nextLine();
        System.out.print("Enter Youtube Username: \n>>");
        String youtubeUsername = scanner.nextLine();

        try {
            List<String> youtubeResult = searchService.searchYoutubeStreamer(youtubeUsername);

            if(youtubeResult == null || youtubeResult.isEmpty()) {
                System.out.println("Youtuber not found.");
            } else {
                System.out.println("Youtube Streamer Found: " + youtubeResult.get(0));
            }
        } catch (Exception e) {
            System.out.println("Youtuber not found.");
        }

    }
}