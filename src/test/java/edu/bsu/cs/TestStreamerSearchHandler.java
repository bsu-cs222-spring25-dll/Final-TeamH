package edu.bsu.cs;

import edu.bsu.cs.services.StreamerSearchHandler;
import edu.bsu.cs.services.StreamerSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void promptForValidUsername_ShouldReturnValidUsername_WhenNoSpacesEntered() {
        String validUsername = "ValidUser";

        when(mockScanner.nextLine()).thenReturn(validUsername);
        when(mockSearchService.searchTwitchStreamer(validUsername))
                .thenReturn(List.of(validUsername));

        String result = searchHandler.searchStreamer("Twitch");

        assertEquals(validUsername, result);
    }

    @Test
    void searchStreamer_ShouldReturnUsername_WhenStreamerFoundOnTwitch() {
        String username = "TestTwitchStreamer";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenReturn(List.of(username));

        String result = searchHandler.searchStreamer("Twitch");

        assertEquals(username, result);
    }

    @Test
    void searchStreamer_ShouldReturnEmpty_WhenStreamerNotFoundOnTwitch() {
        String username = "UnknownTwitchUser";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenReturn(List.of());

        String result = searchHandler.searchStreamer("Twitch");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchStreamer_ShouldReturnUsername_WhenStreamerFoundOnYouTube() {
        String username = "TestYouTubeStreamer";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchYoutubeStreamer(username)).thenReturn(List.of(username));

        String result = searchHandler.searchStreamer("YouTube");

        assertEquals(username, result);
    }

    @Test
    void searchStreamer_ShouldReturnEmpty_WhenStreamerNotFoundOnYouTube() {
        String username = "UnknownYouTubeUser";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchYoutubeStreamer(username)).thenReturn(List.of());

        String result = searchHandler.searchStreamer("YouTube");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchStreamer_ShouldReturnEmpty_WhenExceptionIsThrown() {
        String username = "ErrorUser";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenThrow(new RuntimeException("API error"));

        String result = searchHandler.searchStreamer("Twitch");

        assertTrue(result.isEmpty());
    }
}