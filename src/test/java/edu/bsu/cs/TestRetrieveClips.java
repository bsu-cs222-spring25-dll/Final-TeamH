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

    private ObtainStreamerID mockIdService;
    private RetrieveClips clipService;
    private TwitchHelix helix;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext context = mock(ApiContext.class);
        TwitchClient mockClient = mock(TwitchClient.class);
        helix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientField = ApiContext.class.getDeclaredField("twitchClient");
        clientField.setAccessible(true);
        clientField.set(context, mockClient);

        Field tokenField = ApiContext.class.getDeclaredField("twitchAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(context, "dummy-token");

        when(mockClient.getHelix()).thenReturn(helix);

        mockIdService = mock(ObtainStreamerID.class);
        clipService = new RetrieveClips(context);

        Field idField = RetrieveClips.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(clipService, mockIdService);
    }

    @Test
    void returnsNull_whenUserIdIsNull() {
        when(mockIdService.getTwitchUserId("unknown")).thenReturn(null);
        assertNull(clipService.getTwitchClipsInfo("unknown"));
    }

    @Test
    void returnsNull_whenHelixThrowsException() {
        when(mockIdService.getTwitchUserId("errorUser")).thenReturn("ID0");
        when(helix.getClips(anyString(), anyString(), any(), any(), any(), any(), anyInt(), any(), any(), anyBoolean())
                .execute()).thenThrow(new RuntimeException("fail"));
        assertNull(clipService.getTwitchClipsInfo("errorUser"));
    }

    @Test
    void returnsNull_whenNoClipsExist() throws Exception {
        when(mockIdService.getTwitchUserId("noClips")).thenReturn("ID1");

        ClipList emptyClips = new ClipList();
        Field dataField = ClipList.class.getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(emptyClips, Collections.emptyList());

        when(helix.getClips(anyString(), anyString(), any(), any(), any(), any(), anyInt(), any(), any(), anyBoolean())
                .execute()).thenReturn(emptyClips);

        assertNull(clipService.getTwitchClipsInfo("noClips"));
    }

    @Test
    void returnsClipList_whenClipsAreAvailable() throws Exception {
        when(mockIdService.getTwitchUserId("someUser")).thenReturn("ID2");

        Clip clip = new Clip();
        Field title = Clip.class.getDeclaredField("title");
        Field id = Clip.class.getDeclaredField("id");
        Field thumb = Clip.class.getDeclaredField("thumbnailUrl");
        title.setAccessible(true);
        id.setAccessible(true);
        thumb.setAccessible(true);
        title.set(clip, "ClipTitle");
        id.set(clip, "ClipID");
        thumb.set(clip, "ThumbURL");

        ClipList clipList = new ClipList();
        Field data = ClipList.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(clipList, List.of(clip));

        when(helix.getClips(anyString(), anyString(), any(), any(), any(), any(), anyInt(), any(), any(), anyBoolean())
                .execute()).thenReturn(clipList);

        List<String> results = clipService.getTwitchClipsInfo("someUser");
        assertEquals(1, results.size());
    }

    @Test
    void returnsNoClipsMessage_whenFormattedResultIsNull() {
        RetrieveClips spyService = spy(clipService);
        doReturn(null).when(spyService).getTwitchClipsInfo("none");
        assertEquals("No clips found for none", spyService.getFormattedTwitchClips("none"));
    }

    @Test
    void formatsClipOutputCorrectly_whenClipsExist() {
        RetrieveClips spyService = spy(clipService);
        doReturn(List.of("TitleFoo__123__thumbUrl")).when(spyService).getTwitchClipsInfo("Foo");

        String result = spyService.getFormattedTwitchClips("Foo");
        assertTrue(result.contains("1. Title: TitleFoo"));
    }
}