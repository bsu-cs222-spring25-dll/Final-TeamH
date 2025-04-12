package edu.bsu.cs.main;

import edu.bsu.cs.*;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.api.ApiInitializer;

import java.util.Scanner;

public class Main {

    private static final ApiContext context = ApiInitializer.initializeApiContext();

    static Scanner scanner = new Scanner(System.in);

    private static final StreamerSearchService searchService = new StreamerSearchService(context);
    private static final StreamerSearchHandler searchHandler = new StreamerSearchHandler(scanner, searchService);
    private static final RetrieveStreamsService streamsService = new RetrieveStreamsService(context);
    private static final ChannelInfoService channelInfo = new ChannelInfoService(context);
    private static final RetrieveVideosService videosService = new RetrieveVideosService(context);
    static LiveStatusService liveStatus = new LiveStatusService(context);

    static String username = "";

    public static void main(String[] args) throws Exception {
        while (true) {
            Menu.printMainMenu();
            int choice = getUserInput(Menu::printMainMenu);

            switch (choice) {
                case 1 -> {
                    context.twitchClient = ApiInitializer.initializeTwitch();
                    username = searchHandler.searchStreamer("Twitch");

                    if (!username.isEmpty()) {
                        twitchAccess(username);
                    }
                }
                case 2 -> {
                    context.youtubeService = ApiInitializer.initializeYoutube();
                    username = searchHandler.searchStreamer("YouTube");
                    if (!username.isEmpty()) {
                        youtubeAccess(username);
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

    private static void twitchAccess(String username) throws Exception {
        while (true) {
            Menu.printTwitchMenu();
            int choice = getUserInput(Menu::printTwitchMenu);

            switch (choice) {
                case 1 -> {
                    String result = streamsService.getTwitchStreams(username);
                    System.out.println(result);
                }
                case 2 -> {
                    RetrieveClips clipsProvider = new RetrieveClips(context);
                    try {
                        clipsProvider.getTwitchClips(username);
                    } catch (Exception e) {
                        System.out.println("Failed to retrieve clips: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                case 3 -> {
                    String info = channelInfo.getTwitchStreamerInfo(username);
                    System.out.println(info != null ? info : "Twitch channel not found for: " + username);
                }
                case 4 -> {
                    String twitchStatus = liveStatus.getTwitchLiveStatus(username);
                    System.out.println(twitchStatus);
                }
                case 5 -> {
                    return;
                }
            }
        }
    }

    private static void youtubeAccess(String username) throws Exception {
        while (true) {
            Menu.printYoutubeMenu();
            int choice = getUserInput(Menu::printYoutubeMenu);

            switch (choice) {
                case 1 -> {
                    String result = streamsService.getYoutubeStreams(username);
                    System.out.println(result);
                }
                case 2 -> {
                    try {
                        var videos = videosService.fetchRecentVideos(username);
                        if (videos.isEmpty()) {
                            System.out.println("No recent videos found for " + username);
                        } else {
                            String formatted = videosService.getYoutubeVideos(username);
                            System.out.println(formatted);
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to retrieve videos: " + e.getMessage());
                    }
                }
                case 3 -> {
                    String youtubeStatus = liveStatus.getYoutubeLiveStatus(username);
                    System.out.println(youtubeStatus);
                }
                case 4 -> {
                    String info = channelInfo.getYoutuberInfo(username);
                    System.out.println(info != null ? info : "YouTube channel not found for: " + username);
                }
                case 5 -> {
                    return;
                }
            }
        }
    }

    private static int getUserInput(Runnable printMenu) {
        while (true) {
            try {
                System.out.print(">> ");
                return scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine();
                printMenu.run();
            } catch (java.util.NoSuchElementException e) {
                System.out.println("No input received. Exiting program.");
                break;
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                break;
            }
        }
        return 0;
    }
}