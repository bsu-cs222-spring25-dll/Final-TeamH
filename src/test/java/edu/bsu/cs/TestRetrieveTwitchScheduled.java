package edu.bsu.cs;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ScheduledSegment;
import com.github.twitch4j.helix.domain.StreamSchedule;
import com.github.twitch4j.helix.domain.StreamScheduleResponse;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ObtainStreamerID;
import edu.bsu.cs.services.RetrieveScheduledStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestRetrieveTwitchScheduled {

    private ApiContext contextMock;
    private ObtainStreamerID idLookupMock;
    private RetrieveScheduledStreams streamsMock;
    private TwitchHelix helixMock;

    @BeforeEach
    void setUp() throws Exception {
        contextMock = mock(ApiContext.class);
        TwitchClient clientMock = mock(TwitchClient.class, RETURNS_DEEP_STUBS);
        helixMock = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientField = ApiContext.class.getDeclaredField("twitchClient");
        clientField.setAccessible(true);
        clientField.set(contextMock, clientMock);

        Field tokenField = ApiContext.class.getDeclaredField("twitchAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(contextMock, "dummy-token");

        when(clientMock.getHelix()).thenReturn(helixMock);

        idLookupMock = mock(ObtainStreamerID.class);
        streamsMock = new RetrieveScheduledStreams(contextMock);

        Field idField = RetrieveScheduledStreams.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(streamsMock, idLookupMock);
    }

    @Test
    void returnsNull_whenBroadcasterIdIsNull() {
        when(idLookupMock.getTwitchUserId("unknown")).thenReturn(null);
        assertNull(streamsMock.getTwitchScheduledStreams("unknown"));
    }

    @Test
    void returnsNull_whenBroadcasterIdIsEmpty() {
        when(idLookupMock.getTwitchUserId("emptyUser")).thenReturn("");
        assertNull(streamsMock.getTwitchScheduledStreams("emptyUser"));
    }

    @Test
    void returnsNull_whenHelixThrowsException() {
        when(idLookupMock.getTwitchUserId("errorUser")).thenReturn("ID1");
        when(helixMock
                .getChannelStreamSchedule(anyString(), anyString(), any(), any(), any(), any(), anyInt())
                .execute()
        ).thenThrow(new RuntimeException("fail"));
        assertNull(streamsMock.getTwitchScheduledStreams("errorUser"));
    }

    @Test
    void returnsNull_whenNoScheduledSegments() throws Exception {
        when(idLookupMock.getTwitchUserId("noSchedule")).thenReturn("ID2");

        StreamSchedule emptySchedule = new StreamSchedule();
        Field segmentsField = StreamSchedule.class.getDeclaredField("segments");
        segmentsField.setAccessible(true);
        segmentsField.set(emptySchedule, Collections.emptyList());

        StreamScheduleResponse emptyResponse = new StreamScheduleResponse();
        Field scheduleField = StreamScheduleResponse.class.getDeclaredField("schedule");
        scheduleField.setAccessible(true);
        scheduleField.set(emptyResponse, emptySchedule);

        when(helixMock
                .getChannelStreamSchedule(anyString(), anyString(), any(), any(), any(), any(), anyInt())
                .execute()
        ).thenReturn(emptyResponse);

        assertNull(streamsMock.getTwitchScheduledStreams("noSchedule"));
    }

    @Test
    void returnsOneFormattedEntry_whenSegmentExists() throws Exception {
        when(idLookupMock.getTwitchUserId("validUser")).thenReturn("ID3");

        ScheduledSegment segment = new ScheduledSegment();
        Field titleField = ScheduledSegment.class.getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(segment, "GameNight");
        Field idField = ScheduledSegment.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(segment, "Seg42");
        Field startField = ScheduledSegment.class.getDeclaredField("startTime");
        startField.setAccessible(true);
        startField.set(segment, OffsetDateTime.parse("2023-10-01T18:00:00Z").toInstant());
        Field endField = ScheduledSegment.class.getDeclaredField("endTime");
        endField.setAccessible(true);
        endField.set(segment, OffsetDateTime.parse("2023-10-01T19:00:00Z").toInstant());

        StreamSchedule schedule = new StreamSchedule();
        Field segmentsListField = StreamSchedule.class.getDeclaredField("segments");
        segmentsListField.setAccessible(true);
        segmentsListField.set(schedule, List.of(segment));

        StreamScheduleResponse response = new StreamScheduleResponse();
        Field scheduleRespField = StreamScheduleResponse.class.getDeclaredField("schedule");
        scheduleRespField.setAccessible(true);
        scheduleRespField.set(response, schedule);

        when(helixMock
                .getChannelStreamSchedule(anyString(), anyString(), any(), any(), any(), any(), anyInt())
                .execute()
        ).thenReturn(response);

        List<String> formattedSegments = streamsMock.getTwitchScheduledStreams("validUser");
        assertEquals(1, formattedSegments.size());
    }
}