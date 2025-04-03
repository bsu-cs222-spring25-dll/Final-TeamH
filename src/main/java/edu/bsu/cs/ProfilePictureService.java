package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ProfilePictureService {
    private final ITwitchClient twitchClient;
    private final YouTube youtubeService;

    public ProfilePictureService(ITwitchClient twitchClient, YouTube youtubeService) {
        this.twitchClient = twitchClient;
        this.youtubeService = youtubeService;
    }

    public String getProfilePicture(String username, String platform) {
        if (platform.equalsIgnoreCase("Twitch")) {
            return getTwitchProfilePicture(username);
        } else if (platform.equalsIgnoreCase("YouTube")) {
            return getYouTubeProfilePicture(username);
        }
        return null;
    }

    private String getTwitchProfilePicture(String username) {
        return twitchClient.getHelix().getUsers(null, null, List.of(username))
                .execute()
                .getUsers()
                .stream()
                .findFirst()
                .map(user -> user.getProfileImageUrl())
                .orElse(null);
    }

    private String getYouTubeProfilePicture(String username) {
        try {
            YouTube.Channels.List request = youtubeService.channels()
                    .list(Collections.singletonList("snippet"))
                    .setForUsername(username)
                    .setKey(ApiInitializer.YoutubeAuthToken);
            ChannelListResponse response = request.execute();
            List<Channel> channels = response.getItems();

            if (!channels.isEmpty()) {
                return channels.get(0).getSnippet().getThumbnails().getHigh().getUrl();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
