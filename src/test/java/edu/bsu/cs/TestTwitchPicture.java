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

    private ApiContext mockContext;
    private TwitchHelix mockHelix;
    private ProfilePictureService service;

    @BeforeEach
    void setUp() throws Exception {
        mockContext = mock(ApiContext.class);
        TwitchClient mockClient = mock(TwitchClient.class);
        mockHelix = mock(TwitchHelix.class, RETURNS_DEEP_STUBS);

        Field clientField = ApiContext.class.getDeclaredField("twitchClient");
        clientField.setAccessible(true);
        clientField.set(mockContext, mockClient);

        when(mockClient.getHelix()).thenReturn(mockHelix);

        service = new ProfilePictureService(mockContext);
    }

    @Test
    void returnsProfileImageUrl_whenUserExists() throws Exception {
        User fakeUser = new User();
        Field urlField = User.class.getDeclaredField("profileImageUrl");
        urlField.setAccessible(true);
        urlField.set(fakeUser, "http://example.com/avatar.png");

        UserList fakeList = new UserList();
        Field usersField = UserList.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(fakeList, List.of(fakeUser));

        when(mockHelix
                .getUsers(any(), any(), anyList())
                .execute()
        ).thenReturn(fakeList);

        String result = service.getProfilePicture("alice", "Twitch");
        assertEquals("http://example.com/avatar.png", result);
    }

    @Test
    void returnsNull_whenNoUsersReturned() throws Exception {
        UserList emptyList = new UserList();
        Field usersField = UserList.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(emptyList, Collections.emptyList());

        when(mockHelix
                .getUsers(any(), any(), anyList())
                .execute()
        ).thenReturn(emptyList);

        assertNull(service.getProfilePicture("bob", "Twitch"));
    }

    @Test
    void returnsNull_whenHelixThrows() {
        when(mockHelix
                .getUsers(any(), any(), anyList())
                .execute()
        ).thenThrow(new RuntimeException("failure"));

        assertNull(service.getProfilePicture("carol", "Twitch"));
    }

    @Test
    void returnsNull_forNonTwitchPlatform() {
        assertNull(service.getProfilePicture("doesntmatter", "NotTwitch"));
    }
}
