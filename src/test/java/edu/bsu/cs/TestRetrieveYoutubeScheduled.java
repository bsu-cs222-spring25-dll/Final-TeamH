package edu.bsu.cs;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import edu.bsu.cs.services.RetrieveScheduledStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestRetrieveYoutubeScheduled {

    private ObtainStreamerID mockIdService;
    private RetrieveScheduledStreams scheduledStreamsService;
    private YouTube.Search.List mockSearchList;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext apiContext = mock(ApiContext.class);
        YouTube mockYoutube = mock(YouTube.class, RETURNS_DEEP_STUBS);
        YouTube.Search mockSearch = mock(YouTube.Search.class);
        mockSearchList = mock(YouTube.Search.List.class);

        Field serviceField = ApiContext.class.getDeclaredField("youtubeService");
        serviceField.setAccessible(true);
        serviceField.set(apiContext, mockYoutube);

        Field tokenField = ApiContext.class.getDeclaredField("youtubeAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(apiContext, "dummy-key");

        when(mockYoutube.search()).thenReturn(mockSearch);
        when(mockSearch.list(anyList())).thenReturn(mockSearchList);
        when(mockSearchList.setKey(anyString())).thenReturn(mockSearchList);
        when(mockSearchList.setChannelId(anyString())).thenReturn(mockSearchList);
        when(mockSearchList.setType(any())).thenReturn(mockSearchList);
        when(mockSearchList.setEventType(anyString())).thenReturn(mockSearchList);
        when(mockSearchList.setOrder(anyString())).thenReturn(mockSearchList);
        when(mockSearchList.setMaxResults(anyLong())).thenReturn(mockSearchList);

        mockIdService = mock(ObtainStreamerID.class);
        scheduledStreamsService = new RetrieveScheduledStreams(apiContext);

        Field idField = RetrieveScheduledStreams.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(scheduledStreamsService, mockIdService);
    }

    @Test
    void returnsEmptyList_whenUserIdIsNull() throws Exception {
        when(mockIdService.getYoutubeUserId("noone")).thenReturn(null);
        List<SearchResult> results = scheduledStreamsService.fetchYoutubeScheduledStreams("noone");
        assertTrue(results.isEmpty());
    }

    @Test
    void returnsEmptyList_whenResponseIsNull() throws Exception {
        when(mockIdService.getYoutubeUserId("alice")).thenReturn("UID1");
        when(mockSearchList.execute()).thenReturn(null);
        List<SearchResult> results = scheduledStreamsService.fetchYoutubeScheduledStreams("alice");
        assertTrue(results.isEmpty());
    }

    @Test
    void returnsEmptyList_whenItemsAreNull() throws Exception {
        when(mockIdService.getYoutubeUserId("bob")).thenReturn("UID2");
        SearchListResponse response = new SearchListResponse();
        response.setItems(null);
        when(mockSearchList.execute()).thenReturn(response);
        List<SearchResult> results = scheduledStreamsService.fetchYoutubeScheduledStreams("bob");
        assertTrue(results.isEmpty());
    }

    @Test
    void returnsResults_whenItemsArePresent() throws Exception {
        when(mockIdService.getYoutubeUserId("carol")).thenReturn("UID3");

        ResourceId resourceId = new ResourceId().setVideoId("VID123");
        SearchResult result = new SearchResult().setId(resourceId);
        SearchListResponse response = new SearchListResponse().setItems(List.of(result));

        when(mockSearchList.execute()).thenReturn(response);

        List<SearchResult> results = scheduledStreamsService.fetchYoutubeScheduledStreams("carol");
        assertEquals(1, results.size());
    }
}