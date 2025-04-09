package edu.bsu.cs.streamersearch;

import java.util.List;

public interface StreamerSearchProvider {
    List<String> searchStreamer(String username);
}
