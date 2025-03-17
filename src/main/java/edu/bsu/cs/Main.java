package edu.bsu.cs;

import java.util.Scanner;
import java.util.List;

public class Main {

    private static StreamerSearchService searchService;
    private static StreamerStreamsService streamService;
    static String username = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        streamService = new StreamerStreamsService(ApiInitializer.initializeTwitch(), ApiInitializer.TwitchAuthToken);


        while(true) {
            int choice = displayMenu(scanner);

            switch(choice) {
                case 1 -> {
                    username = searchTwitch(scanner);
                    if(!username.isEmpty()) {
                        printTwitchMenu(scanner, username);
                    }
                }
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

    private static void printTwitchMenu(Scanner scanner, String username) {
        System.out.println("----- Twitch Services -----");
        System.out.println("1) Print the 10 most recent streams");
        System.out.println("2) View Live Status");
        System.out.println("3) View Past Streams");
        System.out.println("4) View Subscriber Count");
        System.out.println("5) View Bio Information");
        System.out.print(">>");
        int choice = scanner.nextInt();
        if(choice == 1) {
            streamService.getTwitchStreams(username);
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

    private static String searchTwitch(Scanner scanner) {
        if(searchService == null) {
            searchService = new StreamerSearchService(ApiInitializer.initializeTwitch(), null, ApiInitializer.TwitchAuthToken, null);
        }

        scanner.nextLine();
        System.out.print("Enter Twitch Username: \n>>");
        username = scanner.nextLine();
        List<String> twitchResult = searchService.searchTwitchStreamer(username);

        if(twitchResult == null || twitchResult.isEmpty()) {
            System.out.println("Twitch streamer not found.");
            return "";
        } else {
            System.out.println("Twitch streamer found: " + twitchResult.get(0));
            return username;
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