package edu.bsu.cs;

import edu.bsu.cs.channelInfo.ChannelInfo;
import edu.bsu.cs.channelInfo.ChannelInfoAggregator;
import edu.bsu.cs.livestatus.LiveStatusAggregator;

import java.util.Scanner;

public class Main {

    private static final ApiContext context = ApiInitializer.initializeApiContext();

    private static final StreamerSearchService searchService = new StreamerSearchService(context);
    private static final RetrieveStreamsService streamService = new RetrieveStreamsService(context);
    private static final ChannelInfoAggregator channelInfoAggregator = new ChannelInfoAggregator(context);
    private static final RetrieveVideosService videoService = new RetrieveVideosService(context);
    private static final RetrieveClips clipService = new RetrieveClips(context);
    static LiveStatusAggregator liveStatusAggregator = new LiveStatusAggregator(context);

    static Scanner scanner = new Scanner(System.in);
    static StreamerSearchHandler searchHandler = new StreamerSearchHandler(scanner, searchService);
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
                    String result = streamService.getTwitchStreams(username);
                    System.out.println(result);
                }
                case 2 -> clipService.getTwitchClips(username);
                case 3 -> {
                    ChannelInfo info = channelInfoAggregator.getChannelInfo("Twitch", username);
                    System.out.println(info != null ? info.toString() : "Twitch channel not found for: " + username);
                }
                case 4 -> {
                    String twitchStatus = liveStatusAggregator.getLiveStatus("Twitch", username);
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
                    String result = streamService.getYoutubeStreams(username);
                    System.out.println(result);
                }
                case 2 -> {
                    String result = videoService.getYoutubeVideos(username);
                    System.out.println(result);
                }
                case 3 -> {
                    String youtubeStatus = liveStatusAggregator.getLiveStatus("YouTube", username);
                    System.out.println(youtubeStatus);
                }
                case 4 -> {
                    ChannelInfo info = channelInfoAggregator.getChannelInfo("YouTube", username);
                    System.out.println(info != null ? info.toString() : "YouTube channel not found for: " + username);
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
