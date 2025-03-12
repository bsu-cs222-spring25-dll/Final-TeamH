package edu.bsu.cs;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class ApiExample {

    private static final String API_KEY = "AIzaSyBoizCiqHj1I7MMT6j3Z96sVAw92OygejM";
    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CHANNEL_ID = "UCIfAvpeIWGHb0duCkMkmm2Q"; // YouTuber's channel ID



    public static YouTube getService() throws GeneralSecurityException, IOException {
        final YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
        return youtubeService;
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();

        //Gets Channels past 10 streams
        YouTube.Search.List searchRequest = youtubeService.search()
                .list(Arrays.asList("id,snippet"))
                .setKey(API_KEY)
                .setChannelId(CHANNEL_ID)
                .setType(Arrays.asList("video"))
                .setEventType("completed") //completed live streams
                .setOrder("date") // Order by date
                .setMaxResults(10L); // Fetch up to 10 results

        SearchListResponse searchResponse = searchRequest.execute();
        List<SearchResult> results = searchResponse.getItems();

        System.out.println(results);




        // Gets Channels Information
        YouTube.Channels.List request2 = youtubeService.channels()
                .list(Arrays.asList("snippet,contentDetails,statistics"));
        ChannelListResponse response2 = request2.setKey(API_KEY)
                .setForHandle("@MrBeast")
                .execute();
        System.out.println(response2);




    }




}