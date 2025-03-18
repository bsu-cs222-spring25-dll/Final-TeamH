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
    public static final String TwitchAuthToken = "x4bbracgz61vqbzgkccm7ed41evvjy";
    public static final String YoutubeAuthToken = "AIzaSyBoizCiqHj1I7MMT6j3Z96sVAw92OygejM";
    public static ITwitchClient twitchClient;


    public static ITwitchClient initializeTwitch() {
        twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withDefaultAuthToken(new OAuth2Credential("twitch", TwitchAuthToken))
                .build();
        return twitchClient;
    }

    @SuppressWarnings("deprecation")
    public static YouTube initializeYoutube() {
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
