//package edu.bsu.cs;
//
//import com.github.twitch4j.ITwitchClient;
//import com.github.twitch4j.helix.TwitchHelix;
//import com.github.twitch4j.helix.domain.User;
//import com.github.twitch4j.helix.domain.UserList;
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.Channel;
//import com.google.api.services.youtube.model.ChannelListResponse;
//import com.google.api.services.youtube.model.ChannelSnippet;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import com.netflix.hystrix.HystrixCommand;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class TestSearchStreamers {
//
//    private TwitchHelix twitchHelix;
//    private YouTube youtubeService;
//    private StreamerSearchService searchService = new StreamerSearchService(null, null, null, null);
//
//    @BeforeEach
//    void setUp() {
//        ITwitchClient twitchClient = mock(ITwitchClient.class);
//        twitchHelix = mock(TwitchHelix.class);
//        youtubeService = mock(YouTube.class);
//
//        when(twitchClient.getHelix()).thenReturn(twitchHelix);
//        searchService = new StreamerSearchService(twitchClient, youtubeService, "mockToken", "mockApiKey");
//    }
//
//    @Test
//    void searchTwitchStreamer_ShouldReturnUsername_WhenUserExists() {
//        UserList mockUserList = mock(UserList.class);
//        User mockUser = mock(User.class);
//
//        when(mockUser.getDisplayName()).thenReturn("TestStreamer");
//        when(mockUserList.getUsers()).thenReturn(List.of(mockUser));
//
//        HystrixCommand<UserList> mockCommand = mock(HystrixCommand.class);
//        when(mockCommand.execute()).thenReturn(mockUserList);
//
//        when(twitchHelix.getUsers(any(), any(), any())).thenReturn(mockCommand);
//
//        List<String> result = searchService.searchTwitchStreamer("TestStreamer");
//
//        assertEquals("TestStreamer", result.get(0));
//    }
//
//    @Test
//    void searchTwitchStreamer_ShouldReturnNull_WhenUserNotFound() {
//        UserList mockUserList = mock(UserList.class);
//        when(mockUserList.getUsers()).thenReturn(Collections.emptyList());
//
//        HystrixCommand<UserList> mockCommand = mock(HystrixCommand.class);
//        when(mockCommand.execute()).thenReturn(mockUserList);
//
//        when(twitchHelix.getUsers(any(), any(), any())).thenReturn(mockCommand);
//
//        List<String> result = searchService.searchTwitchStreamer("UnknownUser");
//
//        assertNull(result);
//    }
//
//    @Test
//    void searchYoutubeStreamer_ShouldReturnChannelName_WhenChannelExists() throws IOException {
//        YouTube.Channels mockChannels = mock(YouTube.Channels.class);
//        YouTube.Channels.List mockRequest = mock(YouTube.Channels.List.class);
//        ChannelListResponse mockResponse = mock(ChannelListResponse.class);
//        Channel mockChannel = mock(Channel.class);
//        ChannelSnippet mockSnippet = mock(ChannelSnippet.class);
//
//        when(youtubeService.channels()).thenReturn(mockChannels);
//        when(mockChannels.list(any())).thenReturn(mockRequest);
//        when(mockRequest.setKey(any())).thenReturn(mockRequest);
//        when(mockRequest.setForHandle(any())).thenReturn(mockRequest);
//        when(mockRequest.execute()).thenReturn(mockResponse);
//        when(mockResponse.getItems()).thenReturn(List.of(mockChannel));
//        when(mockChannel.getSnippet()).thenReturn(mockSnippet);
//        when(mockSnippet.getTitle()).thenReturn("TestChannel");
//
//        List<String> result = searchService.searchYoutubeStreamer("TestChannel");
//
//        assertEquals("TestChannel", result.get(0));
//    }
//
//    @Test
//    void searchYoutubeStreamer_ShouldReturnEmptyList_WhenChannelNotFound() throws IOException {
//        YouTube.Channels mockChannels = mock(YouTube.Channels.class);
//        YouTube.Channels.List mockRequest = mock(YouTube.Channels.List.class);
//        ChannelListResponse mockResponse = mock(ChannelListResponse.class);
//
//        when(youtubeService.channels()).thenReturn(mockChannels);
//        when(mockChannels.list(any())).thenReturn(mockRequest);
//        when(mockRequest.setKey(any())).thenReturn(mockRequest);
//        when(mockRequest.setForHandle(any())).thenReturn(mockRequest);
//        when(mockRequest.execute()).thenReturn(mockResponse);
//        when(mockResponse.getItems()).thenReturn(Collections.emptyList());
//
//        List<String> result = searchService.searchYoutubeStreamer("UnknownChannel");
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void searchYoutubeStreamer_ShouldHandleIOException_Gracefully() throws IOException {
//        YouTube.Channels mockChannels = mock(YouTube.Channels.class);
//        YouTube.Channels.List mockRequest = mock(YouTube.Channels.List.class);
//
//        when(youtubeService.channels()).thenReturn(mockChannels);
//        when(mockChannels.list(any())).thenReturn(mockRequest);
//        when(mockRequest.setKey(any())).thenReturn(mockRequest);
//        when(mockRequest.setForHandle(any())).thenReturn(mockRequest);
//        when(mockRequest.execute()).thenThrow(new IOException("API error"));
//
//        List<String> result = searchService.searchYoutubeStreamer("TestChannel");
//
//        assertNotNull(result);
//    }
//}
