package edu.bsu.cs;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ApiInitializer {

    private static final ApiConfig config = new ApiConfig();

    public static ITwitchClient initializeTwitch() {
        String twitchToken = config.getTwitchAuthToken();
        if (twitchToken == null || twitchToken.isEmpty()) {
            throw new IllegalArgumentException("Twitch API token is not set.");
        }
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withDefaultAuthToken(new OAuth2Credential("twitch", twitchToken))
                .build();
    }

    @SuppressWarnings("deprecation")
    public static YouTube initializeYoutube() {
        String youtubeToken = config.getYoutubeAuthToken();
        if (youtubeToken == null || youtubeToken.isEmpty()) {
            throw new IllegalArgumentException("YouTube API token is not set.");
        }
        try {
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, request -> {})
                    .setApplicationName("StreamerSearchApp")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to initialize YouTube API.", e);
        }
    }

    public static ApiContext initializeApiContext() {
        ITwitchClient twitchClient = initializeTwitch();
        YouTube youtubeService = initializeYoutube();
        String twitchToken = config.getTwitchAuthToken();
        String youtubeToken = config.getYoutubeAuthToken();
        return new ApiContext(twitchClient, youtubeService, twitchToken, youtubeToken);
    }
}
