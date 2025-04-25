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

    private ObtainStreamerID mockIdService;
    private RetrieveStreamsService streamService;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext apiContext = mock(ApiContext.class);
        YouTube mockYouTube = mock(YouTube.class, RETURNS_DEEP_STUBS);

        Field ytField = ApiContext.class.getDeclaredField("youtubeService");
        ytField.setAccessible(true);
        ytField.set(apiContext, mockYouTube);

        Field authField = ApiContext.class.getDeclaredField("youtubeAuthToken");
        authField.setAccessible(true);
        authField.set(apiContext, "fake-key");

        mockIdService = mock(ObtainStreamerID.class);
        streamService = new RetrieveStreamsService(apiContext);

        Field idField = RetrieveStreamsService.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(streamService, mockIdService);
    }

    @Test
    void returnsError_whenUserIdIsNull() throws Exception {
        when(mockIdService.getYoutubeUserId("Nobody")).thenReturn(null);
        String result = streamService.getYoutubeStreams("Nobody");
        assertEquals("Error: Could not retrieve YouTube Channel ID for Nobody", result);
    }

    @Test
    void fetchCompletedStreams_returnsEmptyList_whenUserIdIsNull() throws Exception {
        when(mockIdService.getYoutubeUserId("Nobody")).thenReturn(null);
        List<SearchResult> results = streamService.fetchCompletedStreams("Nobody");
        assertTrue(results.isEmpty());
    }

    @Test
    void returnsNoStreamsMessage_whenStreamListIsEmpty() throws Exception {
        when(mockIdService.getYoutubeUserId("Alice")).thenReturn("ID123");
        RetrieveStreamsService spyService = spy(streamService);
        doReturn(Collections.emptyList()).when(spyService).fetchCompletedStreamsById("ID123");

        String result = spyService.getYoutubeStreams("Alice");
        assertEquals("No recent streams found for Alice", result);
    }

    @Test
    void fetchCompletedStreams_delegatesToById_whenUserIdExists() throws Exception {
        when(mockIdService.getYoutubeUserId("Bob")).thenReturn("ID456");
        RetrieveStreamsService spyService = spy(streamService);
        doReturn(List.of(new SearchResult())).when(spyService).fetchCompletedStreamsById("ID456");

        List<SearchResult> results = spyService.fetchCompletedStreams("Bob");
        assertFalse(results.isEmpty());
    }

    @Test
    void formatsCorrectOutput_whenStreamDataExists() throws Exception {
        when(mockIdService.getYoutubeUserId("Carol")).thenReturn("ID789");

        SearchResult video = new SearchResult();
        ResourceId id = new ResourceId().setVideoId("VID1");
        SearchResultSnippet snippet = new SearchResultSnippet()
                .setTitle("Title1")
                .setPublishedAt(new DateTime("2023-10-01T18:00:00Z"));
        video.setId(id);
        video.setSnippet(snippet);

        RetrieveStreamsService spyService = spy(streamService);
        doReturn(List.of(video)).when(spyService).fetchCompletedStreamsById("ID789");

        String result = spyService.getYoutubeStreams("Carol");
        assertTrue(result.contains("1. Title1"));
    }
}