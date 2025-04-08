package edu.bsu.cs.clips;

import edu.bsu.cs.ApiContext;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Clip;
import com.github.twitch4j.helix.domain.ClipList;
import edu.bsu.cs.channelid.TwitchUserIdProvider;

import java.util.Collections;
import java.util.List;

public class TwitchClipsProvider implements ClipsProvider {
    private final ApiContext context;
    private final TwitchUserIdProvider userIdProvider;

    public TwitchClipsProvider(ApiContext context) {
        this.context = context;
        this.userIdProvider = new TwitchUserIdProvider(context);
    }

    @Override
    public List<Clip> fetchClips(String username) throws Exception {
        String broadcasterId = userIdProvider.getUserId(username);
        if (broadcasterId == null || broadcasterId.isEmpty()) {
            return Collections.emptyList();
        }

        TwitchHelix helix = context.twitchClient.getHelix();
        if (helix == null) {
            throw new Exception("TwitchHelix API client not initialized.");
        }

        ClipList clipList = helix.getClips(
                context.twitchAuthToken,
                broadcasterId,
                null,
                null,
                null,
                null,
                10,
                null,
                null,
                null
        ).execute();

        List<Clip> clips = clipList.getData();
        return (clips != null) ? clips : Collections.emptyList();
    }
}
