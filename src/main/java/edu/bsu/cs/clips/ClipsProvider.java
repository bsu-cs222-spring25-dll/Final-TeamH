package edu.bsu.cs.clips;

import com.github.twitch4j.helix.domain.Clip;
import java.util.List;

public interface ClipsProvider {
    List<Clip> fetchClips(String username) throws Exception;
}
