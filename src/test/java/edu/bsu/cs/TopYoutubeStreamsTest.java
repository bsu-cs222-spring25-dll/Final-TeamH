package edu.bsu.cs;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.TopYoutubeStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TopYoutubeStreamsTest {

    private YouTube.Search.List searchListRequest;
    private TopYoutubeStreams youtubeStreamsService;

    @BeforeEach
    void setUp() throws IOException {
        YouTube youtubeService = mock(YouTube.class, RETURNS_DEEP_STUBS);
        YouTube.Search youtubeSearch = mock(YouTube.Search.class);
        searchListRequest = mock(YouTube.Search.List.class);

        when(youtubeService.search()).thenReturn(youtubeSearch);
        when(youtubeSearch.list(anyList())).thenReturn(searchListRequest);
        when(searchListRequest.setKey(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setType(anyList())).thenReturn(searchListRequest);
        when(searchListRequest.setEventType(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setOrder(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setMaxResults(anyLong())).thenReturn(searchListRequest);
        when(searchListRequest.setVideoCategoryId(anyString())).thenReturn(searchListRequest);

        ApiContext apiContext = new ApiContext(null, youtubeService, "", "fakeYoutubeKey");
        youtubeStreamsService = new TopYoutubeStreams(apiContext);
    }

    @Test
    void fetchTopStreams_returnsEmptyList_whenItemsAreNull() throws IOException {
        SearchListResponse searchResponse = mock(SearchListResponse.class);
        when(searchListRequest.execute()).thenReturn(searchResponse);
        when(searchResponse.getItems()).thenReturn(null);

        List<SearchResult> results = youtubeStreamsService.fetchTopStreams();
        assertTrue(results.isEmpty());
    }

    @Test
    void fetchTopStreams_returnsList_whenItemsPresent() throws IOException {
        SearchListResponse searchResponse = mock(SearchListResponse.class);
        SearchResult result = mock(SearchResult.class);
        when(searchListRequest.execute()).thenReturn(searchResponse);
        when(searchResponse.getItems()).thenReturn(List.of(result));

        List<SearchResult> results = youtubeStreamsService.fetchTopStreams();
        assertEquals(1, results.size());
    }

    @Test
    void fetchTopStreams_throwsException_whenExecuteFails() {
        assertThrows(IOException.class, () -> {
            when(searchListRequest.execute()).thenThrow(new IOException("fail"));
            youtubeStreamsService.fetchTopStreams();
        });
    }

    @Test
    void fetchTopLiveStreamsByCategory_returnsEmptyList_whenItemsAreNull() throws IOException {
        SearchListResponse searchResponse = mock(SearchListResponse.class);
        when(searchListRequest.execute()).thenReturn(searchResponse);
        when(searchResponse.getItems()).thenReturn(null);

        List<SearchResult> results = youtubeStreamsService.fetchTopLiveStreamsByCategory("cat1");
        assertTrue(results.isEmpty());
    }

    @Test
    void fetchTopLiveStreamsByCategory_returnsList_whenItemsPresent() throws IOException {
        SearchListResponse searchResponse = mock(SearchListResponse.class);
        SearchResult result = mock(SearchResult.class);
        when(searchListRequest.execute()).thenReturn(searchResponse);
        when(searchResponse.getItems()).thenReturn(List.of(result));

        List<SearchResult> results = youtubeStreamsService.fetchTopLiveStreamsByCategory("cat1");
        assertEquals(1, results.size());
    }
}