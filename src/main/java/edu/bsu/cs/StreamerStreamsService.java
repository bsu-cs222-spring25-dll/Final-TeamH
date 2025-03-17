package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.VideoList;
import com.google.api.services.youtube.YouTube;

public class StreamerStreamsService {

    private final ITwitchClient twitchClient;
    private final YouTube youtubeService;
    private final String twitchAuthToken;
    private final String youtubeApiKey;

    public StreamerStreamsService(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeApiKey) {
        this.twitchClient = twitchClient;
        this.youtubeService = youtubeService;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeApiKey = youtubeApiKey;
    }

    public void getTwitchStreams(String username) {
        twitchClient.getClientHelper().enableStreamEventListener("twitch4j");
        twitchClient.getClientHelper().enableFollowEventListener("twitch4j");
        final int[] i = {0};
        //Past 10 Stream Title API Example Test
        VideoList resultList = twitchClient.getHelix().getVideos(twitchAuthToken, null, username, null, null, null, null, null, 10, null, null).execute();
        System.out.println("\nStart of list");
        resultList.getVideos().forEach(video -> {
            i[0]++;
            System.out.println(i[0] + ". " + video.getTitle() + " - " + video.getUserName());
        });
    }
}
