package edu.bsu.cs.gui.youtube;

class YoutubeVideoEntry {
    final String title;
    final String videoId;
    final String thumbnailUrl;

    YoutubeVideoEntry(String title, String videoId, String thumbnailUrl) {
        this.title = title;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
    }
}