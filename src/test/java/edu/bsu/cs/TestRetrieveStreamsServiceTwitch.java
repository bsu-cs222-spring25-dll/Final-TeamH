package edu.bsu.cs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.VideoList;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import edu.bsu.cs.services.RetrieveStreamsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestRetrieveStreamsServiceTwitch {

    private ApiContext mockContext;
    private ObtainStreamerID mockIdLookup;
    private RetrieveStreamsService service;
    private TwitchHelix mockHelix;

    @BeforeEach
    void setUp() throws Exception {
        mockContext = mock(ApiContext.class);
        TwitchClient mockClient = mock(TwitchClient.class);
        mockHelix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientF = ApiContext.class.getDeclaredField("twitchClient");
        clientF.setAccessible(true);
        clientF.set(mockContext, mockClient);

        Field tokenF = ApiContext.class.getDeclaredField("twitchAuthToken");
        tokenF.setAccessible(true);
        tokenF.set(mockContext, "dummy-token");

        when(mockClient.getHelix()).thenReturn(mockHelix);

        mockIdLookup = mock(ObtainStreamerID.class);

        service = new RetrieveStreamsService(mockContext);
        Field idF = RetrieveStreamsService.class.getDeclaredField("obtainStreamerID");
        idF.setAccessible(true);
        idF.set(service, mockIdLookup);
    }

    @Test
    void getTwitchStreamsInfo_ShouldReturnStreamTitle_WhenDataExists() throws Exception {
        when(mockIdLookup.getTwitchUserId("StreamerName")).thenReturn("98765");

        try (InputStream is = getClass().getResourceAsStream("/twitch-vods.json")) {
            VideoList videos = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(is, VideoList.class);

            when(mockHelix
                    .getVideos(anyString(), any(), anyString(), any(), any(),
                            any(), any(), any(), anyInt(), any(), any()).execute()).thenReturn(videos);
        }

        List<String> result = service.getTwitchStreamsInfo("StreamerName");

        String returnedTitle = result.get(0).split("__", 2)[0].trim();
        assertEquals("Mock Twitch Stream", returnedTitle);
    }

    @Test
    void getTwitchStreamsInfo_ShouldReturnNull_WhenUserIdIsNull() {
        when(mockIdLookup.getTwitchUserId("Nobody")).thenReturn(null);
        assertNull(service.getTwitchStreamsInfo("Nobody"));
    }

    @Test
    void getTwitchStreamsInfo_ShouldReturnNull_WhenHelixThrows() {
        when(mockIdLookup.getTwitchUserId("BadGuy")).thenReturn("98765");

        when(mockHelix
                .getVideos(
                        anyString(), any(), anyString(),
                        any(), any(), any(), any(), any(), anyInt(), any(), any()).execute()
        ).thenThrow(new RuntimeException("boom"));

        assertNull(service.getTwitchStreamsInfo("BadGuy"));
    }

    @Test
    void getFormattedTwitchVODs_ShouldReturnMessage_WhenNoVods() {
        RetrieveStreamsService spy = spy(service);
        doReturn(null).when(spy).getTwitchStreamsInfo("NoOne");

        doReturn(Collections.emptyList()).when(spy).getTwitchStreamsInfo("Nobody");
        assertEquals("No VODs found for Nobody", spy.getFormattedTwitchVODs("Nobody"));
    }

    @Test
    void getFormattedTwitchVODs_ShouldFormatCorrectly() {
        List<String> fake = List.of("TitleFoo__123__thumbUrl");
        RetrieveStreamsService spy = spy(service);
        doReturn(fake).when(spy).getTwitchStreamsInfo("Foo");

        String out = spy.getFormattedTwitchVODs("Foo");
        assertTrue(out.startsWith("Recent Twitch VODs:\n1. Title: TitleFoo\n"));
    }
    @Test
    void getTwitchStreamsInfo_ShouldReturnNull_WhenNoVideos() throws Exception {
        when(mockIdLookup.getTwitchUserId("StreamerName")).thenReturn("98765");
        VideoList empty = new VideoList();
        Field videosField = VideoList.class.getDeclaredField("videos");
        videosField.setAccessible(true);
        videosField.set(empty, Collections.emptyList());
        when(mockHelix
                .getVideos(anyString(), isNull(), anyString(), isNull(), isNull(),
                        isNull(), isNull(), isNull(), anyInt(), isNull(), isNull())
                .execute()
        ).thenReturn(empty);
        assertNull(service.getTwitchStreamsInfo("StreamerName"));
    }
}