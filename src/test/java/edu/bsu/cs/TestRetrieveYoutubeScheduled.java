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

    private ApiContext contextMock;
    private ObtainStreamerID idLookupMock;
    private RetrieveScheduledStreams streamsMock;
    private YouTube youtubeMock;
    private YouTube.Search searchMock;
    private YouTube.Search.List listRequestMock;

    @BeforeEach
    void setUp() throws Exception {
        contextMock = mock(ApiContext.class);
        youtubeMock = mock(YouTube.class, RETURNS_DEEP_STUBS);
        searchMock = mock(YouTube.Search.class);
        listRequestMock = mock(YouTube.Search.List.class);

        Field serviceField = ApiContext.class.getDeclaredField("youtubeService");
        serviceField.setAccessible(true);
        serviceField.set(contextMock, youtubeMock);

        Field tokenField = ApiContext.class.getDeclaredField("youtubeAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(contextMock, "dummy-key");

        when(youtubeMock.search()).thenReturn(searchMock);
        when(searchMock.list(anyList())).thenReturn(listRequestMock);
        when(listRequestMock.setKey(anyString())).thenReturn(listRequestMock);
        when(listRequestMock.setChannelId(anyString())).thenReturn(listRequestMock);
        when(listRequestMock.setType(any())).thenReturn(listRequestMock);
        when(listRequestMock.setEventType(anyString())).thenReturn(listRequestMock);
        when(listRequestMock.setOrder(anyString())).thenReturn(listRequestMock);
        when(listRequestMock.setMaxResults(anyLong())).thenReturn(listRequestMock);

        idLookupMock = mock(ObtainStreamerID.class);
        streamsMock = new RetrieveScheduledStreams(contextMock);
        Field idField = RetrieveScheduledStreams.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(streamsMock, idLookupMock);
    }

    @Test
    void youtubeScheduled_returnsEmpty_whenUserIdNull() throws Exception {
        when(idLookupMock.getYoutubeUserId("noone")).thenReturn(null);
        List<SearchResult> result = streamsMock.fetchYoutubeScheduledStreams("noone");
        assertTrue(result.isEmpty());
    }

    @Test
    void youtubeScheduled_returnsEmpty_whenResponseNull() throws Exception {
        when(idLookupMock.getYoutubeUserId("alice")).thenReturn("UID1");
        when(listRequestMock.execute()).thenReturn(null);
        List<SearchResult> result = streamsMock.fetchYoutubeScheduledStreams("alice");
        assertTrue(result.isEmpty());
    }

    @Test
    void youtubeScheduled_returnsEmpty_whenItemsNull() throws Exception {
        when(idLookupMock.getYoutubeUserId("bob")).thenReturn("UID2");
        SearchListResponse emptyResponse = new SearchListResponse();
        emptyResponse.setItems(null);
        when(listRequestMock.execute()).thenReturn(emptyResponse);
        List<SearchResult> result = streamsMock.fetchYoutubeScheduledStreams("bob");
        assertTrue(result.isEmpty());
    }

    @Test
    void youtubeScheduled_returnsList_whenItemsPresent() throws Exception {
        when(idLookupMock.getYoutubeUserId("carol")).thenReturn("UID3");
        SearchListResponse response = new SearchListResponse();
        SearchResult result = new SearchResult();
        ResourceId resource = new ResourceId();
        resource.setVideoId("VID123");
        result.setId(resource);
        response.setItems(List.of(result));
        when(listRequestMock.execute()).thenReturn(response);
        List<SearchResult> searchResults = streamsMock.fetchYoutubeScheduledStreams("carol");
        assertEquals(1, searchResults.size());
    }
}