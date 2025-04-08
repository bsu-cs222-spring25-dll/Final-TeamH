package edu.bsu.cs.videos;

import com.google.api.services.youtube.model.SearchResult;
import java.util.List;

public class YoutubeVideosFormatter {

    public static String formatVideoList(List<SearchResult> videos) {
        StringBuilder builder = new StringBuilder("\nRecent Videos:\n");
        int index = 1;
        for (SearchResult video : videos) {
            builder.append(index++).append(". ")
                    .append(video.getSnippet().getTitle()).append("\n")
                    .append("Published At: ").append(video.getSnippet().getPublishedAt()).append("\n")
                    .append("Watch here: https://www.youtube.com/watch?v=")
                    .append(video.getId().getVideoId()).append("\n")
                    .append("----------------------\n");
        }
        return builder.toString();
    }
}
