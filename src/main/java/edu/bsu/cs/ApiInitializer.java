package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.google.api.services.youtube.YouTube;

public class ApiInitializer {
    public static final String TwitchAuthToken = "x4bbracgz61vqbzgkccm7ed41evvjy";
    public static final String YoutubeAuthToken = "AIzaSyBoizCiqHj1I7MMT6j3Z96sVAw92OygejM";

    public static ITwitchClient initializeTwitch() {
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .build();
    }

    public static YouTube initializeYoutube() {

    }
}
