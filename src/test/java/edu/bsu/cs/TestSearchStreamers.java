package edu.bsu.cs;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSearchStreamers {

    //implement some mock of searching for streamer names on Twitch
    @Test
    void searchTwitchStreamerByName() {
        StreamerSearchService service = Mockito.mock(StreamerSearchService.class);

        List<String> mockResult = List.of("Gamer1", "Streamer2");
        Mockito.when(service.searchTwitchStreamer("gamer1")).thenReturn(mockResult);

        List<String> result = service.searchTwitchStreamer("gamer1");

        assertTrue(result.contains("Gamer1"));

        Mockito.verify(service).searchTwitchStreamer("gamer1");
    }

    //we should implement a test class for a offline connection that represents an actual api call
}
