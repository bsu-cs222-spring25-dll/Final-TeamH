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
    private ApiContext apiContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TwitchHelix twitchHelix;
    @Mock
    private com.github.twitch4j.TwitchClient twitchClient;
    @Mock
    private ObtainStreamerID idService;
    @Mock
    private YouTube youtubeService;
    @Mock
    private YouTube.Search youtubeSearch;
    @Mock
    private YouTube.Search.List youtubeSearchRequest;
    private LiveStatusService statusService;

    @BeforeEach
    void setUp() throws Exception {
        statusService = new LiveStatusService(apiContext);

        Field idField = LiveStatusService.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(statusService, idService);

        apiContext.twitchClient = twitchClient;
        apiContext.twitchAuthToken = "fakeTwitchToken";
        when(twitchClient.getHelix()).thenReturn(twitchHelix);

        apiContext.youtubeService = youtubeService;
        apiContext.youtubeAuthToken = "fakeYouTubeToken";
        when(youtubeService.search()).thenReturn(youtubeSearch);
        when(youtubeSearch.list(anyList())).thenReturn(youtubeSearchRequest);
        when(youtubeSearchRequest.setKey(anyString())).thenReturn(youtubeSearchRequest);
        when(youtubeSearchRequest.setChannelId(anyString())).thenReturn(youtubeSearchRequest);
        when(youtubeSearchRequest.setEventType(anyString())).thenReturn(youtubeSearchRequest);
        when(youtubeSearchRequest.setType(anyList())).thenReturn(youtubeSearchRequest);
    }

    @Test
    void returnsError_whenTwitchHelixNotInitialized() {
        when(twitchClient.getHelix()).thenReturn(null);
        String result = statusService.getTwitchLiveStatus("alice");
        assertEquals("Error: TwitchHelix API client not initialized.", result);
    }

    @Test
    void returnsNotLive_whenNoTwitchStreamsExist() {
        when(twitchHelix.getStreams(any(), any(), any(), anyInt(), any(), any(), any(), anyList())
                .execute()).thenReturn(null);

        String result = statusService.getTwitchLiveStatus("bob");
        assertEquals("bob is NOT live on Twitch.", result);
    }

    @Test
    void returnsTwitchLiveStatus_whenStreamPresent() {
        StreamList streamList = mock(StreamList.class);
        Stream stream = mock(Stream.class);
        when(stream.getTitle()).thenReturn("Game Time");
        when(streamList.getStreams()).thenReturn(Collections.singletonList(stream));

        when(twitchHelix.getStreams(any(), any(), any(), anyInt(), any(), any(), any(), anyList())
                .execute()).thenReturn(streamList);

        String result = statusService.getTwitchLiveStatus("charlie");
        assertTrue(result.contains("Watch: https://www.twitch.tv/charlie"));
    }

    @Test
    void returnsError_whenYoutubeChannelIdIsMissing() throws IOException {
        when(idService.getYoutubeUserId("dave")).thenReturn(null);
        String result = statusService.getYoutubeLiveStatus("dave");
        assertEquals("Error: Could not retrieve YouTube Channel ID for dave", result);
    }

    @Test
    void returnsNotLive_whenYoutubeSearchHasNoResults() throws IOException {
        when(idService.getYoutubeUserId("eve")).thenReturn("channelId");
        SearchListResponse emptyResponse = mock(SearchListResponse.class);
        when(youtubeSearchRequest.execute()).thenReturn(emptyResponse);
        when(emptyResponse.getItems()).thenReturn(null);

        String result = statusService.getYoutubeLiveStatus("eve");
        assertEquals("This YouTuber is NOT live.", result);
    }

    @Test
    void returnsYoutubeLiveStatus_whenResultPresent() throws IOException {
        when(idService.getYoutubeUserId("frank")).thenReturn("channelId");

        SearchResult resultItem = mock(SearchResult.class);
        SearchResultSnippet snippet = mock(SearchResultSnippet.class);
        ResourceId resourceId = mock(ResourceId.class);
        when(snippet.getTitle()).thenReturn("Live Now!");
        when(resourceId.getVideoId()).thenReturn("vid123");
        when(resultItem.getSnippet()).thenReturn(snippet);
        when(resultItem.getId()).thenReturn(resourceId);

        SearchListResponse response = mock(SearchListResponse.class);
        when(youtubeSearchRequest.execute()).thenReturn(response);
        when(response.getItems()).thenReturn(Collections.singletonList(resultItem));

        String result = statusService.getYoutubeLiveStatus("frank");
        assertTrue(result.contains("Watch here: https://www.youtube.com/watch?v=vid123"));
    }
}