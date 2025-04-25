package edu.bsu.cs;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.services.ProfilePictureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestTwitchPicture {

    private TwitchHelix mockHelix;
    private ProfilePictureService pictureService;

    @BeforeEach
    void setUp() throws Exception {
        ApiContext apiContext = mock(ApiContext.class);
        TwitchClient mockClient = mock(TwitchClient.class);
        mockHelix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientField = ApiContext.class.getDeclaredField("twitchClient");
        clientField.setAccessible(true);
        clientField.set(apiContext, mockClient);

        when(mockClient.getHelix()).thenReturn(mockHelix);

        pictureService = new ProfilePictureService(apiContext);
    }

    @Test
    void returnsProfileUrl_whenUserExists() throws Exception {
        User user = new User();
        Field profileField = User.class.getDeclaredField("profileImageUrl");
        profileField.setAccessible(true);
        profileField.set(user, "http://example.com/avatar.png");

        UserList userList = new UserList();
        Field usersField = UserList.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(userList, List.of(user));

        when(mockHelix.getUsers(any(), any(), anyList()).execute()).thenReturn(userList);

        String result = pictureService.getProfilePicture("alice", "Twitch");
        assertEquals("http://example.com/avatar.png", result);
    }

    @Test
    void returnsNull_whenUserListIsEmpty() throws Exception {
        UserList userList = new UserList();
        Field usersField = UserList.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(userList, Collections.emptyList());

        when(mockHelix.getUsers(any(), any(), anyList()).execute()).thenReturn(userList);
        assertNull(pictureService.getProfilePicture("bob", "Twitch"));
    }

    @Test
    void returnsNull_whenExceptionIsThrown() {
        when(mockHelix.getUsers(any(), any(), anyList()).execute()).thenThrow(new RuntimeException("fail"));
        assertNull(pictureService.getProfilePicture("carol", "Twitch"));
    }

    @Test
    void returnsNull_whenPlatformIsNotTwitch() {
        assertNull(pictureService.getProfilePicture("irrelevant", "OtherPlatform"));
    }
}