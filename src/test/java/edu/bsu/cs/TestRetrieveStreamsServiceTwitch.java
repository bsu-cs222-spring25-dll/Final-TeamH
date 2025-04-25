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

    private ObtainStreamerID idService;
    private RetrieveStreamsService streamService;
    private TwitchHelix twitchHelix;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext apiContext = mock(ApiContext.class);
        TwitchClient twitchClient = mock(TwitchClient.class);
        twitchHelix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientField = ApiContext.class.getDeclaredField("twitchClient");
        clientField.setAccessible(true);
        clientField.set(apiContext, twitchClient);

        Field tokenField = ApiContext.class.getDeclaredField("twitchAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(apiContext, "dummy-token");

        when(twitchClient.getHelix()).thenReturn(twitchHelix);

        idService = mock(ObtainStreamerID.class);
        streamService = new RetrieveStreamsService(apiContext);

        Field idField = RetrieveStreamsService.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(streamService, idService);
    }

    @Test
    void returnsStreamTitle_whenValidDataExists() throws Exception {
        when(idService.getTwitchUserId("StreamerName")).thenReturn("98765");

        try (InputStream json = getClass().getResourceAsStream("/twitch-vods.json")) {
            VideoList videoList = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(json, VideoList.class);

            when(twitchHelix.getVideos(anyString(), any(), anyString(), any(), any(), any(), any(), any(), anyInt(), any(), any())
                    .execute()).thenReturn(videoList);
        }

        List<String> results = streamService.getTwitchStreamsInfo("StreamerName");
        assertTrue(results.getFirst().startsWith("Mock Twitch Stream"));
    }

    @Test
    void returnsNull_whenUserIdIsNull() {
        when(idService.getTwitchUserId("Nobody")).thenReturn(null);
        assertNull(streamService.getTwitchStreamsInfo("Nobody"));
    }

    @Test
    void returnsNull_whenHelixThrowsException() {
        when(idService.getTwitchUserId("BadGuy")).thenReturn("98765");

        when(twitchHelix.getVideos(anyString(), any(), anyString(), any(), any(), any(), any(), any(), anyInt(), any(), any())
                .execute()).thenThrow(new RuntimeException("fail"));

        assertNull(streamService.getTwitchStreamsInfo("BadGuy"));
    }

    @Test
    void returnsNoVODsMessage_whenStreamListIsEmpty() {
        RetrieveStreamsService spyService = spy(streamService);
        doReturn(null).when(spyService).getTwitchStreamsInfo("NoOne");

        doReturn(Collections.emptyList()).when(spyService).getTwitchStreamsInfo("Nobody");
        assertEquals("No VODs found for Nobody", spyService.getFormattedTwitchVODs("Nobody"));
    }

    @Test
    void formatsOutputCorrectly_whenVODsExist() {
        RetrieveStreamsService spyService = spy(streamService);
        doReturn(List.of("TitleFoo__123__thumbUrl")).when(spyService).getTwitchStreamsInfo("Foo");

        String output = spyService.getFormattedTwitchVODs("Foo");
        assertTrue(output.startsWith("Recent Twitch VODs:\n1. Title: TitleFoo\n"));
    }

    @Test
    void returnsNull_whenNoVideosExist() throws Exception {
        when(idService.getTwitchUserId("StreamerName")).thenReturn("98765");

        VideoList emptyList = new VideoList();
        Field videoField = VideoList.class.getDeclaredField("videos");
        videoField.setAccessible(true);
        videoField.set(emptyList, Collections.emptyList());

        when(twitchHelix.getVideos(anyString(), isNull(), anyString(), isNull(), isNull(), isNull(), isNull(), isNull(), anyInt(), isNull(), isNull())
                .execute()).thenReturn(emptyList);

        assertNull(streamService.getTwitchStreamsInfo("StreamerName"));
    }
}