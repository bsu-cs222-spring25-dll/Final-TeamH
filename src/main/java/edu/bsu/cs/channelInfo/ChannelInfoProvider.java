package edu.bsu.cs.channelInfo;

public interface ChannelInfoProvider {
    ChannelInfo getChannelInfo(String username) throws Exception;
}
