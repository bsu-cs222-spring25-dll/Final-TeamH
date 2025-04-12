package edu.bsu.cs;

import com.github.twitch4j.helix.domain.Video;
import com.github.twitch4j.helix.domain.VideoList;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TwitchStreamsProviderTest {

    @Test
    void testJsonParsingReturnsNonNullVideoList() {
        String json = readJsonFile("/twitch_streams_sample.json");

        VideoList videoList = JsonParserUtility.parseVideoList(json); // hypothetical helper
        assertNotNull(videoList);
    }

    @Test
    void testVideoListContainsExpectedVideoTitle() {
        String json = readJsonFile("/twitch_streams_sample.json");

        VideoList videoList = JsonParserUtility.parseVideoList(json);
        List<Video> videos = videoList.getVideos();

        assertEquals("hablamos y le damos a Little Nightmares 1", videos.get(0).getTitle());
    }

    @Test
    void testThumbnailUrlIsParsedCorrectly() {
        String json = readJsonFile("/twitch_streams_sample.json");

        VideoList videoList = JsonParserUtility.parseVideoList(json);
        String thumbnailUrl = videoList.getVideos().get(0).getThumbnailUrl();

        assertTrue(thumbnailUrl.contains("{width}x{height}"));
    }

    private String readJsonFile(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            assertNotNull(is, "Test resource file not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            fail("Failed to read test JSON file: " + e.getMessage());
            return null;
        }
    }
}
