package edu.bsu.cs.livestatus;

public class LiveStatusFetcher {
    private final LiveStatusProvider twitchProvider;
    private final LiveStatusProvider youtubeProvider;

    public LiveStatusFetcher(LiveStatusProvider twitch, LiveStatusProvider youtube) {
        this.twitchProvider = twitch;
        this.youtubeProvider = youtube;
    }

    public String getLiveStatus(String username, String platform) {
        try {
            return switch (platform.toLowerCase()) {
                case "twitch" -> twitchProvider.getLiveStatus(username);
                case "youtube" -> youtubeProvider.getLiveStatus(username);
                default -> "Unsupported platform.";
            };
        } catch (Exception e) {
            return "Error fetching live status: " + e.getMessage();
        }
    }
}
