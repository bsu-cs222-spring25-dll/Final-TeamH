package edu.bsu.cs;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestObtainStreamerId {

    private ApiContext mockContext;
    private ObtainStreamerID idService;
    private TwitchClient mockTwitchClient;
    private TwitchHelix   mockHelix;
    private YouTube mockYouTube;
    private YouTube.Search mockSearch;
    private YouTube.Search.List mockSearchForChannel;
    private YouTube.Channels mockChannels;
    private YouTube.Channels.List mockChannelListRequest;

    @BeforeEach
    void setUp() throws Exception {
        mockContext = mock(ApiContext.class);

        mockTwitchClient = mock(TwitchClient.class, RETURNS_DEEP_STUBS);
        mockHelix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);
        when(mockTwitchClient.getHelix()).thenReturn(mockHelix);
        injectField(mockContext, "twitchClient", mockTwitchClient);
        injectField(mockContext, "twitchAuthToken", "dummy-token");

        mockYouTube = mock(YouTube.class);
        mockSearch = mock(YouTube.Search.class);
        mockSearchForChannel  = mock(YouTube.Search.List.class, RETURNS_DEEP_STUBS);
        when(mockYouTube.search()).thenReturn(mockSearch);
        when(mockSearch.list(anyList())).thenReturn(mockSearchForChannel);
        when(mockSearchForChannel.setQ(anyString())).thenReturn(mockSearchForChannel);
        when(mockSearchForChannel.setType(anyList())).thenReturn(mockSearchForChannel);
        when(mockSearchForChannel.setMaxResults(anyLong())).thenReturn(mockSearchForChannel);
        when(mockSearchForChannel.setKey(anyString())).thenReturn(mockSearchForChannel);

        mockChannels = mock(YouTube.Channels.class);
        mockChannelListRequest = mock(YouTube.Channels.List.class, RETURNS_DEEP_STUBS);
        when(mockYouTube.channels()).thenReturn(mockChannels);
        when(mockChannels.list(anyList())).thenReturn(mockChannelListRequest);

        when(mockChannelListRequest.setKey(anyString())).thenReturn(mockChannelListRequest);
        when(mockChannelListRequest.setForUsername(anyString())).thenReturn(mockChannelListRequest);

        injectField(mockContext, "youtubeService",    mockYouTube);
        injectField(mockContext, "youtubeAuthToken", "dummy-token");

        idService = new ObtainStreamerID(mockContext);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field f = ApiContext.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void getTwitchUserId_returnsNull_forNullUsername() {
        assertNull(idService.getTwitchUserId(null));
    }

    @Test
    void getTwitchUserId_returnsNull_onHelixError() {
        when(mockHelix
                .getUsers(anyString(), any(), anyList())
                .execute()
        ).thenThrow(new RuntimeException("boom"));
        assertNull(idService.getTwitchUserId("someUser"));
    }

    @Test
    void getTwitchUserId_returnsNull_whenNoUserFound() throws Exception {
        UserList emptyList = new UserList();
        Field usersField = UserList.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(emptyList, Collections.emptyList());
        when(mockHelix
                .getUsers(anyString(), any(), anyList())
                .execute()
        ).thenReturn(emptyList);
        assertNull(idService.getTwitchUserId("unknownUser"));
    }

    @Test
    void getTwitchUserId_returnsUserId_whenUserExists() throws Exception {
        User singleUser = new User();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(singleUser, "user-123");

        UserList oneEntry = new UserList();
        Field usersField = UserList.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(oneEntry, List.of(singleUser));

        when(mockHelix
                .getUsers(anyString(), any(), anyList())
                .execute()
        ).thenReturn(oneEntry);

        assertEquals("user-123", idService.getTwitchUserId("knownUser"));
    }

    @Test
    void getYoutubeUserId_throwsIOException_onSearchError() throws Exception {
        when(mockSearchForChannel.execute()).thenThrow(new IOException("search failed"));
        assertThrows(IOException.class, () -> idService.getYoutubeUserId("anyChannel"));
    }

    @Test
    void getYoutubeUserId_returnsChannelId_fromSearchResults() throws Exception {
        SearchResult sr = new SearchResult()
                .setSnippet(new com.google.api.services.youtube.model.SearchResultSnippet()
                        .setChannelId("chan-789"));
        SearchListResponse searchResponse = new SearchListResponse().setItems(List.of(sr));
        when(mockSearchForChannel.execute()).thenReturn(searchResponse);

        assertEquals("chan-789", idService.getYoutubeUserId("someChannel"));
    }

    @Test
    void getYoutubeUserId_returnsChannelId_fromChannelsList_whenSearchEmpty() throws Exception {
        when(mockSearchForChannel.execute())
                .thenReturn(new SearchListResponse().setItems(Collections.emptyList()));

        ChannelListResponse channelResp = new ChannelListResponse()
                .setItems(List.of(new com.google.api.services.youtube.model.Channel().setId("chan-456")));
        when(mockChannelListRequest.execute()).thenReturn(channelResp);

        assertEquals("chan-456", idService.getYoutubeUserId("userName"));
    }

    @Test
    void getYoutubeUserId_returnsNull_whenNoChannelFound() throws Exception {
        when(mockSearchForChannel.execute())
                .thenReturn(new SearchListResponse().setItems(Collections.emptyList()));
        when(mockChannelListRequest.execute())
                .thenReturn(new ChannelListResponse().setItems(Collections.emptyList()));

        assertNull(idService.getYoutubeUserId("noChannel"));
    }
}