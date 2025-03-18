package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.helix.domain.VideoList;
import com.github.twitch4j.helix.domain.User;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StreamerStreamsService {

    private final ITwitchClient twitchClient;
    private final String twitchAuthToken;
    private final String youtubeApiKey;
    private final YouTube youtubeService;



    public StreamerStreamsService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
        this.youtubeService = youtubeService;
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

    public void getYoutubeStreams(String username) throws IOException {
        String userId = getUserIdFromUsernameYt(username);
        YouTube.Search.List pastStreamsRequest = youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(youtubeApiKey)
                .setChannelId(userId)
                .setType(Arrays.asList("video"))
                .setEventType("completed")
                .setOrder("date") // Order by date
                .setMaxResults(10L); // Fetch up to 10 results
        SearchListResponse pastStreamsInformation = pastStreamsRequest.execute();
        List<SearchResult> results = pastStreamsInformation.getItems();
        System.out.println(results);

    }
    private String getUserIdFromUsernameYt(String username) throws IOException {
        YouTube.Channels.List channelRequest = youtubeService.channels()
                .list(Collections.singletonList("snippet"))//request ONLY snippet field (contains title and other channel details)
                .setKey(youtubeApiKey)
                .setForHandle("@" + username);
        ChannelListResponse response = channelRequest.execute();
        return response.getItems().get(0).getId();
    }

}
