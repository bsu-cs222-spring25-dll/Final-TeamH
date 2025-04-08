package edu.bsu.cs.channelInfo;

import edu.bsu.cs.ApiContext;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;

import java.util.Collections;

public class TwitchChannelInfoProvider implements ChannelInfoProvider {
    private final ApiContext context;

    public TwitchChannelInfoProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public ChannelInfo getChannelInfo(String username) {
        try {
            UserList userList = context.twitchClient.getHelix()
                    .getUsers(context.twitchAuthToken, null, Collections.singletonList(username))
                    .execute();
            if (userList == null || userList.getUsers() == null || userList.getUsers().isEmpty()) {
                return null;
            }
            User user = userList.getUsers().get(0);
            String bio = user.getDescription();
            if (bio == null || bio.isEmpty()) {
                bio = "No bio available.";
            }
            long followerCount = context.twitchClient.getHelix()
                    .getChannelFollowers(context.twitchAuthToken, user.getId(), null, null, null)
                    .execute().getTotal();
            return new ChannelInfo(bio, String.valueOf(followerCount));
        } catch (Exception e) {
            System.err.println("Error fetching Twitch info for " + username + ": " + e.getMessage());
            return null;
        }
    }
}
