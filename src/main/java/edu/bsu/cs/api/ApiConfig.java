package edu.bsu.cs.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {
    private final String twitchAuthToken;
    private final String youtubeAuthToken;

    public ApiConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
            twitchAuthToken = properties.getProperty("twitch.auth.token");
            youtubeAuthToken = properties.getProperty("youtube.auth.token");
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration.", ex);
        }
    }

    public String getTwitchAuthToken() {
        return twitchAuthToken;
    }

    public String getYoutubeAuthToken() {
        return youtubeAuthToken;
    }
}
