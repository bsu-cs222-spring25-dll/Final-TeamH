package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

public class TwitchAPI {
    public static final String AUTHTOKEN = "x4bbracgz61vqbzgkccm7ed41evvjy";

    public static ITwitchClient twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true)
            .build();

    public static void main(String[] args) {

    }
}
