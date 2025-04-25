package edu.bsu.cs;

import edu.bsu.cs.services.StreamerSearchHandler;
import edu.bsu.cs.services.StreamerSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestStreamerSearchHandler {

    private Scanner mockScanner;
    private StreamerSearchService mockSearchService;
    private StreamerSearchHandler searchHandler;

    @BeforeEach
    void setUp() {
        mockScanner = mock(Scanner.class);
        mockSearchService = mock(StreamerSearchService.class);
        searchHandler = new StreamerSearchHandler(mockScanner, mockSearchService);
    }

    @Test
    void returnsValidUsername_whenInputIsClean() {
        String username = "ValidUser";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenReturn(List.of(username));

        String result = searchHandler.searchStreamer("Twitch");
        assertEquals(username, result);
    }

    @Test
    void returnsUsername_whenFoundOnTwitch() {
        String username = "TwitchUser";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenReturn(List.of(username));

        String result = searchHandler.searchStreamer("Twitch");
        assertEquals(username, result);
    }

    @Test
    void returnsEmpty_whenNotFoundOnTwitch() {
        String username = "UnknownTwitch";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenReturn(List.of());

        String result = searchHandler.searchStreamer("Twitch");
        assertTrue(result.isEmpty());
    }

    @Test
    void returnsUsername_whenFoundOnYouTube() {
        String username = "YouTubeUser";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchYoutubeStreamer(username)).thenReturn(List.of(username));

        String result = searchHandler.searchStreamer("YouTube");
        assertEquals(username, result);
    }

    @Test
    void returnsEmpty_whenNotFoundOnYouTube() {
        String username = "UnknownYouTube";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchYoutubeStreamer(username)).thenReturn(List.of());

        String result = searchHandler.searchStreamer("YouTube");
        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmpty_whenSearchThrowsException() {
        String username = "ProblemUser";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenThrow(new RuntimeException("API error"));

        String result = searchHandler.searchStreamer("Twitch");
        assertTrue(result.isEmpty());
    }
}