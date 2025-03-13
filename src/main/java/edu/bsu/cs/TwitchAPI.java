package edu.bsu.cs;


import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.config.ProxyConfig;
import com.github.twitch4j.helix.domain.Video;
import com.github.twitch4j.helix.domain.VideoList;
import com.netflix.hystrix.HystrixCommand;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;


public class TwitchAPI {
    public static final String AUTHTOKEN = "x4bbracgz61vqbzgkccm7ed41evvjy";

    public static ITwitchClient twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true)
            .withDefaultAuthToken(new OAuth2Credential("twitch", AUTHTOKEN))
            .build();

    public static void main(String[] args) {
        twitchClient.getClientHelper().enableStreamEventListener("twitch4j");
        twitchClient.getClientHelper().enableFollowEventListener("twitch4j");
        final int[] i = {0};
        //Past 10 Stream Title API Example Test
        VideoList resultList = twitchClient.getHelix().getVideos(AUTHTOKEN, null, "641972806", null, null, null, null, null, 10, null, null).execute();
        System.out.println("\nStart of list");
        resultList.getVideos().forEach(video -> {
            i[0]++;
            System.out.println(i[0] + ". " + video.getTitle() + " - " + video.getUserName());
        });

    }

    @RequestLine("GET /videos?id={id}&user_id={user_id}&game_id={game_id}&language={language}&period={period}&sort={sort}&type={type}&after={after}&before={before}&first={first}")
    @Headers("Authorization: Bearer {token}")
    HystrixCommand<VideoList> getVideos(
            @Param("token") String authToken,
            @Param("id") List<String> id,
            @Param("user_id") String userId,
            @Param("game_id") String gameId,
            @Param("language") String language,
            @Param("period") Video.SearchPeriod period,
            @Param("sort") Video.SearchOrder sort,
            @Param("type") Video.Type type,
            @Param("first") Integer limit,
            @Param("after") String after,
            @Param("before") String before
    ) {
        return null;
    }
}
