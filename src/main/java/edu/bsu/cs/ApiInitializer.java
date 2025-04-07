package edu.bsu.cs;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.security.GeneralSecurityException;

public class ApiInitializer {

    static String TwitchAuthToken;
    static String YoutubeAuthToken;

    public static ITwitchClient twitchClient;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = ApiInitializer.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
            TwitchAuthToken = properties.getProperty("twitch.auth.token");
            YoutubeAuthToken = properties.getProperty("youtube.auth.token");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ITwitchClient initializeTwitch() {
        if (TwitchAuthToken == null || TwitchAuthToken.isEmpty()) {
            throw new IllegalArgumentException("Twitch API token is not set.");
        }

        twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withDefaultAuthToken(new OAuth2Credential("twitch", TwitchAuthToken))
                .build();
        return twitchClient;
    }

    @SuppressWarnings("deprecation")
    public static YouTube initializeYoutube() {
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
