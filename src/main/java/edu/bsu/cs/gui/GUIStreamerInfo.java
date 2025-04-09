package edu.bsu.cs.gui;

import edu.bsu.cs.channelInfo.ChannelInfo;
import edu.bsu.cs.channelInfo.StreamerDetailsProvider;
import edu.bsu.cs.livestatus.LiveStatusFetcher;
import edu.bsu.cs.profilepicture.ProfilePictureAggregator;
import edu.bsu.cs.streamersearch.StreamerSearchAggregator;

public class GUIStreamerInfo {
    private final StreamerDetailsProvider streamerDetailsProvider;
    private final ProfilePictureAggregator profilePictureAggregator;
    private final LiveStatusFetcher liveStatusProviderAggregator;

    public GUIStreamerInfo(StreamerDetailsProvider detailsProvider, ProfilePictureAggregator pictureAggregator, LiveStatusFetcher liveAggregator) {
        this.streamerDetailsProvider = detailsProvider;
        this.profilePictureAggregator = pictureAggregator;
        this.liveStatusProviderAggregator = liveAggregator;
    }

    public ChannelInfo fetchStreamerDetails(String username, String platform) {
        return streamerDetailsProvider.getStreamerDetails(username, platform);
    }

    public String fetchProfilePicture(String username, String platform) {
        return profilePictureAggregator.getProfilePicture(platform, username);
    }

    public String fetchLiveStatus(String username, String platform) {
        return liveStatusProviderAggregator.getLiveStatus(username, platform);
    }
}
