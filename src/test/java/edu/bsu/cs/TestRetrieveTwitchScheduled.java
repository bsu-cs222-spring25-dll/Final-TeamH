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

    private ObtainStreamerID mockIdService;
    private RetrieveScheduledStreams scheduledService;
    private TwitchHelix mockHelix;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext apiContext = mock(ApiContext.class);
        TwitchClient mockClient = mock(TwitchClient.class, RETURNS_DEEP_STUBS);
        mockHelix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientField = ApiContext.class.getDeclaredField("twitchClient");
        clientField.setAccessible(true);
        clientField.set(apiContext, mockClient);

        Field tokenField = ApiContext.class.getDeclaredField("twitchAuthToken");
        tokenField.setAccessible(true);
        tokenField.set(apiContext, "dummy-token");

        when(mockClient.getHelix()).thenReturn(mockHelix);

        mockIdService = mock(ObtainStreamerID.class);
        scheduledService = new RetrieveScheduledStreams(apiContext);

        Field idField = RetrieveScheduledStreams.class.getDeclaredField("obtainStreamerID");
        idField.setAccessible(true);
        idField.set(scheduledService, mockIdService);
    }

    @Test
    void returnsNull_whenBroadcasterIdIsNull() {
        when(mockIdService.getTwitchUserId("unknown")).thenReturn(null);
        assertNull(scheduledService.getTwitchScheduledStreams("unknown"));
    }

    @Test
    void returnsNull_whenBroadcasterIdIsEmpty() {
        when(mockIdService.getTwitchUserId("emptyUser")).thenReturn("");
        assertNull(scheduledService.getTwitchScheduledStreams("emptyUser"));
    }

    @Test
    void returnsNull_whenHelixThrowsException() {
        when(mockIdService.getTwitchUserId("errorUser")).thenReturn("ID1");
        when(mockHelix
                .getChannelStreamSchedule(anyString(), anyString(), any(), any(), any(), any(), anyInt())
                .execute()
        ).thenThrow(new RuntimeException("fail"));
        assertNull(scheduledService.getTwitchScheduledStreams("errorUser"));
    }

    @Test
    void returnsNull_whenNoScheduledSegments() throws Exception {
        when(mockIdService.getTwitchUserId("noSchedule")).thenReturn("ID2");

        StreamSchedule emptySchedule = new StreamSchedule();
        Field segmentsField = StreamSchedule.class.getDeclaredField("segments");
        segmentsField.setAccessible(true);
        segmentsField.set(emptySchedule, Collections.emptyList());

        StreamScheduleResponse response = new StreamScheduleResponse();
        Field scheduleField = StreamScheduleResponse.class.getDeclaredField("schedule");
        scheduleField.setAccessible(true);
        scheduleField.set(response, emptySchedule);

        when(mockHelix
                .getChannelStreamSchedule(anyString(), anyString(), any(), any(), any(), any(), anyInt())
                .execute()
        ).thenReturn(response);

        assertNull(scheduledService.getTwitchScheduledStreams("noSchedule"));
    }

    @Test
    void returnsFormattedSegment_whenSegmentExists() throws Exception {
        when(mockIdService.getTwitchUserId("validUser")).thenReturn("ID3");

        ScheduledSegment segment = new ScheduledSegment();
        Field title = ScheduledSegment.class.getDeclaredField("title");
        Field id = ScheduledSegment.class.getDeclaredField("id");
        Field start = ScheduledSegment.class.getDeclaredField("startTime");
        Field end = ScheduledSegment.class.getDeclaredField("endTime");

        title.setAccessible(true);
        id.setAccessible(true);
        start.setAccessible(true);
        end.setAccessible(true);

        title.set(segment, "GameNight");
        id.set(segment, "Seg42");
        start.set(segment, OffsetDateTime.parse("2023-10-01T18:00:00Z").toInstant());
        end.set(segment, OffsetDateTime.parse("2023-10-01T19:00:00Z").toInstant());

        StreamSchedule schedule = new StreamSchedule();
        Field segmentList = StreamSchedule.class.getDeclaredField("segments");
        segmentList.setAccessible(true);
        segmentList.set(schedule, List.of(segment));

        StreamScheduleResponse response = new StreamScheduleResponse();
        Field scheduleResp = StreamScheduleResponse.class.getDeclaredField("schedule");
        scheduleResp.setAccessible(true);
        scheduleResp.set(response, schedule);

        when(mockHelix
                .getChannelStreamSchedule(anyString(), anyString(), any(), any(), any(), any(), anyInt())
                .execute()
        ).thenReturn(response);

        List<String> results = scheduledService.getTwitchScheduledStreams("validUser");
        assertEquals(1, results.size());
    }
}