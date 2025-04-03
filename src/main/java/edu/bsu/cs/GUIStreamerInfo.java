package edu.bsu.cs;

public class GUIStreamerInfo {
    private final ChannelInfoService channelInfoService;

    public GUIStreamerInfo(ChannelInfoService channelInfoService) {
        this.channelInfoService = channelInfoService;
    }

    public String fetchStreamerDetails(String username, String platform) {
        try {
            if (platform.equalsIgnoreCase("Twitch")) {
                return channelInfoService.getTwitchStreamerInfo(username);
            } else if (platform.equalsIgnoreCase("YouTube")) {
                return channelInfoService.getYoutuberInfo(username);
            } else {
                return "Unsupported platform.";
            }
        } catch (Exception e) {
            return "Error retrieving streamer details: " + e.getMessage();
        }
    }
}
