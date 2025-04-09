package edu.bsu.cs.livestatus;

import edu.bsu.cs.api.ApiContext;
import com.github.twitch4j.helix.TwitchHelix;

import java.util.Collections;

public class TwitchLiveStatusProvider implements LiveStatusProvider {
    private final ApiContext context;

    public TwitchLiveStatusProvider(ApiContext context) {
        this.context = context;
    }

    @Override
    public String getLiveStatus(String username) {
        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            if (helix == null) {
                return "Error: TwitchHelix API client not initialized.";
            }

            var response = helix.getStreams(
                    context.twitchAuthToken,
                    null,
                    null,
                    1,
                    null,
                    null,
                    null,
                    Collections.singletonList(username)
            ).execute();

            if (response != null && response.getStreams() != null && !response.getStreams().isEmpty()) {
                var stream = response.getStreams().get(0);
                return username + " is LIVE on Twitch!\n" +
                        "Stream Title: " + stream.getTitle() + "\n" +
                        "Watch: https://www.twitch.tv/" + username;
            } else {
                return username + " is NOT live on Twitch.";
            }
        } catch (Exception e) {
            return "Error checking Twitch live status: " + e.getMessage();
        }
    }
}
