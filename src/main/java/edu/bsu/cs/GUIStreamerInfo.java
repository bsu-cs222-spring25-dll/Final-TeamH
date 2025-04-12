package edu.bsu.cs;

public class GUIStreamerInfo {
    private final ChannelInfoService channelInfoService;
    private final ProfilePictureService profilePictureService;
    private final LiveStatusService liveStatusService;

    public GUIStreamerInfo(ChannelInfoService channelInfoService, ProfilePictureService profilePictureService, LiveStatusService liveStatusService) {
        this.channelInfoService = channelInfoService;
        this.profilePictureService = profilePictureService;
        this.liveStatusService = liveStatusService;
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

    public String fetchProfilePicture(String username, String platform) {
        return profilePictureService.getProfilePicture(username, platform);
    }

    public String fetchLiveStatus(String username, String platform) {
        try {
            if (platform.equalsIgnoreCase("Twitch")) {
                return liveStatusService.getTwitchLiveStatus(username);
            } else if (platform.equalsIgnoreCase("YouTube")) {
                return liveStatusService.getYoutubeLiveStatus(username);
            } else {
                return "Unsupported platform.";
            }
        } catch (Exception e) {
            return "Error fetching live status: " + e.getMessage();
        }
    }
}