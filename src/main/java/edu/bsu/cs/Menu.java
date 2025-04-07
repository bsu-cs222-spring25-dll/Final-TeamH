package edu.bsu.cs;

public class Menu {

    public static void printTwitchMenu() {
        System.out.println("----- Twitch Services -----");
        System.out.println("1) Print the 10 most recent streams");
        System.out.println("2) Print the 10 most recent clips");
        System.out.println("3) View Account Information");
        System.out.println("4) View Live Status");
        System.out.println("5) Exit Twitch Service");
    }

    public static void printYoutubeMenu() {
        System.out.println("----- Youtube Services -----");
        System.out.println("1) Print the 10 most recent streams");
        System.out.println("2) Print recent uploads");
        System.out.println("3) View live status");
        System.out.println("4) View Channel Information");
        System.out.println("5) Exit YouTube Service");
    }

    public static void printMainMenu() {
        System.out.println("\n===== Streamer Search Service =====");
        System.out.println("1. Search for a Twitch Streamer");
        System.out.println("2. Search for a Youtuber");
        System.out.println("3. Exit");
    }

}
