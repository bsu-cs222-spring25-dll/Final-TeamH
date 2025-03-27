package edu.bsu.cs;

import com.google.api.services.youtube.YouTube;

import java.util.Scanner;
import java.util.List;

import java.io.IOException;

import static edu.bsu.cs.ApiInitializer.twitchClient;

public class Main {

    private static StreamerSearchService searchService;
    private static RetrieveStreamsService streamService;
    private static ChannelInfoService infoService;
    private static RetrieveVideosService videoService;
    private static RetrieveClips clipService;
    private static LiveStatusService statusService;


    static String username = "";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        streamService = new RetrieveStreamsService(ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube(), ApiInitializer.TwitchAuthToken, ApiInitializer.YoutubeAuthToken);
        infoService = new ChannelInfoService(ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube(), ApiInitializer.YoutubeAuthToken);
        videoService = new RetrieveVideosService(ApiInitializer.initializeYoutube(), ApiInitializer.YoutubeAuthToken);
        clipService = new RetrieveClips(ApiInitializer.initializeTwitch(), ApiInitializer.TwitchAuthToken);
        statusService = new LiveStatusService(ApiInitializer.initializeTwitch(), ApiInitializer.initializeYoutube(), ApiInitializer.TwitchAuthToken, ApiInitializer.YoutubeAuthToken);

        while(true) {
            int choice = displayMenu(scanner);

            switch(choice) {
                case 1 -> {
                    twitchClient = ApiInitializer.initializeTwitch();
                    searchService = new StreamerSearchService(twitchClient, null, ApiInitializer.TwitchAuthToken, null);

                    username = searchTwitch(scanner);
                    if(!username.isEmpty()) {
                        printTwitchMenu(scanner, username);
                    }
                }
                case 2 -> {
                    YouTube youtubeClient = ApiInitializer.initializeYoutube();
                    searchService = new StreamerSearchService(null, youtubeClient, null, ApiInitializer.YoutubeAuthToken);

                    username = searchYoutube(scanner);
                    if(!username.isEmpty()) {
                        printYoutubeMenu(scanner, username);
                    }
                }
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
        System.out.println("2) Print the 10 most recent clips");
        System.out.println("3) View Account Information");
        System.out.println("4) View Live Status");
        System.out.print(">>");

        int choice = -1;
        while (true) {
            if(scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();  // Consume the newline
                if (choice >= 1 && choice <= 4) {
                    break;
                } else {
                    System.out.println("Invalid choice! Please enter a number between 1 and 4.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();  // Consume the invalid input
            }
        }

        switch (choice) {
            case 1 -> streamService.getTwitchStreams(username);
            case 2 -> clipService.getTwitchClips(username);
            case 3 -> infoService.getTwitchStreamerInfo(username);
            case 4 -> statusService.getTwitchLiveStatus(username);
            default -> System.out.println("Invalid choice.");
        }
    }


    private static void printYoutubeMenu(Scanner scanner, String username) throws IOException {
        System.out.println("----- Youtube Services -----");
        System.out.println("1) Print the 10 most recent streams");
        System.out.println("2) Print the 10 most streamed videos");
        System.out.println("3) View live status");
        System.out.println("4) View Channel Information");
        System.out.print(">>");
        int choice = scanner.nextInt();
        if(choice == 1) {
            streamService.getYoutubeStreams(username);
        }else if (choice == 2) {
            videoService.getYoutubeVideos(username);
        }else if (choice == 3) {
            statusService.getYoutubeLiveStatus(username);
        }else if (choice == 4) {
            infoService.getYoutuberInfo(username);
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
        while (true) {
            System.out.print("Enter Twitch Username (no spaces allowed): \n>> ");
            username = scanner.nextLine().trim();  // Remove leading and trailing spaces

            if (username.contains(" ")) {
                System.out.println("Error: Username cannot contain spaces. Please try again.");
            } else {
                break;  // Valid input, exit loop
            }
        }
        try {
            List<String> twitchResult = searchService.searchTwitchStreamer(username);
            if (twitchResult == null || twitchResult.isEmpty()) {
                System.out.println("Twitch streamer not found.");
                return "";
            } else {
                System.out.println("Twitch streamer found: " + twitchResult.get(0));
                return username;
            }
        } catch (Exception e) {
            System.out.println("Twitch User not found (crash prevention)");
            return "";
        }

    }


    private static String searchYoutube(Scanner scanner) {
        if (searchService == null) {
            searchService = new StreamerSearchService(null, ApiInitializer.initializeYoutube(), null, ApiInitializer.YoutubeAuthToken);
        }

        scanner.nextLine();
        String youtubeUsername;

        while (true) {
            System.out.print("Enter YouTube Username (no spaces allowed): \n>> ");
            youtubeUsername = scanner.nextLine().trim();  // Remove leading/trailing spaces

            if (youtubeUsername.contains(" ")) {
                System.out.println("Error: Username cannot contain spaces. Please try again.");
            } else {
                break;  // Valid input, exit loop
            }
        }

        try {
            List<String> youtubeResult = searchService.searchYoutubeStreamer(youtubeUsername);

            if (youtubeResult != null && !youtubeResult.isEmpty()) {
                System.out.println("YouTube Streamer Found: " + youtubeResult.get(0));
            } else {
                System.out.println("YouTuber not found.");
                return "";
            }

        } catch (Exception e) {
            System.out.println("Youtuber not found (crash prevention)");
            return "";
        }

        return youtubeUsername;
    }

}