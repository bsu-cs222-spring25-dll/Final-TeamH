package edu.bsu.cs.clips;

import edu.bsu.cs.ApiContext;
import edu.bsu.cs.ObtainStreamerID;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Clip;
import com.github.twitch4j.helix.domain.ClipList;

import java.util.Collections;
import java.util.List;

public class TwitchClipsProvider implements ClipsProvider {
    private final ApiContext context;
    private final ObtainStreamerID obtainStreamerID;

    public TwitchClipsProvider(ApiContext context) {
        this.context = context;
        this.obtainStreamerID = new ObtainStreamerID(context);
    }

    @Override
    public List<Clip> fetchClips(String username) throws Exception {
        String broadcasterId = obtainStreamerID.getTwitchUserId(username);
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
