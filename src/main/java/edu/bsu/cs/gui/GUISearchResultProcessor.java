package edu.bsu.cs.gui;

import java.util.List;

public class GUISearchResultProcessor {

    public String processResult(String platform, List<String> result) {
        if (result == null || result.isEmpty()) {
            System.out.println(platform + " streamer not found.");
            return "";
        }
        String foundName = result.get(0);
        System.out.println(platform + " streamer found: " + foundName);
        return foundName;
    }
}
