package edu.bsu.cs.gui;

import edu.bsu.cs.scheduledstreams.TwitchScheduleProvider;

import java.util.ArrayList;
import java.util.List;

public class GUITwitchInfo {
    private final TwitchStreamsFetcher streamsFetcher;
    private final TwitchClipsFetcher clipsFetcher;
    private final TwitchScheduleProvider scheduleProvider;

    public GUITwitchInfo(TwitchStreamsFetcher streamsFetcher, TwitchClipsFetcher clipsFetcher, TwitchScheduleProvider scheduleProvider) {
        this.streamsFetcher = streamsFetcher;
        this.clipsFetcher = clipsFetcher;
        this.scheduleProvider = scheduleProvider;
    }

    public ArrayList<String> fetchTwitchVODs(String username) {
        return streamsFetcher.fetchFormattedVODs(username);
    }

    public ArrayList<String> fetchTwitchClips(String username) {
        return clipsFetcher.fetchFormattedClips(username);
    }

    public List<String> fetchStreamSchedule(String username) {
        return scheduleProvider.getStreamSchedule(username);
    }
}
