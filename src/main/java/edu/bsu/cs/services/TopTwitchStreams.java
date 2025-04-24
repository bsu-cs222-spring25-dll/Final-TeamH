package edu.bsu.cs.services;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.GameTopList;
import com.github.twitch4j.helix.domain.StreamList;
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
        for (var game: topGames.getGames()) {
            gamesIDString.append(game.getId()).append("__");
            gamesNameString.append(game.getName()).append("__");
            gamesURLString.append(game.getBoxArtUrl(142,189)).append("__");
        }
        formattedTopGamesInfo.add(String.valueOf(gamesIDString));
        formattedTopGamesInfo.add(String.valueOf(gamesNameString));
        formattedTopGamesInfo.add(String.valueOf(gamesURLString));
        return formattedTopGamesInfo;
    }

    public void getTopStreamsForCategory(String gameId){
        List<String> gameIDList = new ArrayList<>();
        gameIDList.add(gameId);
        try {
            TwitchHelix helix = context.twitchClient.getHelix();
            StreamList topStreams = helix.getStreams(null, null, null,
                    5, gameIDList, null, null, null).execute();
            for (var stream: topStreams.getStreams()) {
                System.out.println("ID: " + stream.getId() + " - Title: " + stream.getTitle()+ " - Game: " + stream.getGameId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}