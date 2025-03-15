package edu.bsu.cs;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestSearchStreamers {

    //implement some mock of searching for streamer names on Twitch
    @Test
    void searchTwitchStreamerByName() {
        StreamerSearchService twitchSearchService =  mock(StreamerSearchService.class);

        List<String> mockResult = List.of("Gamer1", "Streamer2");
        when(twitchSearchService.searchTwitchStreamer("gamer1")).thenReturn(mockResult);

        List<String> result = twitchSearchService.searchTwitchStreamer("gamer1");

        assertEquals(mockResult, result);
    }

    //verify that the searchTwitchStreamer method is called for mock
    @Test
    void verifyTwitchSearchCall() {
        StreamerSearchService service = Mockito.mock(StreamerSearchService.class);

        service.searchTwitchStreamer("gamer1");

        Mockito.verify(service).searchTwitchStreamer("gamer1");
    }

    //implement some sort of mock for testing getting youtube names
    @Test
    void searchYoutubeStreamerByName() {
        StreamerSearchService youtubeSearchService = mock(StreamerSearchService.class);

        List<String> mockResult = List.of("IShowSpeed");
        when(youtubeSearchService.searchYoutubeStreamer("IShowSpeed")).thenReturn(mockResult);

        List<String> result = youtubeSearchService.searchYoutubeStreamer("IShowSpeed");

        assertEquals(mockResult, result);
    }

    //verify that the searchYoutubeStreamer method is called for mock
    @Test
    void verifyYoutubeSearchCall() {
        StreamerSearchService service = Mockito.mock(StreamerSearchService.class);

        service.searchYoutubeStreamer("IShowSpeed");

        Mockito.verify(service).searchYoutubeStreamer("IShowSpeed");
    }
}
