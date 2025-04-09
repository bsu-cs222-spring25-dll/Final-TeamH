package edu.bsu.cs.gui;

import edu.bsu.cs.api.ApiContext;
import edu.bsu.cs.clips.TwitchClipsProvider;
import com.github.twitch4j.helix.domain.Clip;

import java.util.ArrayList;
import java.util.List;

public class TwitchClipsFetcher {
    private final TwitchClipsProvider clipsProvider;

    public TwitchClipsFetcher(ApiContext context) {
        this.clipsProvider = new TwitchClipsProvider(context);
    }

    public ArrayList<String> fetchFormattedClips(String username) {
        try {
            List<Clip> clips = clipsProvider.fetchClips(username);
            ArrayList<String> formatted = new ArrayList<>();
            for (Clip clip : clips) {
                String info = clip.getTitle() + "__" +
                        clip.getId() + "__" +
                        clip.getThumbnailUrl();
                formatted.add(info);
            }
            return formatted;
        } catch (Exception e) {
            System.err.println("Error fetching Twitch clips: " + e.getMessage());
            return null;
        }
    }
}
