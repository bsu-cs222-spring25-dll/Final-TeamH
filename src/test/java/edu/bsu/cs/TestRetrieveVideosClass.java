package edu.bsu.cs;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import edu.bsu.cs.services.RetrieveVideosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestRetrieveVideosClass{

    private ApiContext mockContext;
    private ObtainStreamerID mockObtainStreamerID;
    private RetrieveVideosService service;

    @BeforeEach
    void setUp() throws Exception {
        mockContext = mock(ApiContext.class);
        mockObtainStreamerID = mock(ObtainStreamerID.class);
        service = new RetrieveVideosService(mockContext);

        Field field = RetrieveVideosService.class.getDeclaredField("obtainStreamerID");
        field.setAccessible(true);
        field.set(service, mockObtainStreamerID);
    }

    @Test
    void fetchRecentVideos_ShouldReturnVideos_WhenDataExists() throws Exception {
        InputStream json = getClass().getResourceAsStream("/youtube-videos.json");

        com.google.api.client.json.JsonFactory jsonFactory = com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance();
        SearchListResponse response = jsonFactory.fromInputStream(json, SearchListResponse.class);
        List<SearchResult> mockResults = response.getItems();

        when(mockObtainStreamerID.getYoutubeUserId("TestUser")).thenReturn("mockId");

        RetrieveVideosService spyService = spy(service);
        doReturn(mockResults).when(spyService).fetchRecentVideosById("mockId");

        List<SearchResult> results = spyService.fetchRecentVideos("TestUser");

        assertEquals("Mock Video Title", results.get(0).getSnippet().getTitle());
    }

    @Test
    void fetchRecentVideos_ShouldReturnEmptyList_WhenUserIdIsNull() throws Exception {
        when(mockObtainStreamerID.getYoutubeUserId("NoUser")).thenReturn(null);

        List<SearchResult> results = service.fetchRecentVideos("NoUser");

        assertTrue(results.isEmpty());
    }
}