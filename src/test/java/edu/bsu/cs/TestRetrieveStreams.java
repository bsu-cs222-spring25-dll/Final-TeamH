package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.google.api.services.youtube.YouTube;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRetrieveStreams {

    private ITwitchClient twitchClient;
    private YouTube youtubeService;
    private StreamerStreamsService streamerStreamsService;
    private String twitchAuthToken = "TwitchAuthToken";

    @BeforeEach
    void setUp() {
        twitchClient = mock(ITwitchClient.class);
        youtubeService = mock(YouTube.class);
    }

    @Test
    void testGetTwitchStreams() throws Exception {
        Stream stream = mock(Stream.class);
        StreamList streamList = mock(StreamList.class);

        when(stream.getUserName()).thenReturn("testUsername");
        when(stream.getTitle()).thenReturn("Test Stream Title");

        when(streamList.getStreams()).thenReturn(List.of(stream));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        streamerStreamsService.getTwitchStreams("testUsername");

        String output = outputStream.toString();
        assertTrue(output.contains("Test Stream Title"));
        assertTrue(output.contains("testUsername"));
    }
}
