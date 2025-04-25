package edu.bsu.cs;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestObtainStreamerId {

    private ObtainStreamerID idService;
    private TwitchHelix twitchHelix;
    private YouTube.Search.List searchList;
    private YouTube.Channels.List channelList;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext context = mock(ApiContext.class);

        TwitchClient twitchClient = mock(TwitchClient.class, RETURNS_DEEP_STUBS);
        twitchHelix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);
        when(twitchClient.getHelix()).thenReturn(twitchHelix);
        injectField(context, "twitchClient", twitchClient);
        injectField(context, "twitchAuthToken", "dummy-token");

        YouTube youtube = mock(YouTube.class);
        searchList = mock(YouTube.Search.List.class, RETURNS_DEEP_STUBS);
        channelList = mock(YouTube.Channels.List.class, RETURNS_DEEP_STUBS);

        YouTube.Search search = mock(YouTube.Search.class);
        when(youtube.search()).thenReturn(search);
        when(search.list(anyList())).thenReturn(searchList);
        when(searchList.setQ(anyString())).thenReturn(searchList);
        when(searchList.setType(anyList())).thenReturn(searchList);
        when(searchList.setMaxResults(anyLong())).thenReturn(searchList);
        when(searchList.setKey(anyString())).thenReturn(searchList);

        YouTube.Channels channels = mock(YouTube.Channels.class);
        when(youtube.channels()).thenReturn(channels);
        when(channels.list(anyList())).thenReturn(channelList);
        when(channelList.setKey(anyString())).thenReturn(channelList);
        when(channelList.setForUsername(anyString())).thenReturn(channelList);

        injectField(context, "youtubeService", youtube);
        injectField(context, "youtubeAuthToken", "dummy-token");

        idService = new ObtainStreamerID(context);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = ApiContext.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void returnsNullForNullTwitchUsername() {
        assertNull(idService.getTwitchUserId(null));
    }

    @Test
    void returnsNullWhenTwitchHelixThrows() {
        when(twitchHelix.getUsers(anyString(), any(), anyList()).execute())
                .thenThrow(new RuntimeException("fail"));
        assertNull(idService.getTwitchUserId("failUser"));
    }

    @Test
    void returnsNullWhenTwitchUserListIsEmpty() throws Exception {
        UserList emptyList = new UserList();
        Field field = UserList.class.getDeclaredField("users");
        field.setAccessible(true);
        field.set(emptyList, Collections.emptyList());

        when(twitchHelix.getUsers(anyString(), any(), anyList()).execute()).thenReturn(emptyList);
        assertNull(idService.getTwitchUserId("unknownUser"));
    }

    @Test
    void returnsTwitchUserIdWhenUserFound() throws Exception {
        User user = new User();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, "user-123");

        UserList userList = new UserList();
        Field listField = UserList.class.getDeclaredField("users");
        listField.setAccessible(true);
        listField.set(userList, List.of(user));

        when(twitchHelix.getUsers(anyString(), any(), anyList()).execute()).thenReturn(userList);
        assertEquals("user-123", idService.getTwitchUserId("knownUser"));
    }

    @Test
    void throwsIOExceptionWhenYoutubeSearchFails() throws Exception {
        when(searchList.execute()).thenThrow(new IOException("fail"));
        assertThrows(IOException.class, () -> idService.getYoutubeUserId("failSearch"));
    }

    @Test
    void returnsYoutubeChannelIdFromSearch() throws Exception {
        SearchResult result = new SearchResult()
                .setSnippet(new SearchResultSnippet().setChannelId("chan-789"));
        SearchListResponse searchResp = new SearchListResponse().setItems(List.of(result));
        when(searchList.execute()).thenReturn(searchResp);

        assertEquals("chan-789", idService.getYoutubeUserId("foundInSearch"));
    }

    @Test
    void returnsYoutubeChannelIdFromChannelsListFallback() throws Exception {
        when(searchList.execute()).thenReturn(new SearchListResponse().setItems(Collections.emptyList()));

        ChannelListResponse fallbackResp = new ChannelListResponse()
                .setItems(List.of(new Channel().setId("chan-456")));
        when(channelList.execute()).thenReturn(fallbackResp);

        assertEquals("chan-456", idService.getYoutubeUserId("foundInChannelsList"));
    }

    @Test
    void returnsNullWhenYoutubeSearchAndChannelsFail() throws Exception {
        when(searchList.execute()).thenReturn(new SearchListResponse().setItems(Collections.emptyList()));
        when(channelList.execute()).thenReturn(new ChannelListResponse().setItems(Collections.emptyList()));
        assertNull(idService.getYoutubeUserId("notFound"));
    }
}