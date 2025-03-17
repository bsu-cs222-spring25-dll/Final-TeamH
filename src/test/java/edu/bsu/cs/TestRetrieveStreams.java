package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.google.api.services.youtube.YouTube;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

public class TestRetrieveStreams {

    private ITwitchClient twitchClient;
    private YouTube youtubeService;
    private StreamerStreamsService streamerStreamsService;

    @BeforeEach
    void setUp() {
        twitchClient = mock(ITwitchClient.class);
        youtubeService = mock(YouTube.class);
        streamerStreamsService = new StreamerStreamsService(twitchClient, youtubeService, "TwitchAuthToken", "YouTubeApiKey");
    }
}
