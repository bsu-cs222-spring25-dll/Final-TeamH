package edu.bsu.cs;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class ApiExample {

    private static final String API_KEY = "AIzaSyBoizCiqHj1I7MMT6j3Z96sVAw92OygejM";
    private static final String APPLICATION_NAME = "YoutubeApi";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static YouTube getService() throws GeneralSecurityException, IOException {
        final YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
        return youtubeService;
    }

    public static String getChannelInfo(YouTube youtubeService, String handle) throws IOException {
        YouTube.Channels.List channelInfoRequest = youtubeService.channels()
                .list(Arrays.asList("snippet,contentDetails,statistics"));
        ChannelListResponse ChannelInformation = channelInfoRequest.setKey(API_KEY)
                .setForHandle("@IShowSpeed")
                .execute();
        System.out.println(ChannelInformation);

        return ChannelInformation.getItems().get(0).getId();//returns the channel id
    }

    public static void getPastStreams(YouTube youtubeService, String channelId) throws IOException {
        YouTube.Search.List pastStreamsRequest = youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(API_KEY)
                .setChannelId(channelId)
                .setType(Arrays.asList("video"))
                .setEventType("completed")
                .setOrder("date") // Order by date
                .setMaxResults(10L); // Fetch up to 10 results
        SearchListResponse pastStreamsInformation = pastStreamsRequest.execute();
        List<SearchResult> results = pastStreamsInformation.getItems();
        System.out.println(results);

    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        System.out.println("\nThis is the channel information: ");
        String channelId = getChannelInfo(youtubeService, "@IShowSpeed");
        System.out.println("\nThese are the past streams: ");
        getPastStreams(youtubeService, channelId);

    }
}