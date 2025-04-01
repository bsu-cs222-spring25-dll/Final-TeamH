package edu.bsu.cs;

public class GUIStreamer {
    private String name;
    private String status;
    private String streamUrl;

    public void Streamer(String name, String status, String streamUrl) {
        this.name = name;
        this.status = status;
        this.streamUrl = streamUrl;
    }

    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getStreamUrl() { return streamUrl; }
}

