package edu.bsu.cs;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Clip;
import com.github.twitch4j.helix.domain.ClipList;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import edu.bsu.cs.services.RetrieveClips;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestRetrieveClips {

    private ApiContext contextMock;
    private ObtainStreamerID idLookupMock;
    private RetrieveClips service;
    private TwitchHelix helixMock;

    @BeforeEach
    void setUp() throws Exception {
        contextMock = mock(ApiContext.class);
        TwitchClient clientMock = mock(TwitchClient.class);
        helixMock = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientField = ApiContext.class.getDeclaredField("twitchClient");
        clientField.setAccessible(true);
        clientField.set(contextMock, clientMock);

        Field tokenField = ApiContext.class.getDeclaredField("twitchAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(contextMock, "dummy-token");

        when(clientMock.getHelix()).thenReturn(helixMock);

        idLookupMock = mock(ObtainStreamerID.class);
        service = new RetrieveClips(contextMock);
        Field idField = RetrieveClips.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(service, idLookupMock);
    }

    @Test
    void getClips_returnsNull_whenUserIdIsNull() {
        when(idLookupMock.getTwitchUserId("unknown")).thenReturn(null);
        assertNull(service.getTwitchClipsInfo("unknown"));
    }

    @Test
    void getClips_returnsNull_whenHelixThrows() {
        when(idLookupMock.getTwitchUserId("errorUser")).thenReturn("ID0");
        when(helixMock.getClips(
                anyString(), anyString(), any(), any(), any(), any(),
                anyInt(), any(), any(), anyBoolean()
        ).execute()).thenThrow(new RuntimeException("boom"));
        assertNull(service.getTwitchClipsInfo("errorUser"));
    }

    @Test
    void getClips_returnsNull_whenNoClips() throws Exception {
        when(idLookupMock.getTwitchUserId("noClips")).thenReturn("ID1");
        ClipList emptyList = new ClipList();
        Field dataField = ClipList.class.getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(emptyList, Collections.emptyList());
        when(helixMock.getClips(
                anyString(), anyString(), any(), any(), any(), any(),
                anyInt(), any(), any(), anyBoolean()
        ).execute()).thenReturn(emptyList);
        assertNull(service.getTwitchClipsInfo("noClips"));
    }

    @Test
    void getClips_returnsList_whenClipsExist() throws Exception {
        when(idLookupMock.getTwitchUserId("someUser")).thenReturn("ID2");
        ClipList clipList = new ClipList();
        Clip clip = new Clip();

        Field titleF = Clip.class.getDeclaredField("title");
        titleF.setAccessible(true);
        titleF.set(clip, "ClipTitle");
        Field idF = Clip.class.getDeclaredField("id");
        idF.setAccessible(true);
        idF.set(clip, "ClipID");
        Field thumbF = Clip.class.getDeclaredField("thumbnailUrl");
        thumbF.setAccessible(true);
        thumbF.set(clip, "ThumbURL");
        Field listF = ClipList.class.getDeclaredField("data");
        listF.setAccessible(true);
        listF.set(clipList, List.of(clip));

        when(helixMock.getClips(
                anyString(), anyString(), any(), any(), any(), any(),
                anyInt(), any(), any(), anyBoolean()
        ).execute()).thenReturn(clipList);

        List<String> result = service.getTwitchClipsInfo("someUser");
        assertEquals(1, result.size());
    }

    @Test
    void getClips_returnsNoClipsMessage_whenEmpty() {
        RetrieveClips spy = spy(service);
        doReturn(null).when(spy).getTwitchClipsInfo("none");
        assertEquals("No clips found for none", spy.getFormattedTwitchClips("none"));
    }

    @Test
    void getClips_formatsCorrectly() {
        RetrieveClips spy = spy(service);
        doReturn(List.of("TitleFoo__123__thumbUrl")).when(spy).getTwitchClipsInfo("Foo");
        String output = spy.getFormattedTwitchClips("Foo");
        assertTrue(output.contains("1. Title: TitleFoo"));
    }
}