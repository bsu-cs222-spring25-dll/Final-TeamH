package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.helix.domain.VideoList;
import com.github.twitch4j.helix.domain.User;

import java.util.Optional;

public class StreamerStreamsService {

    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;

    public StreamerStreamsService(ITwitchClient twitchClient, String twitchAuthToken) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
    }

    public void getTwitchStreams(String username) {
        String userId = getUserIdFromUsername(username);

        if (userId == null) {
            System.out.println("Error: Could not retrieve user ID for " + username);
            return;
        }

        VideoList resultList = twitchClient.getHelix()
                .getVideos(twitchAuthToken, null, userId, null, null, null, null, null, 10, null, null)
                .execute();

        if (resultList == null || resultList.getVideos().isEmpty()) {
            System.out.println("No recent streams found for " + username);
            return;
        }

        System.out.println("\nStart of list:");
        final int[] i = {0};
        resultList.getVideos().forEach(video -> {
            i[0]++;
            System.out.println(i[0] + ". " + video.getTitle() + " - " + video.getUserName());
        });
    }


    private String getUserIdFromUsername(String username) {
        UserList userList = twitchClient.getHelix().getUsers(twitchAuthToken, null, java.util.List.of(username)).execute();
        Optional<User> user = userList.getUsers().stream().findFirst();

        String userId = user.map(User::getId).orElse(null);
        System.out.println("Retrieved User ID: " + userId); // Debugging line

        return userId;
    }

}
