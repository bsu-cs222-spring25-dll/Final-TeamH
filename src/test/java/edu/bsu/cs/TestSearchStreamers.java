package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestSearchStreamers {

    //implement some mock of searching for streamer names on Twitch and YouTube
    @Test
    void searchTwitchStreamerByName() {
        StreamerSearchService twitchSearchService =  mock(StreamerSearchService.class);

        List<String> mockResult = List.of("Gamer1", "Streamer2");
        when(twitchSearchService.searchTwitchStreamer("gamer1")).thenReturn(mockResult);

        List<String> result = twitchSearchService.searchTwitchStreamer("gamer1");

        assertEquals(mockResult, result);
    }


    @Test
    void verifyTestCall() {
        StreamerSearchService service = mock(StreamerSearchService.class);

        service.searchTwitchStreamer("gamer1");

        Mockito.verify(service).searchTwitchStreamer("gamer1");
    }

    //we should implement a test class for an offline connection that represents an actual api call
}
