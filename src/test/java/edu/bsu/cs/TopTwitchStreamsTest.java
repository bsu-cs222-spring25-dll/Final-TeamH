package edu.bsu.cs;

import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.GameTopList;
import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.TopTwitchStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TopTwitchStreamsTest {

    private TwitchHelix twitchHelix;
    private TopTwitchStreams streamService;

    @BeforeEach
    void setUp() {
        ITwitchClient twitchClient = mock(ITwitchClient.class);
        twitchHelix = mock(TwitchHelix.class, Answers.RETURNS_DEEP_STUBS);
        ApiContext apiContext = new ApiContext(twitchClient, null, null, null);
        when(twitchClient.getHelix()).thenReturn(twitchHelix);

        streamService = new TopTwitchStreams(apiContext);
    }

    @Test
    void getTopGamesInfo_returnsNull_onHelixException() {
        when(twitchHelix.getTopGames(null, null, null, "10").execute())
                .thenThrow(new RuntimeException("fail"));

        assertNull(streamService.getTopGamesInfo());
    }

    @Test
    void getTopGamesInfo_formatsCorrectly() {
        GameTopList topGames = mock(GameTopList.class);
        when(twitchHelix.getTopGames(null, null, null, "10").execute()).thenReturn(topGames);

        Game game1 = mock(Game.class);
        Game game2 = mock(Game.class);
        when(game1.getId()).thenReturn("id1");
        when(game1.getName()).thenReturn("Name1");
        when(game1.getBoxArtUrl(180, 240)).thenReturn("url1");
        when(game2.getId()).thenReturn("id2");
        when(game2.getName()).thenReturn("Name2");
        when(game2.getBoxArtUrl(180, 240)).thenReturn("url2");

        when(topGames.getGames()).thenReturn(List.of(game1, game2));

        List<String> result = streamService.getTopGamesInfo();
        assertEquals("id1__id2__", result.getFirst());
    }

    @Test
    void getTopStreamsForCategoryInfo_defaultBranch_formatsCorrectly() {
        Stream stream = mock(Stream.class);
        StreamList streamList = mock(StreamList.class);
        when(stream.getUserId()).thenReturn("u1");
        when(stream.getTitle()).thenReturn("T1");
        when(stream.getThumbnailUrl(170, 96)).thenReturn("thumb1");
        when(streamList.getStreams()).thenReturn(List.of(stream));
        when(twitchHelix.getStreams(null, null, null, 10, null, null, null, null).execute())
                .thenReturn(streamList);

        User user = mock(User.class);
        when(user.getLogin()).thenReturn("login1");
        when(twitchHelix.getUsers(null, List.of("u1"), null).execute().getUsers())
                .thenReturn(List.of(user));

        List<String> result = streamService.getTopStreamsForCategoryInfo(null);
        assertEquals("T1__login1__thumb1", result.getFirst());
    }

    @Test
    void getTopStreamsForCategoryInfo_withCategory_formatsCorrectly() {
        Stream stream = mock(Stream.class);
        StreamList streamList = mock(StreamList.class);
        when(stream.getUserId()).thenReturn("u2");
        when(stream.getTitle()).thenReturn("T2");
        when(stream.getThumbnailUrl(170, 96)).thenReturn("thumb2");
        when(streamList.getStreams()).thenReturn(List.of(stream));
        when(twitchHelix.getStreams(null, null, null, 10, List.of("gameX"), null, null, null).execute())
                .thenReturn(streamList);

        User user = mock(User.class);
        when(user.getLogin()).thenReturn("login2");
        when(twitchHelix.getUsers(null, List.of("u2"), null).execute().getUsers())
                .thenReturn(List.of(user));

        List<String> result = streamService.getTopStreamsForCategoryInfo("gameX");
        assertEquals("T2__login2__thumb2", result.getFirst());
    }

    @Test
    void getTopStreamsForCategoryInfo_withCategory_returnsNullOnException() {
        when(twitchHelix.getStreams(null, null, null, 10, List.of("gameX"), null, null, null).execute())
                .thenThrow(new RuntimeException("boom"));

        assertNull(streamService.getTopStreamsForCategoryInfo("gameX"));
    }

    @Test
    void getTwitchUsername_returnsLogin_whenUserFound() {
        User user = mock(User.class);
        when(user.getLogin()).thenReturn("login");
        when(twitchHelix.getUsers(null, List.of("u3"), null).execute().getUsers())
                .thenReturn(List.of(user));

        assertEquals("login", streamService.getTwitchUsername("u3"));
    }

    @Test
    void getTwitchUsername_returnsNull_whenNoUsers() {
        when(twitchHelix.getUsers(null, List.of("u4"), null).execute().getUsers())
                .thenReturn(Collections.emptyList());

        assertNull(streamService.getTwitchUsername("u4"));
    }
}