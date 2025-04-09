package edu.bsu.cs.channelInfo;

public class StreamerDetailsProvider {
    private final ChannelInfoAggregator aggregator;

    public StreamerDetailsProvider(ChannelInfoAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public ChannelInfo getStreamerDetails(String username, String platform) {
        try {
            return aggregator.getChannelInfo(platform, username);
        } catch (Exception e) {
            return new ChannelInfo("Error retrieving streamer details: " + e.getMessage(), "0");
        }
    }
}
