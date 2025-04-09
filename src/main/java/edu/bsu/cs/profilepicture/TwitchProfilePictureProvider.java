package edu.bsu.cs.profilepicture;

import com.github.twitch4j.helix.domain.User;
import edu.bsu.cs.api.ApiContext;

import java.util.List;

public class TwitchProfilePictureProvider implements ProfilePictureProvider {
    private final ApiContext context;

    public TwitchProfilePictureProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public String getProfilePicture(String username) {
        try {
            return context.twitchClient.getHelix()
                    .getUsers(null, null, List.of(username))
                    .execute()
                    .getUsers()
                    .stream()
                    .findFirst()
                    .map(User::getProfileImageUrl)
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching Twitch profile picture: " + e.getMessage());
            return null;
        }
    }
}
