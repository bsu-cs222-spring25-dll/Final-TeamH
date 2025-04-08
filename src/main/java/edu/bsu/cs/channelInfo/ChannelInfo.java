package edu.bsu.cs.channelInfo;

public class ChannelInfo {
    private final String bio;
    private final String count;

    public ChannelInfo(String bio, String count) {
        this.bio = bio;
        this.count = count;
    }

    @Override
    public String toString() {
        return "Bio:\n" + bio + "\nCount: " + count;
    }
}
