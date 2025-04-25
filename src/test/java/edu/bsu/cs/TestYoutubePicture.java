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

    private YouTube.Search.List searchListRequest;
    private YouTube.Channels.List channelsListRequest;
    private ProfilePictureService pictureService;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext apiContext = mock(ApiContext.class);
        YouTube youtubeService = mock(YouTube.class);
        YouTube.Search youtubeSearch = mock(YouTube.Search.class);
        searchListRequest = mock(YouTube.Search.List.class);
        YouTube.Channels youtubeChannels = mock(YouTube.Channels.class);
        channelsListRequest = mock(YouTube.Channels.List.class);

        Field youtubeField = ApiContext.class.getDeclaredField("youtubeService");
        youtubeField.setAccessible(true);
        youtubeField.set(apiContext, youtubeService);

        Field tokenField = ApiContext.class.getDeclaredField("youtubeAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(apiContext, "dummy-key");

        when(youtubeService.search()).thenReturn(youtubeSearch);
        when(youtubeSearch.list(anyList())).thenReturn(searchListRequest);
        when(searchListRequest.setQ(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setType(anyList())).thenReturn(searchListRequest);
        when(searchListRequest.setMaxResults(anyLong())).thenReturn(searchListRequest);
        when(searchListRequest.setKey(anyString())).thenReturn(searchListRequest);

        when(youtubeService.channels()).thenReturn(youtubeChannels);
        when(youtubeChannels.list(anyList())).thenReturn(channelsListRequest);
        when(channelsListRequest.setId(anyList())).thenReturn(channelsListRequest);
        when(channelsListRequest.setKey(anyString())).thenReturn(channelsListRequest);

        pictureService = new ProfilePictureService(apiContext);
    }

    @Test
    void returnsThumbnailUrl_whenSearchAndChannelsSucceed() throws Exception {
        SearchListResponse searchResponse = new SearchListResponse()
                .setItems(Collections.singletonList(
                        new SearchResult()
                                .setId(new ResourceId().setChannelId("chan123"))
                                .setSnippet(new SearchResultSnippet().setChannelId("chan123"))
                ));
        when(searchListRequest.execute()).thenReturn(searchResponse);

        Thumbnail highThumb = new Thumbnail().setUrl("http://img/hi");
        ThumbnailDetails thumbnails = new ThumbnailDetails().setHigh(highThumb);
        ChannelSnippet channelSnippet = new ChannelSnippet().setThumbnails(thumbnails);
        ChannelListResponse channelResponse = new ChannelListResponse()
                .setItems(Collections.singletonList(new Channel().setSnippet(channelSnippet)));
        when(channelsListRequest.execute()).thenReturn(channelResponse);

        String result = pictureService.getProfilePicture("any", "YouTube");
        assertEquals("http://img/hi", result);
    }

    @Test
    void returnsNull_whenSearchFindsNoChannel() throws Exception {
        when(searchListRequest.execute()).thenReturn(new SearchListResponse().setItems(Collections.emptyList()));
        assertNull(pictureService.getProfilePicture("none", "YouTube"));
    }

    @Test
    void returnsNull_whenChannelsFindNoItems() throws Exception {
        SearchListResponse searchResponse = new SearchListResponse().setItems(Collections.singletonList(
                new SearchResult().setId(new ResourceId().setChannelId("chan"))
        ));
        when(searchListRequest.execute()).thenReturn(searchResponse);

        when(channelsListRequest.execute()).thenReturn(new ChannelListResponse().setItems(Collections.emptyList()));
        assertNull(pictureService.getProfilePicture("any", "YouTube"));
    }

    @Test
    void returnsNull_whenSearchThrowsIOException() throws Exception {
        when(searchListRequest.execute()).thenThrow(new IOException("fail"));
        assertNull(pictureService.getProfilePicture("error", "YouTube"));
    }
}