package edu.bsu.cs;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class ApiExample {
    // Your API Key here
    private static final String API_KEY = "AIzaSyBoizCiqHj1I7MMT6j3Z96sVAw92OygejM";

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Build and return an authorized API client service using the API Key.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
        return youtubeService;
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException
     */
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Playlists.List request = youtubeService.playlists()
                .list(Arrays.asList("snippet", "contentDetails"));

        // Replace with your desired channel ID
        PlaylistListResponse response = request.setChannelId("UC_x5XG1OV2P6uZZ5FSM9Ttw")
                .setMaxResults(25L)
                .setKey(API_KEY)  // Set your API key here
                .execute();

        // Print the response
        System.out.println(response);
    }
}