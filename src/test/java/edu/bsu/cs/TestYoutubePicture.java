package edu.bsu.cs;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ProfilePictureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestYoutubePicture {

    private ApiContext mockContext;
    private YouTube mockYouTube;
    private YouTube.Search mockSearch;
    private YouTube.Search.List mockSearchList;
    private YouTube.Channels mockChannels;
    private YouTube.Channels.List mockChannelsList;
    private ProfilePictureService service;

    @BeforeEach
    void setUp() throws Exception {
        mockContext    = mock(ApiContext.class);
        mockYouTube    = mock(YouTube.class);
        mockSearch     = mock(YouTube.Search.class);
        mockSearchList = mock(YouTube.Search.List.class);
        mockChannels   = mock(YouTube.Channels.class);
        mockChannelsList = mock(YouTube.Channels.List.class);

        Field svcField = ApiContext.class.getDeclaredField("youtubeService");
        svcField.setAccessible(true);
        svcField.set(mockContext, mockYouTube);

        Field tokField = ApiContext.class.getDeclaredField("youtubeAuthToken");
        tokField.setAccessible(true);
        tokField.set(mockContext, "dummy-key");

        when(mockYouTube.search()).thenReturn(mockSearch);
        when(mockSearch.list(anyList())).thenReturn(mockSearchList);
        when(mockSearchList.setQ(anyString())).thenReturn(mockSearchList);
        when(mockSearchList.setType(anyList())).thenReturn(mockSearchList);
        when(mockSearchList.setMaxResults(anyLong())).thenReturn(mockSearchList);
        when(mockSearchList.setKey(anyString())).thenReturn(mockSearchList);

        when(mockYouTube.channels()).thenReturn(mockChannels);
        when(mockChannels.list(anyList())).thenReturn(mockChannelsList);
        when(mockChannelsList.setId(anyList())).thenReturn(mockChannelsList);
        when(mockChannelsList.setKey(anyString())).thenReturn(mockChannelsList);

        service = new ProfilePictureService(mockContext);
    }

    @Test
    void returnsThumbnailUrl_whenSearchAndChannelsSucceed() throws Exception {
        SearchListResponse searchResp = new SearchListResponse()
                .setItems(Collections.singletonList(
                        new SearchResult()
                                .setId(new ResourceId().setChannelId("chan123"))
                                .setSnippet(new SearchResultSnippet().setChannelId("chan123"))
                ));
        when(mockSearchList.execute()).thenReturn(searchResp);

        ChannelSnippet cs = new ChannelSnippet();
        ThumbnailDetails td = new ThumbnailDetails();
        td.setHigh(new Thumbnail().setUrl("http://img/hi"));
        cs.setThumbnails(td);
        ChannelListResponse chanResp = new ChannelListResponse()
                .setItems(Collections.singletonList(new Channel().setSnippet(cs)));
        when(mockChannelsList.execute()).thenReturn(chanResp);

        String result = service.getProfilePicture("any", "YouTube");
        assertEquals("http://img/hi", result);
    }

    @Test
    void returnsNull_whenSearchFindsNoChannel() throws Exception {
        when(mockSearchList.execute())
                .thenReturn(new SearchListResponse().setItems(Collections.emptyList()));

        assertNull(service.getProfilePicture("none", "YouTube"));
    }

    @Test
    void returnsNull_whenChannelsFindNoItems() throws Exception {
        SearchListResponse sr = new SearchListResponse()
                .setItems(Collections.singletonList(
                        new SearchResult().setId(new ResourceId().setChannelId("chan"))
                ));
        when(mockSearchList.execute()).thenReturn(sr);

        when(mockChannelsList.execute())
                .thenReturn(new ChannelListResponse().setItems(Collections.emptyList()));

        assertNull(service.getProfilePicture("any", "YouTube"));
    }

    @Test
    void returnsNull_whenSearchThrowsIOException() throws Exception {
        when(mockSearchList.execute()).thenThrow(new IOException("fail"));
        assertNull(service.getProfilePicture("error", "YouTube"));
    }
}