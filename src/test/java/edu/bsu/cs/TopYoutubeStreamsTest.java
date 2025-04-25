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

    private YouTube mockYouTube;
    private YouTube.Search mockSearch;
    private YouTube.Search.List mockList;
    private ApiContext context;
    private TopYoutubeStreams service;

    @BeforeEach
    void setUp() throws IOException {
        mockYouTube = mock(YouTube.class, RETURNS_DEEP_STUBS);
        mockSearch  = mock(YouTube.Search.class);
        mockList    = mock(YouTube.Search.List.class);

        when(mockYouTube.search()).thenReturn(mockSearch);
        when(mockSearch.list(anyList())).thenReturn(mockList);
        when(mockList.setKey(anyString())).thenReturn(mockList);
        when(mockList.setType(anyList())).thenReturn(mockList);
        when(mockList.setEventType(anyString())).thenReturn(mockList);
        when(mockList.setOrder(anyString())).thenReturn(mockList);
        when(mockList.setMaxResults(anyLong())).thenReturn(mockList);
        when(mockList.setVideoCategoryId(anyString())).thenReturn(mockList);

        context = new ApiContext(null, mockYouTube, "", "fakeYoutubeKey");
        service = new TopYoutubeStreams(context);
    }

    @Test
    void fetchTopStreams_returnsEmpty_whenItemsNull() throws IOException {
        SearchListResponse resp = mock(SearchListResponse.class);
        when(mockList.execute()).thenReturn(resp);
        when(resp.getItems()).thenReturn(null);

        List<SearchResult> out = service.fetchTopStreams();
        assertTrue(out.isEmpty());
    }

    @Test
    void fetchTopStreams_returnsItems_whenPresent() throws IOException {
        SearchListResponse resp = mock(SearchListResponse.class);
        SearchResult sr = mock(SearchResult.class);
        when(mockList.execute()).thenReturn(resp);
        when(resp.getItems()).thenReturn(List.of(sr));

        List<SearchResult> out = service.fetchTopStreams();
        assertEquals(1, out.size());
    }

    @Test
    void fetchTopStreams_propagatesIOException() {
        assertThrows(IOException.class, () -> {
            when(mockList.execute()).thenThrow(new IOException("fail"));
            service.fetchTopStreams();
        });
    }

    @Test
    void fetchTopLiveStreamsByCategory_returnsEmpty_whenItemsNull() throws IOException {
        SearchListResponse resp = mock(SearchListResponse.class);
        when(mockList.execute()).thenReturn(resp);
        when(resp.getItems()).thenReturn(null);

        List<SearchResult> out = service.fetchTopLiveStreamsByCategory("cat1");
        assertTrue(out.isEmpty());
    }

    @Test
    void fetchTopLiveStreamsByCategory_returnsItems_whenPresent() throws IOException {
        SearchListResponse resp = mock(SearchListResponse.class);
        SearchResult sr = mock(SearchResult.class);
        when(mockList.execute()).thenReturn(resp);
        when(resp.getItems()).thenReturn(List.of(sr));

        List<SearchResult> out = service.fetchTopLiveStreamsByCategory("cat1");
        assertEquals(1, out.size());
    }
}