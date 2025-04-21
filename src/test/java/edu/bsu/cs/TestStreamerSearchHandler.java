package edu.bsu.cs;

import edu.bsu.cs.services.StreamerSearchHandler;
import edu.bsu.cs.services.StreamerSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Scanner;

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

//    @Test
//    void getValidatedUsername_ShouldReturnValidUsername_WhenNoSpacesEntered() {
//        String validUsername = "TestUser";
//        when(mockScanner.nextLine()).thenReturn(validUsername);
//
//        String result = searchHandler.getValidatedUsername("Twitch");
//
//        assertEquals(validUsername, result);
//    }

//    @Test
//    void getValidatedUsername_ShouldPromptForValidUsername_WhenSpacesEntered() {
//        when(mockScanner.nextLine()).thenReturn("Invalid Username").thenReturn("ValidUser");
//
//        String result = searchHandler.getValidatedUsername("Twitch");
//
//        assertEquals("ValidUser", result);
//    }

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

        assertEquals("", result);
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

        assertEquals("", result);
    }

    @Test
    void searchStreamer_ShouldReturnEmpty_WhenExceptionIsThrown() {
        String username = "TestStreamer";
        when(mockScanner.nextLine()).thenReturn(username);
        when(mockSearchService.searchTwitchStreamer(username)).thenThrow(new RuntimeException("API error"));

        String result = searchHandler.searchStreamer("Twitch");

        assertEquals("", result);
    }
}
