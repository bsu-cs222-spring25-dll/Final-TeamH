package edu.bsu.cs;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.YouTube;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import edu.bsu.cs.services.RetrieveStreamsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestRetrieveStreamsServiceYoutube {

    private ApiContext mockContext;
    private ObtainStreamerID mockIdLookup;
    private RetrieveStreamsService service;

    @BeforeEach
    void setUp() throws Exception {
        mockContext = mock(ApiContext.class);
        YouTube mockYouTube = mock(YouTube.class, RETURNS_DEEP_STUBS);
        Field ytServiceField = ApiContext.class.getDeclaredField("youtubeService");
        ytServiceField.setAccessible(true);
        ytServiceField.set(mockContext, mockYouTube);

        Field authTokenField = ApiContext.class.getDeclaredField("youtubeAuthToken");
        authTokenField.setAccessible(true);
        authTokenField.set(mockContext, "fake-key");

        mockIdLookup = mock(ObtainStreamerID.class);
        service = new RetrieveStreamsService(mockContext);
        Field idLookupField = RetrieveStreamsService.class.getDeclaredField("obtainStreamerID");
        idLookupField.setAccessible(true);
        idLookupField.set(service, mockIdLookup);
    }

    @Test
    void getYoutubeStreams_returnsError_whenUserIdNull() throws Exception {
        when(mockIdLookup.getYoutubeUserId("Nobody")).thenReturn(null);
        String out = service.getYoutubeStreams("Nobody");
        assertEquals("Error: Could not retrieve YouTube Channel ID for Nobody", out);
    }

    @Test
    void fetchCompletedStreams_returnsEmpty_whenUserIdNull() throws Exception {
        when(mockIdLookup.getYoutubeUserId("Nobody")).thenReturn(null);
        List<SearchResult> list = service.fetchCompletedStreams("Nobody");
        assertTrue(list.isEmpty());
    }

    @Test
    void getYoutubeStreams_returnsNoStreamsMessage_whenEmpty() throws Exception {
        when(mockIdLookup.getYoutubeUserId("Alice")).thenReturn("ID123");
        RetrieveStreamsService spy = spy(service);
        doReturn(Collections.emptyList()).when(spy).fetchCompletedStreamsById("ID123");
        String out = spy.getYoutubeStreams("Alice");
        assertEquals("No recent streams found for Alice", out);
    }

    @Test
    void fetchCompletedStreams_delegatesToById_whenUserIdExists() throws Exception {
        when(mockIdLookup.getYoutubeUserId("Bob")).thenReturn("ID456");
        RetrieveStreamsService spy = spy(service);
        doReturn(List.of(new SearchResult())).when(spy).fetchCompletedStreamsById("ID456");
        List<SearchResult> list = spy.fetchCompletedStreams("Bob");
        assertFalse(list.isEmpty());
    }

    @Test
    void getYoutubeStreams_formatsCorrectly_whenDataExists() throws Exception {
        when(mockIdLookup.getYoutubeUserId("Carol")).thenReturn("ID789");

        SearchResult fake = new SearchResult();
        ResourceId rid = new ResourceId();
        rid.setVideoId("VID1");
        fake.setId(rid);

        SearchResultSnippet snippet = new SearchResultSnippet();
        snippet.setTitle("Title1");
        snippet.setPublishedAt(new DateTime("2023-10-01T18:00:00Z"));
        fake.setSnippet(snippet);

        RetrieveStreamsService spy = spy(service);
        doReturn(List.of(fake)).when(spy).fetchCompletedStreamsById("ID789");

        String out = spy.getYoutubeStreams("Carol");
        assertTrue(out.contains("1. Title1"));
    }
}