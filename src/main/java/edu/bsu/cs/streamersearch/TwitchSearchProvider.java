package edu.bsu.cs.streamersearch;

import com.github.twitch4j.helix.domain.UserList;
import edu.bsu.cs.api.ApiContext;

import java.util.Collections;
import java.util.List;

public class TwitchSearchProvider implements StreamerSearchProvider {
    private final ApiContext context;

    public TwitchSearchProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public List<String> searchStreamer(String username) {
        try {
            UserList userList = context.twitchClient.getHelix()
                    .getUsers(context.twitchAuthToken, null, Collections.singletonList(username))
                    .execute();

            if (userList == null || userList.getUsers().isEmpty()) {
                return Collections.emptyList();
            }

            return Collections.singletonList(userList.getUsers().get(0).getDisplayName());
        } catch (Exception e) {
            System.out.println("Twitch Search Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
