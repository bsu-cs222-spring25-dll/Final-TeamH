package edu.bsu.cs.services;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.GameTopList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import edu.bsu.cs.api.ApiContext;
import java.util.ArrayList;
import java.util.List;

public class TopTwitchStreams {
    private final ApiContext context;

    public TopTwitchStreams(ApiContext context) {
        this.context = context;
    }

    public List<String> getTopGamesInfo() {
        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            GameTopList topGames = helix.getTopGames(null, null, null, "10").execute();
            return formatTopGamesInfo(topGames);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> formatTopGamesInfo(GameTopList topGames) {
        List<String> formattedTopGamesInfo = new ArrayList<>();
        StringBuilder gamesIDString = new StringBuilder();
        StringBuilder gamesNameString = new StringBuilder();
        StringBuilder gamesURLString = new StringBuilder();

        for (var game : topGames.getGames()) {
            gamesIDString.append(game.getId()).append("__");
            gamesNameString.append(game.getName()).append("__");

            String rawUrl = game.getBoxArtUrl();
            String fixedUrl = rawUrl
                    .replace("{width}", "180")
                    .replace("{height}", "240");

            gamesURLString.append(fixedUrl).append("__");
        }

        formattedTopGamesInfo.add(gamesIDString.toString());
        formattedTopGamesInfo.add(gamesNameString.toString());
        formattedTopGamesInfo.add(gamesURLString.toString());

        return formattedTopGamesInfo;
    }

    public List<String> getTopStreamsForCategoryInfo(String gameId){
        List<String> topStreamerUsernames = new ArrayList<>();
        if (gameId == null){
            TwitchHelix helix = context.twitchClient.getHelix();
            StreamList topStreams = helix.getStreams(null, null, null,
                    10, null, null, null, null).execute();
            List<String> userIds = topStreams.getStreams().stream()
                    .map(Stream::getUserId)
                    .toList();
            for (String user: userIds){
                topStreamerUsernames.add(getTwitchUsername(user));
            }
            return formatTopStreamsInfo(topStreams,topStreamerUsernames);
        }
        List<String> gameIDList = List.of(gameId);
        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            StreamList topStreams = helix.getStreams(null, null, null,
                    10, gameIDList, null, null, null).execute();
            List<String> userIds = topStreams.getStreams().stream()
                    .map(Stream::getUserId)
                    .toList();
            for (String user: userIds){
                topStreamerUsernames.add(getTwitchUsername(user));
            }
            return formatTopStreamsInfo(topStreams,topStreamerUsernames);
        } catch (Exception e) {
            return null;
        }
    }
    private List<String> formatTopStreamsInfo(StreamList topStreams,List<String> usernameList) {
        List<String> formattedTopStreamsInfo = new ArrayList<>();
        int i=0;
        for (var stream : topStreams.getStreams()) {
            String entry = String.join("__",
                    stream.getTitle(),
                    usernameList.get(i),
                    stream.getThumbnailUrl(170,96));
            i++;
            formattedTopStreamsInfo.add(entry);
        }
        return formattedTopStreamsInfo;
    }
    public String getTwitchUsername(String userID){
        TwitchHelix helix = context.twitchClient.getHelix();
        List<User> users = helix.getUsers(null, List.of(userID), null)
                .execute()
                .getUsers();
        if (!users.isEmpty()) {
            return users.get(0).getLogin();
        } else {
            return null;
        }
    }
}