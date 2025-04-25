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

    private ITwitchClient twitchClient;
    private TwitchHelix helix;
    private ApiContext context;
    private TopTwitchStreams service;

    @BeforeEach
    void setUp() {
        twitchClient = mock(ITwitchClient.class);
        helix = mock(TwitchHelix.class, Answers.RETURNS_DEEP_STUBS);
        context = new ApiContext(twitchClient, null, null, null);
        when(twitchClient.getHelix()).thenReturn(helix);

        service = new TopTwitchStreams(context);
    }

    @Test
    void getTopGamesInfo_returnsNull_onHelixException() {
        when(helix.getTopGames(null, null, null, "10").execute())
                .thenThrow(new RuntimeException("fail"));

        assertNull(service.getTopGamesInfo());
    }

    @Test
    void getTopGamesInfo_formatsCorrectly() {
        GameTopList topGames = mock(GameTopList.class);
        when(helix.getTopGames(null, null, null, "10").execute()).thenReturn(topGames);

        Game g1 = mock(Game.class);
        Game g2 = mock(Game.class);
        when(g1.getId()).thenReturn("id1");
        when(g1.getName()).thenReturn("Name1");
        when(g1.getBoxArtUrl(180, 240)).thenReturn("url1");
        when(g2.getId()).thenReturn("id2");
        when(g2.getName()).thenReturn("Name2");
        when(g2.getBoxArtUrl(180, 240)).thenReturn("url2");

        when(topGames.getGames()).thenReturn(List.of(g1, g2));

        List<String> result = service.getTopGamesInfo();
        assertEquals("id1__id2__", result.get(0));
    }

    @Test
    void getTopStreamsForCategoryInfo_defaultBranch_formatsCorrectly() {
        StreamList streamList = mock(StreamList.class);
        Stream s1 = mock(Stream.class);

        when(s1.getUserId()).thenReturn("u1");
        when(s1.getTitle()).thenReturn("T1");
        when(s1.getThumbnailUrl(170, 96)).thenReturn("thumb1");
        when(streamList.getStreams()).thenReturn(List.of(s1));
        when(helix.getStreams(null, null, null, 10, null, null, null, null).execute())
                .thenReturn(streamList);

        User user = mock(User.class);
        when(user.getLogin()).thenReturn("login1");
        when(helix.getUsers(null, List.of("u1"), null).execute().getUsers())
                .thenReturn(List.of(user));

        List<String> out = service.getTopStreamsForCategoryInfo(null);
        assertEquals("T1__login1__thumb1", out.get(0));
    }

    @Test
    void getTopStreamsForCategoryInfo_withCategory_formatsCorrectly() {
        StreamList streamList = mock(StreamList.class);
        Stream    s1         = mock(Stream.class);

        when(s1.getUserId()).thenReturn("u2");
        when(s1.getTitle()).thenReturn("T2");
        when(s1.getThumbnailUrl(170, 96)).thenReturn("thumb2");
        when(streamList.getStreams()).thenReturn(List.of(s1));
        when(helix.getStreams(null, null, null, 10, List.of("gameX"), null, null, null).execute())
                .thenReturn(streamList);

        User user = mock(User.class);
        when(user.getLogin()).thenReturn("login2");
        when(helix.getUsers(null, List.of("u2"), null).execute().getUsers())
                .thenReturn(List.of(user));

        List<String> out = service.getTopStreamsForCategoryInfo("gameX");
        assertEquals("T2__login2__thumb2", out.get(0));
    }

    @Test
    void getTopStreamsForCategoryInfo_withCategory_returnsNullOnException() {
        when(helix.getStreams(null, null, null, 10, List.of("gameX"), null, null, null).execute())
                .thenThrow(new RuntimeException("boom"));

        assertNull(service.getTopStreamsForCategoryInfo("gameX"));
    }

    @Test
    void getTwitchUsername_returnsLogin_whenUserFound() {
        User user = mock(User.class);
        when(user.getLogin()).thenReturn("theLogin");
        when(helix.getUsers(null, List.of("u3"), null).execute().getUsers())
                .thenReturn(List.of(user));

        assertEquals("theLogin", service.getTwitchUsername("u3"));
    }

    @Test
    void getTwitchUsername_returnsNull_whenNoUsers() {
        when(helix.getUsers(null, List.of("u4"), null).execute().getUsers())
                .thenReturn(Collections.emptyList());

        assertNull(service.getTwitchUsername("u4"));
    }
}