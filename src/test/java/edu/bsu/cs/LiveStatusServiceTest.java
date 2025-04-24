package edu.bsu.cs;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.LiveStatusService;
import edu.bsu.cs.services.ObtainStreamerID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LiveStatusServiceTest {

    @Mock
    private ApiContext context;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TwitchHelix helix;
    @Mock
    private com.github.twitch4j.TwitchClient twitchClient;
    @Mock
    private ObtainStreamerID obtainStreamerID;
    @Mock
    private YouTube youtubeService;
    @Mock
    private YouTube.Search youtubeSearch;
    @Mock
    private YouTube.Search.List searchListRequest;
    private LiveStatusService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new LiveStatusService(context);

        Field f = LiveStatusService.class.getDeclaredField("obtainStreamerID");
        f.setAccessible(true);
        f.set(service, obtainStreamerID);

        context.twitchClient = twitchClient;
        context.twitchAuthToken = "fakeTwitchToken";
        when(twitchClient.getHelix()).thenReturn(helix);

        context.youtubeService = youtubeService;
        context.youtubeAuthToken = "fakeYouTubeToken";
        when(youtubeService.search()).thenReturn(youtubeSearch);
        when(youtubeSearch.list(anyList())).thenReturn(searchListRequest);
        when(searchListRequest.setKey(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setChannelId(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setEventType(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setType(anyList())).thenReturn(searchListRequest);
    }

    @Test
    void getTwitchLiveStatus_shouldError_whenHelixNotInitialized() {
        when(twitchClient.getHelix()).thenReturn(null);
        String status = service.getTwitchLiveStatus("alice");
        assertEquals("Error: TwitchHelix API client not initialized.", status);
    }

    @Test
    void getTwitchLiveStatus_shouldReportNotLive_whenNoStreamsReturned() throws Exception {
        when(helix.getStreams(any(), any(), any(), anyInt(), any(), any(), any(), anyList())
                .execute()).thenReturn(null);
        String status = service.getTwitchLiveStatus("bob");
        assertEquals("bob is NOT live on Twitch.", status);
    }

    @Test
    void getTwitchLiveStatus_shouldFormatLiveStream_whenStreamPresent() throws Exception {
        StreamList mockList = mock(StreamList.class);
        Stream    mockStream = mock(Stream.class);
        when(mockStream.getTitle()).thenReturn("Game Time");
        when(mockList.getStreams()).thenReturn(Collections.singletonList(mockStream));
        when(helix.getStreams(any(), any(), any(), anyInt(), any(), any(), any(), anyList())
                .execute()).thenReturn(mockList);

        String status = service.getTwitchLiveStatus("charlie");
        assertTrue(status.contains("Watch: https://www.twitch.tv/charlie"));
    }

    @Test
    void getYoutubeLiveStatus_shouldError_whenChannelIdMissing() throws IOException {
        when(obtainStreamerID.getYoutubeUserId("dave")).thenReturn(null);
        String status = service.getYoutubeLiveStatus("dave");
        assertEquals("Error: Could not retrieve YouTube Channel ID for dave", status);
    }

    @Test
    void getYoutubeLiveStatus_shouldReportNotLive_whenNoResults() throws IOException {
        when(obtainStreamerID.getYoutubeUserId("eve")).thenReturn("channelId");
        SearchListResponse emptyResp = mock(SearchListResponse.class);
        when(searchListRequest.execute()).thenReturn(emptyResp);
        when(emptyResp.getItems()).thenReturn(null);

        String status = service.getYoutubeLiveStatus("eve");
        assertEquals("This YouTuber is NOT live.", status);
    }

    @Test
    void getYoutubeLiveStatus_shouldFormatLiveStream_whenResultsPresent() throws IOException {
        when(obtainStreamerID.getYoutubeUserId("frank")).thenReturn("channelId");

        SearchResult sr = mock(SearchResult.class);
        SearchResultSnippet snippet = mock(SearchResultSnippet.class);
        ResourceId rid = mock(ResourceId.class);
        when(snippet.getTitle()).thenReturn("Live Now!");
        when(rid.getVideoId()).thenReturn("vid123");
        when(sr.getSnippet()).thenReturn(snippet);
        when(sr.getId()).thenReturn(rid);

        SearchListResponse liveResp = mock(SearchListResponse.class);
        when(searchListRequest.execute()).thenReturn(liveResp);
        when(liveResp.getItems()).thenReturn(Collections.singletonList(sr));

        String status = service.getYoutubeLiveStatus("frank");
        assertTrue(status.contains("Watch here: https://www.youtube.com/watch?v=vid123"));
    }
}