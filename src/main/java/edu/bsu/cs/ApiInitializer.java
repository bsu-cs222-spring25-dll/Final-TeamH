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
        String TwitchAuthToken = config.getTwitchAuthToken();
        if (TwitchAuthToken == null || TwitchAuthToken.isEmpty()) {
            throw new IllegalArgumentException("Twitch API token is not set.");
        }

        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withDefaultAuthToken(new OAuth2Credential("twitch", TwitchAuthToken))
                .build();
    }

    @SuppressWarnings("deprecation")
    public static YouTube initializeYoutube() {
        String YoutubeAuthToken = config.getYoutubeAuthToken();
        if (YoutubeAuthToken == null || YoutubeAuthToken.isEmpty()) {
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
}
