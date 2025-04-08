package edu.bsu.cs.channelid;

public interface ChannelIdProvider {
    String getUserId(String username) throws Exception;
}
