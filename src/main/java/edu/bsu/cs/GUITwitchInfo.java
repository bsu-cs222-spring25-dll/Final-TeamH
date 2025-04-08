package edu.bsu.cs;

import com.github.twitch4j.helix.domain.ClipList;
import com.github.twitch4j.helix.domain.StreamScheduleResponse;
import com.github.twitch4j.helix.domain.VideoList;
import edu.bsu.cs.clips.TwitchClipsProvider;

import java.util.ArrayList;

public class GUITwitchInfo {
    private final RetrieveStreamsService retrieveStreamsService;
    private final RetrieveClips retrieveClips;
    private final RetrieveScheduledStreams retrieveScheduledStreams;

    public GUITwitchInfo(RetrieveStreamsService retrieveStreamsService, RetrieveClips retrieveClips, RetrieveScheduledStreams retrieveScheduledStreams) {
        this.retrieveStreamsService = retrieveStreamsService;
        this.retrieveClips = retrieveClips;
        this.retrieveScheduledStreams = retrieveScheduledStreams;
    }

    public ArrayList<String> fetchTwitchVODs(String username){
        return this.retrieveStreamsService.getTwitchStreamsInfo(username);
    }
    public ArrayList<String> fetchTwitchClips(String username){
        return retrieveClips.getTwitchClipsInfo(username);
    }
    public ArrayList<String> fetchStreamSchedule(String username){
        return retrieveScheduledStreams.getStreamSchedule(username);
    }
}
