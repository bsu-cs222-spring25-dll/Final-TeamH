package edu.bsu.cs.profilepicture;

public class ProfilePictureAggregator {
    private final TwitchProfilePictureProvider twitch;
    private final YoutubeProfilePictureProvider youtube;

    public ProfilePictureAggregator(TwitchProfilePictureProvider twitch, YoutubeProfilePictureProvider youtube) {
        this.twitch = twitch;
        this.youtube = youtube;
    }

    public String getProfilePicture(String platform, String username) {
        return switch (platform.toLowerCase()) {
            case "twitch" -> twitch.getProfilePicture(username);
            case "youtube" -> youtube.getProfilePicture(username);
            default -> null;
        };
    }
}
