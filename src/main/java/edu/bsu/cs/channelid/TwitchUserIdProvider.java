package edu.bsu.cs.channelid;

import edu.bsu.cs.ApiContext;
import com.github.twitch4j.helix.domain.UserList;
import java.util.List;

public class TwitchUserIdProvider implements ChannelIdProvider {
    private final ApiContext context;

    public TwitchUserIdProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public String getUserId(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                System.out.println("Invalid username provided.");
                return null;
            }
            UserList userList = context.twitchClient.getHelix()
                    .getUsers(context.twitchAuthToken, null, List.of(username))
                    .execute();

            if (userList == null || userList.getUsers().isEmpty()) {
                System.out.println("No user found with username: " + username);
                return null;
            }
            String userId = userList.getUsers().get(0).getId();
            System.out.println("Retrieved Twitch User ID: " + userId);
            return userId;
        } catch (Exception e) {
            System.out.println("Failed to retrieve Twitch user ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
