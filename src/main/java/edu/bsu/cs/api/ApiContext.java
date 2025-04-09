package edu.bsu.cs.api;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;

public class ApiContext {
    public ITwitchClient twitchClient;
    public final YouTube youtubeService;
    public final String twitchAuthToken;
    public final String youtubeAuthToken;

    public ApiContext(ITwitchClient twitchClient, YouTube youtubeService, String twitchAuthToken, String youtubeAuthToken) {
        this.twitchClient = twitchClient;
        this.youtubeService = youtubeService;
        this.twitchAuthToken = twitchAuthToken;
        this.youtubeAuthToken = youtubeAuthToken;
    }
}
